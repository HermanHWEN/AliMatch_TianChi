package training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataInLink;

import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleBase;
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
    private Double error;
    private SimpleMatrix weight;
	private List<List<double[]>> trainingSetWithFold=Collections.unmodifiableList(new ArrayList<List<double[]>>());
	private List<List<double[]>> validationSetWithFold=Collections.unmodifiableList(new ArrayList<List<double[]>>());
    
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

    	//Separate data into training set and validation set
		int size=fullDataSetWithDimension.get(0).length;
		int step=size/foldTime+1;
		
		//training with cross validation 
		//separate data into training set and validation set
		
		for(int start=0;start<size;start+=step){
			List<double[]> trainingSet=Collections.unmodifiableList(new ArrayList<double[]>());
			List<double[]> validationSet=Collections.unmodifiableList(new ArrayList<double[]>());
			for(double[] fullDataSet:fullDataSetWithDimension){
				double[] validationData=Arrays.copyOfRange(fullDataSet, start, start+step-1);
				double[] trainingData2=Arrays.copyOfRange(fullDataSet, start+step, size-1);
				double[] trainingData=null;
				if(start>0){
					
					double[] trainingData1=Arrays.copyOfRange(fullDataSet, 0, start-1);
					trainingData=ArrayUtils.addAll(trainingData1,trainingData2);
				}else{
					trainingData=ArrayUtils.addAll(trainingData2);
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
			SimpleMatrix Yt=new SimpleMatrix(trainingSet.size(),1);
			Yt.setColumn(0, 0, trainingSet.get(trainingSet.size()-1));
			SimpleMatrix W=CrossValidation.genTargetFunWeidth(trainingSet,Yt);
			
			//validation
	        SimpleMatrix Yv=new SimpleMatrix(validationSet.size(),1);
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
    	}
    	
    	//get final weight
    	SimpleMatrix Y=new SimpleMatrix(fullDataSetWithDimension.size(),1);
		Y.setColumn(0, 0, fullDataSetWithDimension.get(fullDataSetWithDimension.size()-1));
		SimpleMatrix W=CrossValidation.genTargetFunWeidth(fullDataSetWithDimension,Y);
		
		this.error=this.error/foldTime;
		this.weight=W;
		
        errorMap.put(count,error);
		weightMap.put(count, W);
    	
    }
    

	


}
