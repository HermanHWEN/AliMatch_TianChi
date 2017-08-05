package crossValidation;

import importData.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import model.DataInLink;

import org.apache.commons.lang3.StringUtils;
import org.ejml.simple.SimpleMatrix;

import training.Training;

public class CrossValidation {

	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks) throws InterruptedException{

		//convert data high dimension
		System.out.println("converting data high dimension");
		List<double[]> fullDataSetWithDimension= transferData(Constant.MAXORDER,dataInLinks);
		System.out.println("converted data high dimension");


		//from low dimension to high
		ThreadPoolExecutor threadPoolExecutor=Constant.getThreadPoolExecutor();
		System.out.println("Start training");
		for(int count=0;count<fullDataSetWithDimension.size()-1;count++){
			List<double[]> fullDataSet=new ArrayList<>();
			for(int index=0;index<=count;index++){
				fullDataSet.add(fullDataSetWithDimension.get(index));
			}
			fullDataSet.add(fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
			System.out.println("Start training with "+(count+1)+" parameters and powered by orders: " +StringUtils.join(OrdersOfVars.getOrdersStr(Constant.MAXORDER,count),","));
			threadPoolExecutor.execute(new Training(count,Constant.FOLDTIME,errorMap,weightMap,fullDataSet));
//			new Thread(new Training(count,Constant.FOLDTIME,errorMap,weightMap,fullDataSet)).run();
		}
		threadPoolExecutor.shutdown();

		while(!threadPoolExecutor.isTerminated()){}
		System.out.println("All trainings completed.");

		
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

		
		//print result
		List<OrdersOfVars> orders=OrdersOfVars.getOrders(Constant.MAXORDER,minCount);
		List<String> ordersStr=OrdersOfVars.getOrdersStr(Constant.MAXORDER,minCount);
		double[] weights=weightMap.get(minCount).getMatrix().getData();
		System.out.println("Order of min error:  " +StringUtils.join(ordersStr,"#"));
		System.out.println("Weight of min error: "+ Arrays.toString(weights));
		System.out.println("minCount : " + minCount);
		System.out.println("Min error: " + minError);

		
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
		List<double[]> res=new LinkedList<double[]>();
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