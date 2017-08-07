package controll;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Constant {
	public final static String PATH_OF_LINK_INFO="C://Match//gy_contest_link_info.txt";
	public final static String PATH_OF_TMP="C://TMP";
	public final static String PATH_OF_LINK_TOP="C://Match//gy_contest_link_top(20170715更新).txt";
//	public final static String PATH_OF_TRAINING_DATA="C://Match//gy_contest_link_traveltime_training_data - small.txt";
	public final static String PATH_OF_TRAINING_DATA="C://Match//gy_contest_link_traveltime_training_data.txt";
	public final static String PATH_OF_RESULT="C://Match//{1}//HermanWen_{0}_{1}.txt";
	public final static String PATH_OF_MODEL="C://Match//{1}//Model_{0}_{1}.txt";
	public final static int ACURACY_OF_TRAVEL_TIME_OUTPUT=14;//accuracy of travel time for output
	
	public final static int MAXORDER=2;
	public final static int MINORDER_PER_FIELD=-2;
	public final static int FOLDTIME=10;//fold time of cross validation
	public final static int LINES_NEED_TO_READ=-1;//how many lines of txt file need to be read in.
	public final static int SIZE_OF_TRAINING_DATA=5000;//how many data for training
	public final static double LEARNING_RATE=0.25;//initial learning rate for gradient descend
	public final static boolean DECAY_LEARNING_RATE=false;
	public final static double LEARNING_RATE_DIVISOR=2;//if the error is going up,learning rate will divided by this
	public final static double LEARNING_RATE_LBOUND_DIVISOR=Math.pow(2, 15);//if the current learning rate<(initial/this), will stop
	public final static int MAX_NUM_OF_EPOCH=1000;//for when epoch time reach this, will check if error is “no-improvement-in-n"
	public final static int ACURACY_OF_ERROR=6;//accuracy of error when checking min error
//	public final static double THRESHOLD=0.00001;//for gradient descend
	
	
	//gradient descend
	public final static int MAX_REPEATE_TIMES=1000000;//for gradient descend
	public final static boolean USE_GRADIENT_DESCEND=true;//false, will use pseudo inverse
	public final static boolean USE_STOCHASTIC_GRADIENT_DESCEND=true;//true, will use small batch stochastic gradient descend;false will use whole data set
	public final static int SIZE_OF_ONE_BATCH=200;//when USE_STOCHASTIC_GRADIENT_DESCEND is true, using it as batch size
	
	//normalization
	public final static boolean USE_MIN_MAX_NORMALIZATION=false;
	public final static boolean USE_ZERO_MEAN_NORMALIZATION=true;//when above two are false, won't use normalization
	public final static boolean USE_MULTI_THREAD_FOR_TRAINING=true;
	
	
	//filter data
	public final static int[] STARTHOUR_RANGE=new int[]{8,9};//include first one, exclude second one
	public final static int[] NEED_MONTH=new int[]{};
	
	public static ThreadPoolExecutor threadPoolExecutor;
	
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if(threadPoolExecutor!=null && !threadPoolExecutor.isShutdown()) return threadPoolExecutor;
		BlockingQueue queue = new LinkedBlockingQueue();   
		threadPoolExecutor=new ThreadPoolExecutor(10,20,Long.MAX_VALUE,TimeUnit.DAYS, queue);
		return threadPoolExecutor;
	}
}
