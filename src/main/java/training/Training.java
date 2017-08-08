package training;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import controll.Constant;
import crossValidation.ErrorFun;

/**
 * Created by 43903042 on 2017/8/2.
 */
public class Training implements Runnable{
	private static Logger log = Logger.getLogger(Training.class);  
	private final Object mListMutex = new Object();

	private int countFolde;
	private int parametersNum;
	private int foldTime;
	private Map<Integer,Double> errorMap;
	private Map<Integer,SimpleMatrix> weightMap;
	private List<List<double[]>> trainingSetWithFold=new ArrayList<List<double[]>>();
	private List<List<double[]>> validationSetWithFold=new ArrayList<List<double[]>>();
	private List<double[]> fullDataSetWithDimension;


	public Training(int parametersNum,int foldTime, Map<Integer, Double> errorMap, Map<Integer, SimpleMatrix> weightMap,
			List<double[]> fullDataSetWithDimension) {
		super();
		this.parametersNum = parametersNum;
		this.foldTime = foldTime;
		this.errorMap = errorMap;
		this.weightMap = weightMap;
		this.fullDataSetWithDimension=Collections.unmodifiableList(fullDataSetWithDimension);
	}

	@Override
	public void run() {
		//		if(isGoingUp(errorMap,parametersNum)) return;
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
		
		SimpleMatrix initW= new SimpleMatrix(trainingSetWithFold.get(0).size()-1,1);
		initW.set(0.1);
		initW=genTargetFunWeidthPseudoI(validationSetWithFold.get(0));
		SimpleMatrix W = null;
		for(int fold=0;fold< trainingSetWithFold.size();fold++){
			List<double[]> trainingSet=trainingSetWithFold.get(fold);
			List<double[]> validationSet=validationSetWithFold.get(fold);

			//validation set
			SimpleMatrix Yv=new SimpleMatrix(validationSet.get(0).length,1);
			Yv.setColumn(0, 0, validationSet.get(validationSet.size()-1));
			SimpleMatrix Xv=new SimpleMatrix(validationSet.get(0).length,validationSet.size()-1);
			for(int colNum=0;colNum<validationSet.size()-1;colNum++){
				double[] col=validationSet.get(colNum);
				Xv.setColumn(colNum, 0,col);
			}
			
			if(Constant.USE_GRADIENT_DESCEND){
				W=genTargetFunWeidthGradientDescend(trainingSet,initW,Xv,Yv);
			}else{
				W=genTargetFunWeidthPseudoI(trainingSet);
			}
			initW=W;
			
			double e=ErrorFun.targetError(W, Xv, Yv);
			error+=e;
			trainingSet.clear();
			validationSet.clear();
			W=null;Yv=null;Xv=null;
			System.gc();
		}

		if(Constant.USE_GRADIENT_DESCEND){
			W=genTargetFunWeidthGradientDescend(fullDataSetWithDimension,initW,null,null);
		}else{
			W=genTargetFunWeidthPseudoI(fullDataSetWithDimension);
		}

		error=error/foldTime;
		log.info("Training with "+StringUtils.repeat(" ", 3-String.valueOf((parametersNum+1)).length())+(parametersNum+1)+" parameters completed! Error: " +error);
		errorMap.put(parametersNum,error);
		weightMap.put(parametersNum, W);


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
			log.info("sudoX rows#" + sudoX.numRows() +" cols#" + sudoX.numCols());
			log.info("Y rows#" + Y.numRows() +" cols#" + Y.numCols());
		}
		Arrays.asList(W.getMatrix().data);
		Y=null;
		X=null;
		System.gc();
		return W;

	}

	//iterator gradient descend
	public SimpleMatrix genTargetFunWeidthGradientDescend(List<double[]> res,SimpleMatrix initW,SimpleMatrix Xv,SimpleMatrix Yv){

		SimpleMatrix Y;SimpleMatrix X;
		
		int dataSize=res.get(0).length;
		int maxRadomNum=dataSize-1;

		if(Constant.USE_STOCHASTIC_GRADIENT_DESCEND){
			Y=new SimpleMatrix(Constant.SIZE_OF_ONE_BATCH<=0?1:Constant.SIZE_OF_ONE_BATCH,1);
			X=new SimpleMatrix(Constant.SIZE_OF_ONE_BATCH<=0?1:Constant.SIZE_OF_ONE_BATCH,res.size()-1);
		}else{
			
			Y=new SimpleMatrix(dataSize,1);
			
			Y.setColumn(0, 0, res.get(res.size()-1));
			
			X=new SimpleMatrix(dataSize,res.size()-1);
			
			for(int colNum=0;colNum<res.size()-1;colNum++){
				double[] col=res.get(colNum);
				X.setColumn(colNum, 0,col);
			}
		}

		if(initW==null){
			initW= new SimpleMatrix(res.size()-1,1);
			initW.set(0.1);
		}
		
		String traceOfLastW=Arrays.toString(initW.getMatrix().data);
		SimpleMatrix W =initW;

		countFolde++;
		
		//gradient descend ......
		double learningRate=Constant.LEARNING_RATE;
		int countOfEpochFromLastMinError=0;
		int countOfEpochWithMinError=0;
		double minError = 0;
		int countOfEpoch;
		for(countOfEpoch=0;countOfEpoch<Constant.MAX_REPEATE_TIMES;countOfEpoch++){
			if(Constant.USE_STOCHASTIC_GRADIENT_DESCEND){
				for(int rowOffset=0;rowOffset<(Constant.SIZE_OF_ONE_BATCH<=0?1:Constant.SIZE_OF_ONE_BATCH);rowOffset++){
					int r;
					if(dataSize>1){
						r = ThreadLocalRandom.current().nextInt(maxRadomNum);
					}else{
						r=0;
					}
					Y.setColumn(0, rowOffset, res.get(res.size()-1)[r]);
					for(int colNum=0;colNum<res.size()-1;colNum++){
						double[] col=res.get(colNum);
						X.setColumn(colNum, rowOffset,col[r]);
					}
				}
			}
			SimpleMatrix Wn=ErrorFun.updateWeight(learningRate, W, X, Y);
			double errorN=0;
			if(Xv!=null && Yv!=null){
				errorN=ErrorFun.targetError(Wn, Xv, Yv);
			}else{
				errorN=ErrorFun.targetError(Wn, X, Y);
			}
			
			
			//initial min error
			countOfEpochFromLastMinError++;
			if(countOfEpoch==0)	{
				W=Wn;
				minError=errorN;
				countOfEpochWithMinError=countOfEpoch;
				continue;
			}
			
			//check if reach the max count of epoch
			//if not when error is less than last min error, then update min error.Else just go to next epoch
			if(countOfEpochFromLastMinError<Constant.MAX_NUM_OF_EPOCH){
				if(BigDecimal.valueOf(errorN).setScale(Constant.ACURACY_OF_ERROR, BigDecimal.ROUND_HALF_UP).doubleValue()<=BigDecimal.valueOf(minError).setScale(Constant.ACURACY_OF_ERROR, BigDecimal.ROUND_HALF_UP).doubleValue()){
					minError=errorN;
					countOfEpochWithMinError=countOfEpoch;
					countOfEpochFromLastMinError=0;
					W=Wn;
				}
			}else{//if reach the max count of epoch,check the learning rate lower bound
				//if reach it , then end this training 
				//else decrease learning rate.
				if(Constant.DECAY_LEARNING_RATE){
					if(learningRate<(Constant.LEARNING_RATE/Constant.LEARNING_RATE_LBOUND_DIVISOR)) break;
					learningRate=learningRate/Constant.LEARNING_RATE_DIVISOR;
					countOfEpochFromLastMinError=0;
				}else{
					break;
				}
			}
		}
		
		String traceOfThisW=Arrays.toString(W.getMatrix().data);
		StringBuffer infoOfMinError=new StringBuffer("Model with"+StringUtils.repeat(" ", 3-String.valueOf((res.size()-1)).length())+(res.size()-1)+" params in ");
		infoOfMinError.append("fold "+countFolde+StringUtils.repeat(" ", 3-String.valueOf(countFolde).length()));
		infoOfMinError.append("#Error:"+minError+StringUtils.repeat(" ", 20-String.valueOf(minError).length()));
		infoOfMinError.append("#Current learning rate:"+learningRate+StringUtils.repeat(" ", 20-String.valueOf(learningRate).length()));
		infoOfMinError.append("#Repeated times got MinError:"+(countOfEpochWithMinError+1)+StringUtils.repeat(" ", 10-String.valueOf((countOfEpochWithMinError+1)).length()));
		infoOfMinError.append("#Total repeated times:" + countOfEpoch+StringUtils.repeat(" ", 6-String.valueOf(countOfEpoch).length()));
		infoOfMinError.append("\n"+StringUtils.repeat(" ",101)+"#Last weight:" + traceOfLastW +StringUtils.repeat(" ", 20-String.valueOf(traceOfLastW).length()));
		infoOfMinError.append("\n"+StringUtils.repeat(" ",101)+"#This weight:" + traceOfThisW+StringUtils.repeat(" ", String.valueOf(traceOfThisW).length()));
		log.debug(infoOfMinError);
		Arrays.asList();
		Y=null;
		X=null;
		System.gc();
		return W;

	}

	private static boolean lowerThanThreshold(SimpleMatrix Wo,SimpleMatrix Wn,double threshold){
		SimpleMatrix tmp=Wn.minus(Wo);
		for(int index=0;index<tmp.getNumElements();index++){
			if(Math.abs((tmp.get(index)-Wo.get(index))/Wo.get(index))>threshold){
				return false;
			}
		}
		return true;
	}
	
	
	private static boolean isGoingUp(Map<Integer, Double> errorMap,int parametersNum){
		double[] errors=new double[errorMap.values().size()];
		int errorI=0;
		for(int i=0;i<parametersNum;i++){
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
