package crossValidation;

import org.ejml.simple.SimpleMatrix;

public class ErrorFun {
	public static synchronized double targetError(SimpleMatrix W,SimpleMatrix X,SimpleMatrix Y){

		double sum=0;
		for(int row=0;row<X.numRows();row++){

			SimpleMatrix Xi=X.extractMatrix(row, row+1, 0, X.numCols());
			double yi=Y.get(row, 0);
			sum+=Math.abs(Math.abs(Xi.mult(W).trace())-yi)/yi;
		}
		return sum/X.numRows();
	}
	
	public static synchronized SimpleMatrix updateWeight(double learningRate,SimpleMatrix W,SimpleMatrix X,SimpleMatrix Y){
		SimpleMatrix lamda=new SimpleMatrix(W.numRows(),W.numRows());
		for(int i=0;i<W.numRows();i++){
			lamda.set(i, i, learningRate);
		}
		
		SimpleMatrix WNew=W.minus(lamda.mult(derivative(W,X,Y)));
		return WNew;
	}

	public static synchronized SimpleMatrix derivative(SimpleMatrix W,SimpleMatrix X,SimpleMatrix Y){

		SimpleMatrix sum=new SimpleMatrix(W.numRows(),1);
		int N=X.numRows();
		for(int row=0;row<X.numRows();row++){

			SimpleMatrix Xi=X.extractMatrix(row, row+1, 0, X.numCols());
			SimpleMatrix yi=Y.extractMatrix(row, row+1, 0, 1);
			double yiNum=yi.trace();
			
			double cons=1/(yiNum*N);//yi/N
			double yHash=Xi.mult(W).trace();
			if((yHash>0 && yHash<yiNum) || yHash<(-1*yiNum)){
				cons=-1*cons;
			}
			SimpleMatrix consM=new SimpleMatrix(X.numCols(),X.numCols());
			for(int i=0;i<consM.numRows();i++){
				consM.set(i, i, cons);
			}
			
			sum=sum.plus(consM.mult(Xi.transpose()));
		}
		return sum;
	}
}
