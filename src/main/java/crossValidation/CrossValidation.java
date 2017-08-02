package crossValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
		
		double error=0;
		
		//Separate data into training set and validation set
		int size=dataInLinks.size();
		int step=size/10+1;
		List<Thread> trainingTs=new ArrayList<Thread>();
		
		for(int start=0;start<size;start+=step){
			List<DataInLink> validationSet=new ArrayList<DataInLink>();
			List<DataInLink> trainingSet=new ArrayList<DataInLink>();
			
			for(int index=0;index<size;++index){
				
				
				//pick validation set
				if(index>start && index<start+step && index<size){
					validationSet.add(dataInLinks.get(index));
				}else{//there is training set
					trainingSet.add(dataInLinks.get(index));
				}
			}
			Thread training=new Thread(new Training(trainingSet,validationSet,errorMap,weightMap));
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
							int startTimeO=order-lengthO-widthO-classO-weightO;
							res++;
						}
					}
				}
			}
		}
		return res;
	}
	
	
	public static SimpleMatrix genTargetFunWeidth(List<double[]> res, SimpleMatrix Y){
		
		SimpleMatrix X=new SimpleMatrix(res.get(0).length,res.size());
		
		int colNum=0;
		for(double[] col:res){
			X.setColumn(colNum, 0,col);
			colNum++;
		}
		
		SimpleMatrix W=X.pseudoInverse().mult(Y);
		Arrays.asList(W.getMatrix().data);
		return W;
		
	}
	public static void setY(SimpleMatrix Y, List<DataInLink> dataInLinks){
		
		for(DataInLink dataInLink:dataInLinks){
			
			int offset=0;
			Y.setColumn(0, 0, dataInLink.getTravle_time());
			offset++;
		}
		
	}
}
