package controll;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import calculateFeatures.CalculateFeatures;
import calculateFeatures.Convert2STD;
import crossValidation.CrossValidation;
import importData.ReadAsLink;
import importData.ReadAsTrainData;
import model.DataInLink;
import model.Link;
import outputData.WriteData;
import testData.Testing;

public class App 
{
	private static Logger log = Logger.getLogger(App.class);  
    public static void main( String[] args ) throws ParseException, InterruptedException
    {
    	
    	log.info(" ########################  STRAT  ######################## ");
    	DateFormat df = new SimpleDateFormat("yyyyMMMdd", Locale.ENGLISH);
    	Calendar  startTime=Calendar.getInstance();
    	
    	
    	Map<String,Link> linksMap=ReadAsLink.readAsLink();
    	log.info("Read link info # total : " + linksMap.size());
    	
    	
    	CalculateFeatures.calculateFeaturesOfLinks(linksMap);
    	log.info("Calculated weight of link # total : " + linksMap.size());
    	
    	
    	log.info("Reading training data..." );
    	List<DataInLink> dataInLinks=ReadAsTrainData.readAsTrainData(linksMap);
    	log.info("Read training data # total : " + dataInLinks.size());

    	if(Constant.SIZE_OF_TRAINING_DATA!=-1 && Constant.SIZE_OF_TRAINING_DATA<dataInLinks.size()){
    		log.info("Pick training data # total : " + Constant.SIZE_OF_TRAINING_DATA);
    		dataInLinks=dataInLinks.subList(0, Constant.SIZE_OF_TRAINING_DATA);
    	}
    	
    	Convert2STD.convert2STD(dataInLinks);
    	log.info("Got some componets for normalization.");
    	
    	System.gc();
    	
    	log.info("Getting testing data set ...");
    	List<DataInLink> testDataSet = new LinkedList<>();
    	Thread repareTestingT=new Thread(new RepareTestingData(linksMap,testDataSet));
    	repareTestingT.start();
    	
    	
    	log.info("Training model... ");
    	Function<DataInLink,Double> targetFunction=CrossValidation.getModel(dataInLinks);
    	log.info("Got target function.");
    	
    	System.gc();

    	
    	repareTestingT.join();
    	Testing.testing(targetFunction,testDataSet);
    	log.info("Test result generated.");
    	
    	
    	String today= df.format(Calendar.getInstance().getTime());
    	String resultPath=MessageFormat.format(Constant.PATH_OF_RESULT,today,String.format("%.6f", CrossValidation.minError));
    	log.info("Writing data to specified path - "+resultPath);
    	WriteData.contentToTxt(resultPath, StringUtils.join(testDataSet, "\n")+"\n");
    	
    	
    	log.info("Done! Used " + getUsedTime(startTime,Calendar.getInstance()));
    }
    
    private static String getUsedTime(Calendar startTimeC,Calendar endTimeC){
    	long startTime=startTimeC.getTimeInMillis();
    	long endTime=endTimeC.getTimeInMillis();
    	long usedDays= (long)((endTime - startTime)/(1000 * 60 * 60 *24) + 0.5); 
    	long usedHours=(long)(((endTime - startTime)%(1000 * 60 * 60 *24) + 0.5))/(1000 * 60 * 60); 
    	long usedMinus=(long)(((endTime - startTime)%(1000 * 60 * 60 *24) + 0.5))% (1000 * 60 * 60)/(1000 * 60);
    	long usedSec=(long)(((endTime - startTime)%(1000 * 60 * 60 *24) + 0.5))% (1000 * 60 * 60)%(1000 * 60)/1000;
    	return "days:" + usedDays+" hours:" + usedHours+" minus:" + usedMinus+" seconds:" + usedSec;
    }
}

class RepareTestingData implements Runnable{
	private static Logger log = Logger.getLogger(App.class);
	
	private Map<String,Link> linksMap;
	private List<DataInLink> testDataSet;

	
	public RepareTestingData(Map<String, Link> linksMap, List<DataInLink> testDataSet) {
		super();
		this.linksMap = linksMap;
		this.testDataSet = testDataSet;
	}


	@Override
	public void run() {
    	Testing.getTestDataSet(linksMap,testDataSet);
//    	Testing.getTestDataSetOfFirstLink(linksMap,testDataSet);
    	Convert2STD.convert2STD(testDataSet);
    	log.info("Generated testing data set # total : " + testDataSet.size());
		
	}
	
}