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
        List<List<Double>> fullTrainingData= transferData(5,trainingSet);
        List<List<Double>> fullValidationData=transferData(5,validationSet);

        SimpleMatrix Yt=new SimpleMatrix(trainingSet.size(),1);
        CrossValidation.setY(Yt,trainingSet);
        SimpleMatrix Yv=new SimpleMatrix(validationSet.size(),1);
        CrossValidation.setY(Yv,validationSet);


        for(int count=0;count<fullTrainingData.size();count++){
            List<List<Double>> trainingData=new ArrayList<List<Double>>();
            List<List<Double>> validationData=new ArrayList<List<Double>>();
            for(int index=0;index<=count;index++){
                trainingData.add(fullTrainingData.get(index));
                validationData.add(fullValidationData.get(index));
            }

            SimpleMatrix W=CrossValidation.genTargetFunWeidth(trainingData,Yt);
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
            weightMap.put(count, W);

        }
        trainingSet.clear();
        validationSet.clear();
    }
    

	public synchronized List<List<Double>>  transferData(int maxOrder,final List<DataInLink> dataInLinks){
		List<List<Double>> res=new ArrayList<List<Double>>();
		//get result
		
		//constant col
		List<Double> constants=new ArrayList<Double>();
		int count = 0;
		while(count<dataInLinks.size()){
			count++;
			constants.add((double) 1);
		}
		res.add(constants);
		
		int index=0;
		for(int order=1;order<=maxOrder;order++){
			
			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int weightO=order-lengthO-widthO-classO;weightO>=0;weightO--){
							int startTimeO=order-lengthO-widthO-classO-weightO;
							List<Double> list=new ArrayList<Double>();
							synchronized(mListMutex){
								for(DataInLink dataInLink: dataInLinks){
									if(dataInLink==null){
										System.out.println("null");
									}
									try{
										
										list.add(caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
									}catch(Exception e){
										System.out.println(caclulateWithOrder(dataInLink,lengthO, widthO, classO, weightO, startTimeO));
									}
								}
							}
							res.add(list);
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
