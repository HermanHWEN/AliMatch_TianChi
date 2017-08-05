package importData;

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
	public final static String PATH_OF_RESULT="C://Match//HermanWen_{0}.txt";
	
	public final static int MAXORDER=4;
	public final static int FOLDTIME=10;
	public final static int SIZE_OF_DATA=5000000;
	public final static int REPEATE_TIMES=10000000;
	public final static double LEARNING_RATE=0.1;
	public final static double THREDHOLD=0.00001;
	
	public final static boolean USE_GRADIENT_DESCEND=true;//when false, will use pseudo inverse
	
	public final static boolean USE_STOCHASTIC_GRADIENT_DESCEND=true;//when false, will use batch gradient descend
	
	public final static boolean USE_MIN_MAX_NORMALIZATION=true;
	public final static boolean USE_ZERO_MEAN_NORMALIZATION=true;//when above two are false, won't use normalization
	
	public final static boolean USE_MULTI_THREAD_FOR_TRAINING=true;
	
	public static ThreadPoolExecutor threadPoolExecutor;
	
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if(threadPoolExecutor!=null && !threadPoolExecutor.isShutdown()) return threadPoolExecutor;
		BlockingQueue queue = new LinkedBlockingQueue();   
		threadPoolExecutor=new ThreadPoolExecutor(10,20,Long.MAX_VALUE,TimeUnit.DAYS, queue);
		return threadPoolExecutor;
	}
}
