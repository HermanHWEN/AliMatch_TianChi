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

import training.Training;

public class CrossValidation {

	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks) throws InterruptedException{
		BlockingQueue queue = new LinkedBlockingQueue();   
		ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(4,20,Long.MAX_VALUE,TimeUnit.DAYS, queue);
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
            threadPoolExecutor.execute(new Training(count,foldTime,errorMap,weightMap,fullDataSet));
			System.out.println("Start training of order: " +StringUtils.join(getOrdersStr(maxOrder,count),","));
        }
		threadPoolExecutor.shutdown();
		
		while(!threadPoolExecutor.isTerminated()){}
		
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
		
		List<int[]> orders=getOrders(maxOrder,minCount);
		List<String> ordersStr=getOrdersStr(maxOrder,minCount);
		double[] weights=weightMap.get(minCount).getMatrix().getData();
		System.out.println("Order of min error:  " +StringUtils.join(ordersStr,"#"));
		System.out.println("Weight of min error: "+ Arrays.toString(weights));
		System.out.println("minCount : " + minCount);
		System.out.println("Min error: " + minError/10);
		
		Function<DataInLink,Double> targetFunction = (x) -> {
			double y=0;
			for(int index=0;index<orders.size();++index){
				int[] order=orders.get(index);
				double weight=weights[index];
				y+=caclulateWithOrder(x,order[0],order[1],order[2],order[3],order[4])*weight;
			}
			return y;
			};  
		
		return targetFunction;
		
	}

	private static List<String> getOrdersStr(int maxOrder,int minCount){
		List<String> strRes=new ArrayList<>();
		List<int[]> res=getOrders(maxOrder,minCount);
		for(int[] r:res){
			strRes.add(Arrays.toString(r));
		}
		return strRes;
	}

	private static List<int[]> getOrders(int maxOrder,int minCount){
		List<int[]> res=new ArrayList<int[]>();
		int count=0;
		
		//init res
		for(int order=0;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							int[] orderI=new int[]{lengthO,widthO,classO,weightO,startTimeO};
							count++;
							res.add(orderI);
							if(count>minCount) return res;
						}
					}
				}
			}
		}
		return res;
	}
	
	private static int getCounts(int maxOrder){
		int res=0;
		
		//init res
		for(int order=0;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							res++;
						}
					}
				}
			}
		}
		return res;
	}
	
	
	public static List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks) throws InterruptedException{
		List<double[]> res=new LinkedList<double[]>();
		DataInLink dataInLink;
		
		//init list
		
		List<Thread> initOneColTs=new ArrayList<>();
		for(int order=0;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							double[] oneColData=new double[dataInLinks.size()];
							res.add(oneColData);
							Thread initOneColT=new Thread(new InitOneCol(oneColData,dataInLinks,lengthO,widthO,classO,weightO,startTimeO));
							initOneColTs.add(initOneColT);
							initOneColT.start();
						}
					}
				}
			}
		}
		
		//Y
		double[] Y=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			dataInLink=dataInLinks.get(index);
			Y[index]=dataInLink.getTravle_time();
		}
		res.add(Y);
		
		for(Thread initOneColT: initOneColTs){
			initOneColT.join();
		}
		
		return res;
		
	}
	
	static synchronized double caclulateWithOrder(final DataInLink dataInLink,int lengthO,int widthO,int classO,int weightO,int startTimeO){
		if(dataInLink==null) return 0;
		double reswithOrder=Math.pow(dataInLink.getLink().getLength(), lengthO)*
		Math.pow(dataInLink.getLink().getWidth(), widthO)*
		Math.pow(dataInLink.getLink().getLink_class(), classO)*
		Math.pow(dataInLink.getLink().getWeight(), weightO)*
		Math.pow(dataInLink.getStartTime().getHours()*60+dataInLink.getStartTime().getMinutes(), startTimeO);
		
		return reswithOrder;
	}
}


class InitOneCol implements Runnable{
	
	private double[] oneColData;
	private List<DataInLink> dataInLinks;
	private int lengthO;
	private int widthO;
	private int classO;
	private int weightO;
	private int startTimeO;
	

	@Override
	public void run() {
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			oneColData[index]=CrossValidation.caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO);
		}
	}


	public InitOneCol(double[] oneColData,
			List<DataInLink> dataInLinks, int lengthO, int widthO, int classO,
			int weightO, int startTimeO) {
		super();
		this.oneColData = oneColData;
		this.dataInLinks = dataInLinks;
		this.lengthO = lengthO;
		this.widthO = widthO;
		this.classO = classO;
		this.weightO = weightO;
		this.startTimeO = startTimeO;
	}


	
}