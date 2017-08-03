package crossValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import model.DataInLink;

import org.apache.commons.lang3.StringUtils;
import org.ejml.simple.SimpleMatrix;

import training.Training;

public class CrossValidation {

	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();

	public static Function<DataInLink,Double> getModel(List<DataInLink> dataInLinks) throws InterruptedException{
		
		int maxOrder=5;
		int foldTime=10;

		//convert data high dimension
		System.out.println("converting data high dimension");
		List<LinkedList<Double>> fullDataSetWithDimension= transferData(maxOrder,dataInLinks);
		System.out.println("converted data high dimension");
		
		
		//from low dimension to high
		System.out.println("Start training");
		List<Thread> trainingTs=new ArrayList<Thread>();
		for(int count=0;count<fullDataSetWithDimension.size();count++){
			List<LinkedList<Double>> fullDataSet=new ArrayList<>();
            for(int index=0;index<=count;index++){
            	fullDataSet.add(fullDataSetWithDimension.get(index));
            }
            System.out.println("Got full data with order: " +StringUtils.join(getOrders(maxOrder,count),","));
        	Thread training=new Thread(new Training(count,foldTime,errorMap,weightMap,fullDataSet));
			trainingTs.add(training);
			System.out.println("Start training of order: " +StringUtils.join(getOrders(maxOrder,count),","));
			training.start();
        }
		
		
		for(Thread training:trainingTs){
			training.join();
		}
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
		double[] weights=weightMap.get(minCount).getMatrix().getData();
		System.out.println("Order of min error:  " +StringUtils.join(orders,"#"));
		System.out.println("Weight of min error: "+ StringUtils.join(weights,"#"));
		System.out.println(minCount);
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
	
	
	public static SimpleMatrix genTargetFunWeidth(List<LinkedList<Double>> res, SimpleMatrix Y){
		
		SimpleMatrix X=new SimpleMatrix(res.get(0).size(),res.size());
		
		int colNum=0;
		for(LinkedList<Double> col:res){
			X.setColumn(colNum, 0,col.stream().mapToDouble(d -> d).toArray());
			colNum++;
		}
		
		SimpleMatrix W=X.pseudoInverse().mult(Y);
		Arrays.asList(W.getMatrix().data);
		return W;
		
	}
	public static List<LinkedList<Double>>  transferData(int maxOrder,final List<DataInLink> dataInLinks) throws InterruptedException{
		List<LinkedList<Double>> res=new ArrayList<LinkedList<Double>>();
		DataInLink dataInLink;
		LinkedList<Double> oneColData;
		
		//init list
		
		List<Thread> initOneColTs=new ArrayList<>();
		for(int order=0;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							oneColData=new LinkedList<>();
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
		LinkedList<Double> Y=new LinkedList<Double>();
		for(int index=0;index<dataInLinks.size();index++){
			dataInLink=dataInLinks.get(index);
			Y.add(dataInLink.getTravle_time());
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
	
	private LinkedList<Double> oneColData;
	private List<DataInLink> dataInLinks;
	private int lengthO;
	private int widthO;
	private int classO;
	private int weightO;
	private int startTimeO;
	

	@Override
	public void run() {
		
		oneColData=new LinkedList<>();
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			oneColData.add(CrossValidation.caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
		}
		
	}


	public InitOneCol(LinkedList<Double> oneColData,
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