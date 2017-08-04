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
	public final static String PATH_OF_RESULT="C://Match//HermanWen.txt";
	
	public final static int MAXORDER=4;
	public final static int FOLDTIME=50000;
	public final static int SIZE_OF_DATA=100;
	public final static int REPEATE_TIMES=10000;
	public final static double LEARNING_RATE=0.1;
	public final static double THREDHOLD=0.0001;
	public static ThreadPoolExecutor threadPoolExecutor;
	
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if(threadPoolExecutor!=null && !threadPoolExecutor.isShutdown()) return threadPoolExecutor;
		BlockingQueue queue = new LinkedBlockingQueue();   
		threadPoolExecutor=new ThreadPoolExecutor(10,20,Long.MAX_VALUE,TimeUnit.DAYS, queue);
		return threadPoolExecutor;
	}
}
