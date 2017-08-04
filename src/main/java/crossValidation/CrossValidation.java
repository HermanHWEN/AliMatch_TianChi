package crossValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import model.DataInLink;

import org.apache.commons.lang3.StringUtils;
import org.ejml.simple.SimpleMatrix;

import importData.Constant;
import training.Training;

public class CrossValidation {

	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks) throws InterruptedException{

		int maxOrder=5;
		int foldTime=10;

		//convert data high dimension
		System.out.println("converting data high dimension");
		List<double[]> fullDataSetWithDimension= transferData(maxOrder,dataInLinks);
		System.out.println("converted data high dimension");


		//from low dimension to high
		System.out.println("Start training");
		for(int count=0;count<fullDataSetWithDimension.size()-1;count++){
			List<double[]> fullDataSet=new ArrayList<>();
			for(int index=0;index<=count;index++){
				fullDataSet.add(fullDataSetWithDimension.get(index));
			}
			fullDataSet.add(fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
			Constant.getThreadPoolExecutor().execute(new Training(count,foldTime,errorMap,weightMap,fullDataSet));
			System.out.println("Start training of order: " +StringUtils.join(getOrdersStr(maxOrder,count),","));
		}
		Constant.getThreadPoolExecutor().shutdown();

		while(!Constant.getThreadPoolExecutor().isTerminated()){}

		System.out.println("All trainings completed.");

		//get min error
		int minCount=0;
		double minError=Double.MAX_VALUE;
		for(int count=0;count<getCounts(maxOrder);count++){
			if(errorMap.get(count)!=null){
				if(errorMap.get(count)<minError){
					minCount=count;
					minError=errorMap.get(count);
				}
			}
		}

		List<OrdersOfVars> orders=getOrders(maxOrder,minCount);
		List<String> ordersStr=getOrdersStr(maxOrder,minCount);
		double[] weights=weightMap.get(minCount).getMatrix().getData();
		System.out.println("Order of min error:  " +StringUtils.join(ordersStr,"#"));
		System.out.println("Weight of min error: "+ Arrays.toString(weights));
		System.out.println("minCount : " + minCount);
		System.out.println("Min error: " + minError/10);

		Function<DataInLink,Double> targetFunction = (x) -> {
			double y=0;
			for(int index=0;index<orders.size();++index){
				OrdersOfVars order=orders.get(index);
				double weight=weights[index];
				y+=caclulateWithOrder(x,order.getLengthO(),order.getWidthO(),order.getClassO(),order.getWeightO(),order.getDateO(),order.getStartTimeO())*weight;
			}
			return y;
		};  

		return targetFunction;
	}


	public static List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks) throws InterruptedException{
		List<double[]> res=new LinkedList<double[]>();
		DataInLink dataInLink;

		//init list

		List<OrdersOfVars> orders=getOrders(maxOrder,-1);
		
		for(OrdersOfVars order: orders){
			double[] oneColData=new double[dataInLinks.size()];
			res.add(oneColData);
			Constant.getThreadPoolExecutor().execute(new InitOneCol(oneColData,dataInLinks,order.getLengthO(),order.getWidthO(),order.getClassO(),order.getWeightO(),order.getDateO(),order.getStartTimeO()));
		}

		//Y
		double[] Y=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			dataInLink=dataInLinks.get(index);
			Y[index]=dataInLink.getTravle_time();
		}
		res.add(Y);

		while(!Constant.getThreadPoolExecutor().isTerminated()){}
		return res;

	}

	static synchronized double caclulateWithOrder(final DataInLink dataInLink,int lengthO,int widthO,int classO,int weightO,int dateO,int startTimeO){
		if(dataInLink==null) return 0;
		double reswithOrder=Math.pow(dataInLink.getLink().getLength(), lengthO)*
				Math.pow(dataInLink.getLink().getWidth(), widthO)*
				Math.pow(dataInLink.getLink().getLink_class(), classO)*
				Math.pow(dataInLink.getLink().getWeight(), weightO)*
				Math.pow(dataInLink.getDate().getDate(), dateO)*
				Math.pow(dataInLink.getStartTime().getHours()*60+dataInLink.getStartTime().getMinutes(), startTimeO);

		return reswithOrder;
	}
	
	private static List<String> getOrdersStr(int maxOrder,int minCount){
		List<String> strRes=new ArrayList<>();
		List<OrdersOfVars> res=getOrders(maxOrder,minCount);
		for(OrdersOfVars r:res){
			strRes.add(r.toString());
		}
		return strRes;
	}

	private static List<OrdersOfVars> getOrders(int maxOrder,int minCount){
		List<OrdersOfVars> ordersOfVarsList = new ArrayList<>();
		int count=0;
		for(int order=0;order<=maxOrder;order++){

			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							for(int dateO=order-lengthO-widthO-classO-weightO;dateO>=0;dateO--){
								int startTimeO=order-lengthO-widthO-classO-weightO-dateO;
								OrdersOfVars ordersOfVars=new OrdersOfVars();
								ordersOfVars.setLengthO(lengthO);
								ordersOfVars.setWidthO(widthO);
								ordersOfVars.setClassO(classO);
								ordersOfVars.setWeightO(weightO);
								ordersOfVars.setDateO(dateO);
								ordersOfVars.setStartTimeO(startTimeO);
								ordersOfVarsList.add(ordersOfVars);
								count++;
								if(count>minCount && minCount!=-1) return ordersOfVarsList;
							}

						}
					}
				}
			}
		}
		return ordersOfVarsList;

	}

	private static int getCounts(int maxOrder){
		return getOrders(maxOrder,-1).size()-1;
	}
}


class InitOneCol implements Runnable{

	private double[] oneColData;
	private List<DataInLink> dataInLinks;
	private int lengthO;
	private int widthO;
	private int classO;
	private int weightO;
	private int dateO;
	private int startTimeO;


	@Override
	public void run() {
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			oneColData[index]=CrossValidation.caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO,dateO, startTimeO);
		}
	}


	public InitOneCol(double[] oneColData,
			List<DataInLink> dataInLinks, int lengthO, int widthO, int classO,
			int weightO,int dateO, int startTimeO) {
		super();
		this.oneColData = oneColData;
		this.dataInLinks = dataInLinks;
		this.lengthO = lengthO;
		this.widthO = widthO;
		this.classO = classO;
		this.dateO = dateO;
		this.weightO = weightO;
		this.startTimeO = startTimeO;
	}



}