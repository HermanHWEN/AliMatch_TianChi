package crossValidation;

import org.ejml.simple.SimpleMatrix;

public class ErrorFun {
	public static synchronized double targetError(SimpleMatrix W,SimpleMatrix X,SimpleMatrix Y){

		double sum=0;
		for(int row=0;row<X.numRows();row++){

			SimpleMatrix Xi=X.extractMatrix(row, row+1, 0, X.numCols());
			double yi=Y.get(row, 0);
			sum+=Math.abs(Xi.mult(W).trace()-yi)/yi;
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
		for(int row=0;row<X.numRows();row++){

			SimpleMatrix Xi=X.extractMatrix(row, row+1, 0, X.numCols());
			SimpleMatrix yi=Y.extractMatrix(row, row+1, 0, 1);
			SimpleMatrix tmp=Xi.transpose().mult(Xi.mult(W).minus(yi));
			double cons=2/Math.pow(Y.get(row, 0), 2)/Math.pow(X.numRows(), 2);
			SimpleMatrix consM=new SimpleMatrix(tmp.numRows(),tmp.numRows());
			for(int i=0;i<consM.numRows();i++){
				consM.set(i, i, cons);
			}
			sum=sum.plus(consM.mult(tmp));
		}
		return sum;
	}
}
