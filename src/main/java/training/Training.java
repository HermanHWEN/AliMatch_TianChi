package training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleMatrix;

import crossValidation.CrossValidation;

/**
 * Created by 43903042 on 2017/8/2.
 */
public class Training implements Runnable{
	private final Object mListMutex = new Object();

	private int count;
	private int foldTime;
	private Map<Integer,Double> errorMap;
	private Map<Integer,SimpleMatrix> weightMap;
	private List<List<double[]>> trainingSetWithFold=new ArrayList<List<double[]>>();
	private List<List<double[]>> validationSetWithFold=new ArrayList<List<double[]>>();
    
    private List<double[]> fullDataSetWithDimension;

    
    
    public Training(int count,int foldTime, Map<Integer, Double> errorMap, Map<Integer, SimpleMatrix> weightMap,
    		List<double[]> fullDataSetWithDimension) {
		super();
		this.count = count;
		this.foldTime = foldTime;
		this.errorMap = errorMap;
		this.weightMap = weightMap;
		this.fullDataSetWithDimension=Collections.unmodifiableList(fullDataSetWithDimension);
	}

    @Override
    public void run() {
    	if(isGoingUp(errorMap,count)) return;
    	double error = 0;
    	//Separate data into training set and validation set
		int size=fullDataSetWithDimension.get(0).length;
		int step=size/foldTime+1;
		
		//training with cross validation 
		//separate data into training set and validation set
		
		for(int start=0;start<size;start+=step){
			List<double[]> trainingSet=new ArrayList<double[]>();
			List<double[]> validationSet=new ArrayList<double[]>();
			for(double[] fullDataSet:fullDataSetWithDimension){
				
				double[] validationData=Arrays.copyOfRange(fullDataSet, start, Math.min(size-1, start+step-1));
				double[] trainingData;
				if(start==0){
					trainingData=Arrays.copyOfRange(fullDataSet,start+step,size-1);
					
				}else if(start+step-1<size-1){
					trainingData=Arrays.copyOfRange(fullDataSet,0, start-1);
					ArrayUtils.addAll(trainingData, Arrays.copyOfRange(fullDataSet,start+step, size-1));
				}else{
					trainingData=Arrays.copyOfRange(fullDataSet,0, start-1);
				}
				trainingSet.add(trainingData);validationSet.add(validationData);
			}
			
			trainingSetWithFold.add(trainingSet);
			validationSetWithFold.add(validationSet);
			
		}
		
        //training.....
        //get full dimension data
    	
    	for(int fold=0;fold< trainingSetWithFold.size();fold++){
    		List<double[]> trainingSet=trainingSetWithFold.get(fold);
			List<double[]> validationSet=validationSetWithFold.get(fold);
			
			//get W
			SimpleMatrix Yt=new SimpleMatrix(trainingSet.get(0).length,1);
			
			Yt.setColumn(0, 0, trainingSet.get(trainingSet.size()-1));
			SimpleMatrix W=CrossValidation.genTargetFunWeidth(trainingSet,Yt);
			
			//validation
	        SimpleMatrix Yv=new SimpleMatrix(validationSet.get(0).length,1);
	        Yv.setColumn(0, 0, validationSet.get(validationSet.size()-1));
	        
	        //get validation error
	        SimpleMatrix Xv=new SimpleMatrix(validationSet.get(0).length,validationSet.size());

            //validate
            int colNum=0;
            for(double[] col:validationSet){
            	Xv.setColumn(colNum, 0,col);
                colNum++;
            }
            double e=Xv.mult(W).minus(Yv).normF()/(validationSet.size()+1);
            error+=e;
            trainingSet.clear();
            validationSet.clear();
            Yt=null;W=null;Yv=null;Xv=null;
            System.gc();
    	}
    	
    	//get final weight
    	SimpleMatrix Y=new SimpleMatrix(fullDataSetWithDimension.get(0).length,1);
		Y.setColumn(0, 0, fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
		SimpleMatrix W=CrossValidation.genTargetFunWeidth(fullDataSetWithDimension,Y);
		
		error=error/foldTime;
		
        errorMap.put(count,error);
		weightMap.put(count, W);
		
		
		trainingSetWithFold=null;
		validationSetWithFold=null;
		Y=null;
        System.gc();
    }

	private static boolean isGoingUp(Map<Integer, Double> errorMap,int count){
    	double[] errors=new double[errorMap.values().size()];
    	int errorI=0;
    	for(int i=0;i<count;i++){
    		if(errorMap.containsKey(i)){
    			errors[errorI]=errorMap.get(i);
    			errorI++;
    		}
    	}
    	for(int i=1;i<errors.length;i++){
    		if(errors[i]>errors[i-1]) return true;
    	}
    	
    	return false;
    }

	


}
