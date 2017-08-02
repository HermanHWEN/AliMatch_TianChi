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

		//convert data high dimension
		List<double[]> fullDataSetWithDimension= transferData(5,dataInLinks);
		
		
		//from low dimension to high
		List<Thread> trainingTs=new ArrayList<Thread>();
		for(int count=0;count<fullDataSetWithDimension.size();count++){
			List<double[]> fullDataSet=new ArrayList<>();
            for(int index=0;index<=count;index++){
            	fullDataSet.add(fullDataSetWithDimension.get(index));
            }
        	Thread training=new Thread(new Training(count,10,errorMap,weightMap,fullDataSet));
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
	public static List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks){
		List<double[]> res=new ArrayList<double[]>();
		//get result
		
		//constant col
		double[] constants=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			constants[index]=1;
		}
		res.add(constants);
		
		//tranfered columns
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							double[] oneColData=new double[dataInLinks.size()];
							List<Double> list=new ArrayList<Double>();
							for(int index=0;index<dataInLinks.size();index++){
								DataInLink dataInLink=dataInLinks.get(index);
								
								if(dataInLink==null){
									System.out.println("null");
								}
								try{
									oneColData[index]=caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO);
								}catch(Exception e){
									System.out.println(caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
								}
							}
							res.add(oneColData);
						}
					}
				}
			}
		}
		
		//Y
		//constant col
		double[] Y=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			Y[index]=dataInLink.getTravle_time();
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
