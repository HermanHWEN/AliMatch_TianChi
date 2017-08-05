package crossValidation;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import model.DataInLink;
import outputData.WriteData;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import calculateFeatures.CalculateFeatures;
import controll.Constant;
import training.Training;

public class CrossValidation {
	private static Logger log = Logger.getLogger(CrossValidation.class); 
	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();
	public static double minError;

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks) throws InterruptedException{

		//convert data high dimension
		log.info("converting data high dimension");
		List<double[]> fullDataSetWithDimension= transferData(Constant.MAXORDER,dataInLinks);
		log.info("converted data high dimension");


		//from low dimension to high
		ThreadPoolExecutor threadPoolExecutor=Constant.getThreadPoolExecutor();
		log.info("Start training");
		for(int count=0;count<fullDataSetWithDimension.size()-1;count++){
			List<double[]> fullDataSet=new ArrayList<>();
			for(int index=0;index<=count;index++){
				fullDataSet.add(fullDataSetWithDimension.get(index));
			}
			fullDataSet.add(fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
			log.info("Start training with "+(count+1)+" parameters and powered by orders: " +StringUtils.join(OrdersOfVars.getOrdersStr(Constant.MAXORDER,count),","));
			if(Constant.USE_MULTI_THREAD_FOR_TRAINING){
				threadPoolExecutor.execute(new Training(count,Constant.FOLDTIME,errorMap,weightMap,fullDataSet));
			}else{
				new Thread(new Training(count,Constant.FOLDTIME,errorMap,weightMap,fullDataSet)).run();
			}
		}
		
		if(Constant.USE_MULTI_THREAD_FOR_TRAINING){
			threadPoolExecutor.shutdown();
			while(!threadPoolExecutor.isTerminated()){}
		}
		log.info("All trainings completed.");

		
		//get min error
		int minCount=0;
		double minError=Double.MAX_VALUE;
		for(int count=0;count<OrdersOfVars.getCounts(Constant.MAXORDER);count++){
			if(errorMap.get(count)!=null){
				if(errorMap.get(count)<minError){
					minCount=count;
					minError=errorMap.get(count);
				}
			}
		}

		CrossValidation.minError=minError;
		//print result
		List<OrdersOfVars> orders=OrdersOfVars.getOrders(Constant.MAXORDER,minCount);
		List<String> ordersStr=OrdersOfVars.getOrdersStr(Constant.MAXORDER,minCount);
		double[] weights=weightMap.get(minCount).getMatrix().getData();
		
		StringBuffer modelInfo= new StringBuffer("Model info:\n");
		modelInfo.append("Number of samples:  " +dataInLinks.size()+"\n");
		modelInfo.append("Order of min error:  " +StringUtils.join(ordersStr,"#")+"\n");
		modelInfo.append("Weight of min error: "+ Arrays.toString(weights)+"\n");
		modelInfo.append("MinCount : " + minCount+"\n");
		modelInfo.append("Min error: " + minError+"\n");
		log.info(modelInfo);
		
		String modelPath=MessageFormat.format(Constant.PATH_OF_MODEL,new SimpleDateFormat("yyyyMMMdd", Locale.ENGLISH).format(Calendar.getInstance().getTime()),String.format(".6", minError));
		WriteData.contentToTxt(modelPath, modelInfo.toString());

		
		//get target function
		Function<DataInLink,Double> targetFunction = (x) -> {
			double y=0;
			for(int index=0;index<orders.size();++index){
				OrdersOfVars order=orders.get(index);
				double weight=weights[index];
				y+=x.powerWithOrders(order)*weight;
			}
//			return y;
			return Math.abs(y);
		};  

		return targetFunction;
	}


	public static List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks) throws InterruptedException{
		List<double[]> res=new ArrayList<double[]>();
		DataInLink dataInLink;
		ThreadPoolExecutor threadPoolExecutor=Constant.getThreadPoolExecutor();
		//init list

		List<OrdersOfVars> orders=OrdersOfVars.getOrders(maxOrder,-1);
		
		for(OrdersOfVars order: orders){
			double[] oneColData=new double[dataInLinks.size()];
			res.add(oneColData);
			threadPoolExecutor.execute(new InitOneCol(oneColData,dataInLinks,order));
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


	@Override
	public void run() {
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			if(dataInLink!=null){
				oneColData[index]=dataInLink.powerWithOrders(order);
			}
		}
	}

	public InitOneCol(double[] oneColData, List<DataInLink> dataInLinks, OrdersOfVars order) {
		super();
		this.oneColData = oneColData;
		this.dataInLinks = dataInLinks;
		this.order = order;
	}

}