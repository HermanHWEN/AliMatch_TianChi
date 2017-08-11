package crossValidation;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import model.DataInLink;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import outputData.WriteData;
import training.Training;
import controll.Constant;

public class CrossValidation {
	private static Logger log = Logger.getLogger(CrossValidation.class); 
	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();
	public static double minError;

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks,Map<String,DataInLink> dataInLinksMap) throws InterruptedException{

		//convert data high dimension
		log.info("converting data high dimension");
		List<double[]> fullDataSetWithDimension= transferData(Constant.MAXORDER,dataInLinks,dataInLinksMap);
		log.info("converted data high dimension");


		//from low dimension to high
		ThreadPoolExecutor threadPoolExecutor=Constant.getThreadPoolExecutor();
		log.info("Start training");
		int startNumOfParams=Constant.NUM_OF_PARAMS_OF_START==-1?0:Constant.NUM_OF_PARAMS_OF_START-1;
		int endNumOfParams=(Constant.NUM_OF_PARAMS_OF_END==-1 || (fullDataSetWithDimension.size()-1)<Constant.NUM_OF_PARAMS_OF_END)?(fullDataSetWithDimension.size()-1):Constant.NUM_OF_PARAMS_OF_END;
		for(int parametersNum=startNumOfParams;parametersNum<endNumOfParams;parametersNum++){
			List<double[]> fullDataSet=new ArrayList<>();
			for(int index=0;index<=parametersNum;index++){
				fullDataSet.add(fullDataSetWithDimension.get(index));
			}
			fullDataSet.add(fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
			log.debug("Start training with "+StringUtils.repeat(" ", 3-String.valueOf((parametersNum+1)).length())+(parametersNum+1)+" parameters and powered by orders: " +StringUtils.join(OrdersOfVars.getOrdersStr(Constant.MAXORDER,parametersNum,dataInLinks.get(0).getStandardDeviation()),","));
			if(Constant.USE_MULTI_THREAD_FOR_TRAINING){
				threadPoolExecutor.execute(new Training(parametersNum,Constant.FOLDTIME,errorMap,weightMap,fullDataSet,dataInLinksMap));
			}else{
				new Thread(new Training(parametersNum,Constant.FOLDTIME,errorMap,weightMap,fullDataSet,dataInLinksMap)).run();
			}
		}
		
		if(Constant.USE_MULTI_THREAD_FOR_TRAINING){
			threadPoolExecutor.shutdown();
			while(!threadPoolExecutor.isTerminated()){}
		}
		log.info("All trainings completed.");

		
		//get min error
		int minParametersNum=0;
		double minError=Double.MAX_VALUE;
		for(int parametersNum=0;parametersNum<OrdersOfVars.getParametersNum(Constant.MAXORDER,dataInLinks.get(0).getStandardDeviation());parametersNum++){
			if(errorMap.get(parametersNum)!=null){
				if(BigDecimal.valueOf(errorMap.get(parametersNum)).setScale(Constant.ACURACY_OF_ERROR_FORWARD, BigDecimal.ROUND_HALF_UP).doubleValue()<=BigDecimal.valueOf(minError).setScale(Constant.ACURACY_OF_ERROR_FORWARD, BigDecimal.ROUND_HALF_UP).doubleValue()){
					minParametersNum=parametersNum;
					minError=errorMap.get(parametersNum);
				}
			}
		}

		CrossValidation.minError=minError;
		//print result
		List<OrdersOfVars> orders=OrdersOfVars.getOrders(Constant.MAXORDER,minParametersNum,dataInLinks.get(0).getStandardDeviation());
		List<String> ordersStr=OrdersOfVars.getOrdersStr(Constant.MAXORDER,minParametersNum,dataInLinks.get(0).getStandardDeviation());
		double[] weights=weightMap.get(minParametersNum).getMatrix().getData();
		
		StringBuffer modelInfo= new StringBuffer("Model info:\n");
		modelInfo.append("Number of samples:  " +dataInLinks.size()+"\n");
		modelInfo.append("Order of min error:  " +StringUtils.join(ordersStr,"+")+"\n");
		modelInfo.append("Weight of min error: "+ Arrays.toString(weights)+"\n");
		modelInfo.append("Parameters Num : " + (minParametersNum+1)+"\n");
		modelInfo.append("Min error: " + minError+"\n");
		log.info(modelInfo);
		
		String modelPath=MessageFormat.format(Constant.PATH_OF_MODEL,new SimpleDateFormat("yyyyMMMdd", Locale.ENGLISH).format(Calendar.getInstance().getTime()),String.format("%.6f", minError));
		WriteData.contentToTxt(modelPath, modelInfo.toString());

		
		//get target function
		Function<DataInLink,Double> targetFunction = (x) -> {
			double y=0;
			for(int index=0;index<orders.size();++index){
				OrdersOfVars order=orders.get(index);
				double weight=weights[index];
				y+=x.powerWithOrders(order,dataInLinksMap)*weight;
			}
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(x.getStartTime());
			dataInLinksMap.put(x.getLink().getLink_ID()
					+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DATE)
					+calendar.get(Calendar.HOUR)+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND), x);
			return Math.abs(y);
		};  

		return targetFunction;
	}


	public static List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks,Map<String,DataInLink> dataInLinksMap) throws InterruptedException{
		List<double[]> res=new ArrayList<double[]>();
		DataInLink dataInLink;
		ThreadPoolExecutor threadPoolExecutor=Constant.getThreadPoolExecutor();
		//init list

		List<OrdersOfVars> orders=OrdersOfVars.getOrders(maxOrder,-1,dataInLinks.get(0).getStandardDeviation());
		
		for(OrdersOfVars order: orders){
			double[] oneColData=new double[dataInLinks.size()];
			res.add(oneColData);
			threadPoolExecutor.execute(new InitOneCol(oneColData,dataInLinks,order,dataInLinksMap));
		}

		threadPoolExecutor.shutdown();
		//Y
		double[] Y=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			dataInLink=dataInLinks.get(index);
			Y[index]=dataInLink.getTravle_time();
		}
		res.add(Y);

		while(!threadPoolExecutor.isTerminated()){}
		return res;

	}

	
}


class InitOneCol implements Runnable{

	private double[] oneColData;
	private List<DataInLink> dataInLinks;
	private OrdersOfVars order;
	private Map<String,DataInLink> dataInLinksMap;

	@Override
	public void run() {
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			if(dataInLink!=null){
				oneColData[index]=dataInLink.powerWithOrders(order,dataInLinksMap);
			}
		}
	}

	public InitOneCol(double[] oneColData, List<DataInLink> dataInLinks, OrdersOfVars order,Map<String,DataInLink> dataInLinksMap) {
		super();
		this.oneColData = oneColData;
		this.dataInLinks = dataInLinks;
		this.order = order;
		this.dataInLinksMap=dataInLinksMap;
	}

}