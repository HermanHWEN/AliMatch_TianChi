package calculateFeatures;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Holiday {

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private static List<String[]> rawHolidayMap=new ArrayList<String[]>(){
		private static final long serialVersionUID = 1L;
		{
			add(new String[]{"2016-01-01", "2016-01-03"});
			add(new String[]{"2016-02-07", "2016-02-13"});
			add(new String[]{"2016-04-02", "2016-04-04"});
			add(new String[]{"2016-04-30", "2016-05-02"});
			add(new String[]{"2016-06-09", "2016-06-11"});
			add(new String[]{"2016-09-15", "2016-09-17"});
			add(new String[]{"2016-10-01", "2016-10-07"});
			add(new String[]{"2016-12-31", "2017-01-02"});
			add(new String[]{"2017-01-27", "2017-02-02"});
			add(new String[]{"2017-04-02", "2017-04-04"});
			add(new String[]{"2017-04-29", "2017-05-01"});
			add(new String[]{"2017-05-28", "2017-05-30"});
			add(new String[]{"2017-10-01", "2017-10-08"});
		}
	};
	
	private static List<String[]> rawNotHolidayMap=new ArrayList<String[]>(){
		private static final long serialVersionUID = 1L;
		{
			add(new String[]{"2016-02-06"});
			add(new String[]{"2016-02-14"});
			add(new String[]{"2016-06-12"});
			add(new String[]{"2016-09-18"});
			add(new String[]{"2016-10-08", "2016-10-09"});
			add(new String[]{"2017-01-22"});
			add(new String[]{"2017-02-04"});
			add(new String[]{"2017-04-01"});
			add(new String[]{"2017-05-27"});
			add(new String[]{"2017-09-30"});
		}
	};
	private static Map<String,Long> holidayMap=new HashMap<String,Long>();
	private static Map<String,Long> notHolidayMap=new HashMap<String,Long>();
	public static synchronized Long getHolidayDays(Date date) throws ParseException{
		synchronized(holidayMap){
			holidayMap=getMap(rawHolidayMap);
		}
		synchronized(notHolidayMap){
			notHolidayMap=getMap(rawNotHolidayMap);
		}
		String dateStr;
		synchronized(df){
			dateStr=df.format(date);
		}
		
		Calendar dateC=Calendar.getInstance();
		dateC.setTime(date);
		if(holidayMap.get(dateStr)==null){
			if(notHolidayMap.get(dateStr)!=null){//work for sure
				return (long) 0;
			}else{
				if(dateC.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){//Saturday and not for work
					dateC.add(Calendar.DATE, 1);
					if(notHolidayMap.get(df.format(dateC.getTime()))==null){//And Sunday don't work too.
						return (long) 2;
					}else{
						return (long) 1;
					}
				}else if(dateC.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){//Sunday and not for work
					dateC.add(Calendar.DATE, -1);
					if(notHolidayMap.get(df.format(dateC.getTime()))==null){//And yesterday(Saturday) don't work too.
						return (long) 2;
					}else{
						return (long) 1;
					}
				}else{//normal working day
					return (long) 0;
				}
			}
		}
		return holidayMap.get(dateStr);
		
	}
	
	private static Map<String,Long> getMap(List<String[]> rawMap) throws ParseException{
		if(holidayMap.size()==0){
			for(String[] holidays:rawMap){
				if(holidays.length==0) continue;
				if(holidays.length==1){
					holidayMap.put(holidays[0], (long) 1);
				}else{
					Calendar startDate=Calendar.getInstance();
					Calendar endDate=Calendar.getInstance();
					startDate.setTime(df.parse(holidays[0]));
					endDate.setTime(df.parse(holidays[1]));

					if(endDate.before(startDate)){
						Calendar tmp=startDate;
						startDate=endDate;
						endDate=tmp;
					}

					long days=(endDate.getTimeInMillis()-startDate.getTimeInMillis())/(1000 * 60 * 60 *24)+1;
					endDate.add(Calendar.DATE, 1);
					while(startDate.before(endDate)){
						String holiday = df.format(startDate.getTime());
						holidayMap.put(holiday, days);
						startDate.add(Calendar.DATE, 1);
					}
				}

			}
		}
		return holidayMap;
	}
}
