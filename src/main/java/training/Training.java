package training;

import importData.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleMatrix;

import crossValidation.ErrorFun;

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

	//	private List<double[]> yTrainingSetWithFold=new ArrayList<double[]>();
	//	private List<double[]> yValidationSetWithFold=new ArrayList<double[]>();

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
		//		if(isGoingUp(errorMap,count)) return;
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


			SimpleMatrix W=genTargetFunWeidthPseudoI(trainingSet);
			//			SimpleMatrix W=genTargetFunWeidthGradientDescend(trainingSet);

			//validation
			SimpleMatrix Yv=new SimpleMatrix(validationSet.get(0).length,1);
			Yv.setColumn(0, 0, validationSet.get(validationSet.size()-1));

			//get validation error
			SimpleMatrix Xv=new SimpleMatrix(validationSet.get(0).length,validationSet.size()-1);

			//validate
			for(int colNum=0;colNum<validationSet.size()-1;colNum++){
				double[] col=validationSet.get(colNum);
				Xv.setColumn(colNum, 0,col);
			}
			//			double e=Xv.mult(W).minus(Yv).normF()/(validationSet.size()+1);
			double e=ErrorFun.targetError(W, Xv, Yv);
			error+=e;
			trainingSet.clear();
			validationSet.clear();
			W=null;Yv=null;Xv=null;
			System.gc();
		}

		SimpleMatrix W=genTargetFunWeidthPseudoI(fullDataSetWithDimension);
		//		SimpleMatrix W=genTargetFunWeidthGradientDescend(fullDataSetWithDimension);

		error=error/foldTime;

		errorMap.put(count,error);
		weightMap.put(count, W);


		trainingSetWithFold=null;
		validationSetWithFold=null;
		System.gc();
	}

	//pseudo-inverse
	public SimpleMatrix genTargetFunWeidthPseudoI(List<double[]> res){

		//get W
		SimpleMatrix Y=new SimpleMatrix(res.get(0).length,1);

		Y.setColumn(0, 0, res.get(res.size()-1));

		SimpleMatrix X=new SimpleMatrix(res.get(0).length,res.size()-1);

		for(int colNum=0;colNum<res.size()-1;colNum++){
			double[] col=res.get(colNum);
			X.setColumn(colNum, 0,col);
		}

		SimpleMatrix sudoX=X.pseudoInverse();
		SimpleMatrix W = null;
		try{
			W=sudoX.mult(Y);
		}catch(Exception e){
			System.out.println("sudoX rows#" + sudoX.numRows() +" cols#" + sudoX.numCols());
			System.out.println("Y rows#" + Y.numRows() +" cols#" + Y.numCols());
		}
		Arrays.asList(W.getMatrix().data);
		Y=null;
		X=null;
		System.gc();
		return W;

	}

	//iterator gradient descend
	public SimpleMatrix genTargetFunWeidthGradientDescend(List<double[]> res){

		//get W
		SimpleMatrix Y=new SimpleMatrix(res.get(0).length,1);

		Y.setColumn(0, 0, res.get(res.size()-1));

		SimpleMatrix X=new SimpleMatrix(res.get(0).length,res.size()-1);

		for(int colNum=0;colNum<res.size()-1;colNum++){
			double[] col=res.get(colNum);
			X.setColumn(colNum, 0,col);
		}

		SimpleMatrix W = new SimpleMatrix(res.size()-1,1);

		W.set(0.1);

		for(int count=0;count<Constant.REPEATE_TIMES;count++)
			W=ErrorFun.updateWeight(Constant.LEARNING_RATE, W, X, Y);
		Arrays.asList(W.getMatrix().data);
		Y=null;
		X=null;
		System.gc();
		return W;

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
