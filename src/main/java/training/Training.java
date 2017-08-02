package training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataInLink;

import org.ejml.simple.SimpleMatrix;

import crossValidation.CrossValidation;

/**
 * Created by 43903042 on 2017/8/2.
 */
public class Training implements Runnable{
	private final Object mListMutex = new Object();

    private List<DataInLink> trainingSet;
    private List<DataInLink> validationSet;

    private Map<Integer,Double> errorMap=new HashMap<>();
    private Map<Integer,SimpleMatrix> weightMap=new HashMap<>();

    public Training(List<DataInLink> trainingSet, List<DataInLink> validationSet, Map<Integer, Double> errorMap, Map<Integer, SimpleMatrix> weightMap) {
        this.trainingSet = Collections.unmodifiableList(trainingSet);
        this.validationSet = Collections.unmodifiableList(validationSet);
        this.errorMap = errorMap;
        this.weightMap = weightMap;
    }

    @Override
    public void run() {
        //training.....
        //get full dimension data
        List<double[]> fullTrainingData= transferData(5,trainingSet);
        List<double[]> fullValidationData=transferData(5,validationSet);

        SimpleMatrix Yt=new SimpleMatrix(trainingSet.size(),1);
        CrossValidation.setY(Yt,trainingSet);
        SimpleMatrix Yv=new SimpleMatrix(validationSet.size(),1);
        CrossValidation.setY(Yv,validationSet);


        for(int count=0;count<fullTrainingData.size();count++){
            List<double[]> trainingData=new ArrayList<double[]>();
            List<double[]> validationData=new ArrayList<double[]>();
            for(int index=0;index<=count;index++){
                trainingData.add(fullTrainingData.get(index));
                validationData.add(fullValidationData.get(index));
            }

            SimpleMatrix W=CrossValidation.genTargetFunWeidth(trainingData,Yt);
            SimpleMatrix X=new SimpleMatrix(validationData.get(0).length,trainingData.size());

            //validate
            int colNum=0;
            for(double[] col:validationData){
                X.setColumn(colNum, 0,col);
                colNum++;
            }

            //get validation error for a partial
            double e=X.mult(W).minus(Yv).normF()/(validationSet.size()+1);
            if(errorMap.get(count)!=null){
                errorMap.put(count,errorMap.get(count)+e);
            }else{
                errorMap.put(count,e);
            }
            weightMap.put(count, W);

        }
        trainingSet.clear();
        validationSet.clear();
    }
    

	public synchronized List<double[]>  transferData(int maxOrder,final List<DataInLink> dataInLinks){
		List<double[]> res=new ArrayList<double[]>();
		//get result
		
		//constant col
		double[] constants=new double[dataInLinks.size()];
		for(int index=0;index<dataInLinks.size();index++){
			constants[index]=1;
		}
		res.add(constants);
		
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							double[] oneColData=new double[dataInLinks.size()];
							List<Double> list=new ArrayList<Double>();
							synchronized(mListMutex){
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
							}
							res.add(oneColData);
						}
					}
				}
			}
		}
		return res;
		
	}
	
	private synchronized double caclulateWithOrder(final DataInLink dataInLink,int lengthO,int widthO,int classO,int weightO,int startTimeO){
		if(dataInLink==null) return 0;
		double reswithOrder=Math.pow(dataInLink.getLink().getLength(), lengthO)*
		Math.pow(dataInLink.getLink().getWidth(), widthO)*
		Math.pow(dataInLink.getLink().getLink_class(), classO)*
		Math.pow(dataInLink.getLink().getWeight(), weightO)*
		Math.pow(dataInLink.getStartTime().getHours()*60+dataInLink.getStartTime().getMinutes(), startTimeO);
		
		return reswithOrder;
	}
	

    public List<DataInLink> getValidationSet() {
        return validationSet;
    }

    public void setValidationSet(List<DataInLink> validationSet) {
        this.validationSet = validationSet;
    }

    public List<DataInLink> getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(List<DataInLink> trainingSet) {
        this.trainingSet = trainingSet;
    }

    public Map<Integer, Double> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map<Integer, Double> errorMap) {
        this.errorMap = errorMap;
    }

    public Map<Integer, SimpleMatrix> getWeightMap() {
        return weightMap;
    }

    public void setWeightMap(Map<Integer, SimpleMatrix> weightMap) {
        this.weightMap = weightMap;
    }


}
