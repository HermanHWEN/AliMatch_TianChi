package crossValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.DataInLink;

import org.apache.commons.lang3.StringUtils;
import org.ejml.simple.SimpleMatrix;

import training.Training;

public class CrossValidation {

	static Map<Integer,Double> errorMap=new HashMap<Integer,Double>();
	static Map<Integer,SimpleMatrix> weightMap=new HashMap<Integer,SimpleMatrix>();

	public static Map<Integer,Double> errors(List<DataInLink> dataInLinks) throws InterruptedException{
		
		int maxOrder=5;
		int foldTime=10;

		//convert data high dimension
		System.out.println("converting data high dimension");
		List<LinkedList<Double>> fullDataSetWithDimension= transferData(maxOrder,dataInLinks);
		
		
		//from low dimension to high
		List<Thread> trainingTs=new ArrayList<Thread>();
		for(int count=0;count<fullDataSetWithDimension.size();count++){
			List<LinkedList<Double>> fullDataSet=new ArrayList<>();
            for(int index=0;index<=count;index++){
            	fullDataSet.add(fullDataSetWithDimension.get(index));
            }
        	Thread training=new Thread(new Training(count,foldTime,errorMap,weightMap,fullDataSet));
			trainingTs.add(training);
			training.start();
        }
		
		
		for(Thread training:trainingTs){
			training.join();
		}
		
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
		
		System.out.println("orders: " +StringUtils.join(getOrders(maxOrder,minCount),","));
		System.out.println("weight: "+ StringUtils.join(weightMap.get(minCount).getMatrix().getData(),","));
		System.out.println(minCount);
		System.out.println("error: " + minError/10);
		
		return errorMap;
		
	}


	private static List<String> getOrders(int maxOrder,int minCount){
		List<String> res=new ArrayList<String>();
		int count=0;
		
		//init res
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							count++;
							res.add(""+lengthO+widthO+classO+weightO+startTimeO);
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
		for(int order=1;order<=maxOrder;order++){
			
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
	public static List<LinkedList<Double>>  transferData(int maxOrder,final List<DataInLink> dataInLinks){
		List<LinkedList<Double>> res=new ArrayList<LinkedList<Double>>();
		DataInLink dataInLink;
		LinkedList<Double> oneColData;
		//get result
		
		//constant col
		LinkedList<Double> constants=new LinkedList<Double>();
		for(int index=0;index<dataInLinks.size();index++){
			constants.add((double) 1);
		}
		res.add(constants);
		
		//tranfered columns
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							oneColData=new LinkedList<>();
							for(int index=0;index<dataInLinks.size();index++){
								dataInLink=dataInLinks.get(index);
								oneColData.add(caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
							}
							res.add(oneColData);
						}
					}
				}
			}
		}
		
		//Y
		//constant col
		LinkedList<Double> Y=new LinkedList<Double>();
		for(int index=0;index<dataInLinks.size();index++){
			dataInLink=dataInLinks.get(index);
			Y.add(dataInLink.getTravle_time());
		}
		res.add(Y);
		return res;
		
	}
	
	private static double caclulateWithOrder(final DataInLink dataInLink,int lengthO,int widthO,int classO,int weightO,int startTimeO){
		if(dataInLink==null) return 0;
		double reswithOrder=Math.pow(dataInLink.getLink().getLength(), lengthO)*
		Math.pow(dataInLink.getLink().getWidth(), widthO)*
		Math.pow(dataInLink.getLink().getLink_class(), classO)*
		Math.pow(dataInLink.getLink().getWeight(), weightO)*
		Math.pow(dataInLink.getStartTime().getHours()*60+dataInLink.getStartTime().getMinutes(), startTimeO);
		
		return reswithOrder;
	}
}
