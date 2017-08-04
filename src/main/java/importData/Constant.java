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
	public final static String PATH_OF_RESULT="C://Match//result.txt";
	
	public final static int SIZE_OF_DATA=100;
	public final static int REPEATE_TIMES=1000;
	public final static double LEARNING_RATE=0.1;
	public static ThreadPoolExecutor threadPoolExecutor;
	
	public static ThreadPoolExecutor getThreadPoolExecutor(){
		if(threadPoolExecutor!=null) return threadPoolExecutor;
		BlockingQueue queue = new LinkedBlockingQueue();   
		return new ThreadPoolExecutor(4,20,Long.MAX_VALUE,TimeUnit.DAYS, queue);
	}
}
