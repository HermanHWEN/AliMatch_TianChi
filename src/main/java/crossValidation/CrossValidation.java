package crossValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.ejml.simple.SimpleMatrix;

import model.DataInLink;
import model.Link;

public class CrossValidation {
	
	
	public static Map<Integer,Double> errors(List<DataInLink> dataInLinks){
		
		Map<Integer,Double> errorMap=new HashMap<>();
		List<DataInLink> validationSet=new ArrayList<DataInLink>();
		List<DataInLink> trainingSet=new ArrayList<DataInLink>();
		
		
		double error=0;
		
		//Separate data into training set and validation set
		int size=dataInLinks.size();
		int step=size/10+1;
		for(int start=0;start<size;start+=step){
			
			for(int index=0;index<size;++index){
				
				
				//pick validation set
				if(index>start && index<start+step && index<size){
					validationSet.add(dataInLinks.get(index));
				}else{//there is training set
					trainingSet.add(dataInLinks.get(index));
				}
			}
			
			
			//training.....
			//get full dimension data
			List<List<Double>> fullTrainingData=transferData(trainingSet);
			List<List<Double>> fullValidationData=transferData(validationSet);
			
			SimpleMatrix Yt=new SimpleMatrix(trainingSet.size(),1);
			setY(Yt,trainingSet);
			SimpleMatrix Yv=new SimpleMatrix(validationSet.size(),1);
			setY(Yv,validationSet);
			
			
			for(int count=0;count<fullTrainingData.size();count++){
				List<List<Double>> trainingData=new ArrayList<List<Double>>();
				List<List<Double>> validationData=new ArrayList<List<Double>>();
				for(int index=0;index<=count;index++){
					trainingData.add(fullTrainingData.get(index));
					validationData.add(fullValidationData.get(index));
				}
				
				SimpleMatrix W=genTargetFunWeidth(trainingData,Yt);
				SimpleMatrix X=new SimpleMatrix(validationData.get(0).size(),trainingData.size());
				
				int colNum=0;
				for(List<Double> col:validationData){
					X.setColumn(colNum, 0,col.stream().mapToDouble(Double::doubleValue).toArray());
					colNum++;
				}
				
				//get validation error for a partial
				double e=X.mult(W).minus(Yv).normF()/(validationSet.size()+1);
				if(errorMap.get(count)!=null){
					errorMap.put(count,errorMap.get(count)+e);
				}else{
					errorMap.put(count,e);
				}
				
			}
			
		}
		
		//get min error
		int minCount=0;
		double minError=Double.MAX_VALUE;
		for(int count=0;count<getCounts();count++){
			if(errorMap.get(count)!=null){
				if(errorMap.get(count)<minError){
					minCount=count;
					minError=errorMap.get(count);
				}
			}
		}
		System.out.println(minCount);
		System.out.println(minError);
		
		return errorMap;
		
	}
	
	private static int getCounts(){
		int res=0;
		int maxOrder=5;
		
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
	
	private static List<List<Double>> transferData(List<DataInLink> dataInLinks){
		List<List<Double>> res=new ArrayList<List<Double>>();
		int maxOrder=5;
		//get result
		
		int index=0;
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							List<Double> list=new ArrayList<Double>();
							for(DataInLink dataInLink: dataInLinks){
								list.add(caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
							}
							res.add(list);
						}
					}
				}
			}
		}
		return res;
		
	}
	
	private static double caclulateWithOrder(DataInLink dataInLink,int lengthO,int widthO,int classO,int weightO,int startTimeO){
		return Math.pow(dataInLink.getLink().getLength(), lengthO)*
		Math.pow(dataInLink.getLink().getWidth(), widthO)*
		Math.pow(dataInLink.getLink().getLink_class(), classO)*
		Math.pow(dataInLink.getLink().getWeight(), weightO)*
		Math.pow(dataInLink.getStartTime().getHours()*60+dataInLink.getStartTime().getMinutes(), startTimeO);
	}
	
	private static SimpleMatrix genTargetFunWeidth(List<List<Double>> res,SimpleMatrix Y){
		
		SimpleMatrix X=new SimpleMatrix(res.get(0).size(),res.size());
		
		int colNum=0;
		for(List<Double> col:res){
			X.setColumn(colNum, 0,col.stream().mapToDouble(Double::doubleValue).toArray());
			colNum++;
		}
		
		SimpleMatrix W=X.pseudoInverse().mult(Y);
		Arrays.asList(W.getMatrix().data);
		return W;
		
	}
	private static void setY(SimpleMatrix Y,List<DataInLink> dataInLinks){
		
		for(DataInLink dataInLink:dataInLinks){
			
			int offset=0;
			Y.setColumn(0, 0, dataInLink.getTravle_time());
			offset++;
		}
		
	}
}
