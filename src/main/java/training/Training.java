package training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleMatrix;

import controll.Constant;
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
		int step=(int) Math.ceil(((double) size)/foldTime);

		//training with cross validation 
		//separate data into training set and validation set

		for(int start=0;start<size;start+=step){
			List<double[]> trainingSet=new ArrayList<double[]>();
			List<double[]> validationSet=new ArrayList<double[]>();
			for(double[] fullDataSet:fullDataSetWithDimension){

				double[] validationData=Arrays.copyOfRange(fullDataSet, start, Math.min(size, start+step));
				double[] trainingData;
				if(start==0){
					trainingData=Arrays.copyOfRange(fullDataSet,start+step,size);

				}else if(start+step<size){
					trainingData=Arrays.copyOfRange(fullDataSet,0, start);
					ArrayUtils.addAll(trainingData, Arrays.copyOfRange(fullDataSet,start+step, size));
				}else{
					trainingData=Arrays.copyOfRange(fullDataSet,0, start);
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


			SimpleMatrix W;
			if(Constant.USE_GRADIENT_DESCEND){
				W=genTargetFunWeidthGradientDescend(trainingSet);
			}else{
				W=genTargetFunWeidthPseudoI(trainingSet);
			}

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

		SimpleMatrix W;
		if(Constant.USE_GRADIENT_DESCEND){
			W=genTargetFunWeidthGradientDescend(fullDataSetWithDimension);
		}else{
			W=genTargetFunWeidthPseudoI(fullDataSetWithDimension);
		}

		error=error/foldTime;
		System.out.println("Training with "+(count+1)+" parameters completed! Error: " +error);
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

		SimpleMatrix Y;SimpleMatrix X;
		
		int max=res.get(0).length-1;
        Random random = new Random();

		if(Constant.USE_STOCHASTIC_GRADIENT_DESCEND){
			//get W
			Y=new SimpleMatrix(1,1);
			X=new SimpleMatrix(1,res.size()-1);
		}else{
			
			//get W
			Y=new SimpleMatrix(res.get(0).length,1);
			
			Y.setColumn(0, 0, res.get(res.size()-1));
			
			X=new SimpleMatrix(res.get(0).length,res.size()-1);
			
			for(int colNum=0;colNum<res.size()-1;colNum++){
				double[] col=res.get(colNum);
				X.setColumn(colNum, 0,col);
			}
		}

		SimpleMatrix W = new SimpleMatrix(res.size()-1,1);

		W.set(0.1);

		for(int count=0;count<Constant.REPEATE_TIMES;count++){
			if(Constant.USE_STOCHASTIC_GRADIENT_DESCEND){
				int r = random.nextInt(max);
				Y.setColumn(0, 0, res.get(res.size()-1)[r]);
				for(int colNum=0;colNum<res.size()-1;colNum++){
					double[] col=res.get(colNum);
					X.setColumn(colNum, 0,col[r]);
				}
			}
			SimpleMatrix Wn=ErrorFun.updateWeight(Constant.LEARNING_RATE, W, X, Y);
			if(errorLowerThanThredhold(W,Wn,X,Y,Constant.THREDHOLD)) {
				System.out.println("Lower Than Thredhold # "+(res.size()-1)+" parameters  # Repeat times:" + (count+1));
				break;
			}
			W=Wn;
		}
		Arrays.asList(W.getMatrix().data);
		Y=null;
		X=null;
		System.gc();
		return W;

	}

	private static boolean lowerThanThredhold(SimpleMatrix Wo,SimpleMatrix Wn,double thredhold){
		SimpleMatrix tmp=Wn.minus(Wo);
		for(int index=0;index<tmp.getNumElements();index++){
			if(Math.abs((tmp.get(index)-Wo.get(index))/Wo.get(index))>thredhold){
				return false;
			}
		}
		return true;
	}
	
	private static boolean errorLowerThanThredhold(SimpleMatrix Wo,SimpleMatrix Wn,SimpleMatrix X,SimpleMatrix Y,double thredhold){
		double errorO=ErrorFun.targetError(Wo, X, Y);
		double errorN=ErrorFun.targetError(Wn, X, Y);
		
		return Math.abs(errorN-errorO)<=thredhold;
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
