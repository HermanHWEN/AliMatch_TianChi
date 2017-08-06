package calculateFeatures;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Holiday {

	private static DateFormat df = new SimpleDateFormat("yyyyMMMdd", Locale.ENGLISH);
	private static ArrayList<String[]> rawHolidayMap=new ArrayList<String[]>(){
		{
			add(new String[]{"2016-01-01", "2016-01-03"});
			add(new String[]{"2016-02-06"});
			add(new String[]{"2016-02-07", "2016-02-13"});
			add(new String[]{"2016-02-14"});
			add(new String[]{"2016-04-02", "2016-04-04"});
			add(new String[]{"2016-04-30", "2016-05-02"});
			add(new String[]{"2016-06-09", "2016-06-11"});
			add(new String[]{"2016-06-12"});
			add(new String[]{"2016-09-15", "2016-09-17"});
			add(new String[]{"2016-09-18"});
			add(new String[]{"2016-10-01", "2016-10-07"});
			add(new String[]{"2016-10-08", "2016-10-09"});
			add(new String[]{"2016-12-31", "2017-01-02"});
			add(new String[]{"2017-01-22"});
			add(new String[]{"2017-01-27", "2017-02-02"});
			add(new String[]{"2017-02-04"});
			add(new String[]{"2017-04-01"});
			add(new String[]{"2017-04-02", "2017-04-04"});
			add(new String[]{"2017-04-29", "2017-05-01"});
			add(new String[]{"2017-05-27"});
			add(new String[]{"2017-05-28", "2017-05-30"});
			add(new String[]{"2017-09-30"});
			add(new String[]{"2017-10-01", "2017-10-08"});
		}
	};
	private static Map<String,Integer> holidayMap=new HashMap<String,Integer>();
	public static synchronized int getHolidayDays(Date date){
		synchronized(holidayMap){
			if(holidayMap.size()==0){
				for(String[] holidays:rawHolidayMap){
					int days=holidays.length;
					for(String holiday:holidays) holidayMap.put(holiday, days);
				}
			}
		}
		String dateStr;
		synchronized(df){
			dateStr=df.format(date);
		}
		if(holidayMap.get(dateStr)==null) return 0;
		return holidayMap.get(dateStr);
	}

}
