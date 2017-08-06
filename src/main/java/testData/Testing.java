package testData;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import calculateFeatures.Holiday;
import model.DataInLink;
import model.Link;

public class Testing {

	public static void testing(Function<DataInLink,Double> targetFunction,List<DataInLink> testDataSet){

		for(DataInLink testData:testDataSet){
			testData.setTravle_time(targetFunction.apply(testData));
		}

	}

	public static List<DataInLink> getTestDataSet(Map<String,Link> linksMap){
		List<DataInLink> testDataSet=new LinkedList<>();
		int year=2017;
		int month=6-1;


//		int count=0;
		for(Link link:linksMap.values()){
//			if(count!=0) break;
			for(int day=1;day<=30;day++){
				Calendar calendar=Calendar.getInstance();
				calendar.set(year, month, day);
				Date date=calendar.getTime();
				for(int twominusStep=0;twominusStep<30;twominusStep++){
					DataInLink testData=new DataInLink();
					testData.setDate(date);

					testData.setLink(link);

					calendar.set(year, month, day, 8, twominusStep*2, 0);
					testData.setStartTime(calendar.getTime());

					calendar.set(year, month, day, 8, twominusStep*2+2, 0);
					testData.setEndTime(calendar.getTime());

					try {
						testData.setHolidayDays(Holiday.getHolidayDays(testData.getDate()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					testData.setDayInWeek(testData.getDate().getDay());
					
					testDataSet.add(testData);
//					count++;

				}

			}
		}
		return testDataSet;
	}

	public static List<DataInLink> getTestDataSetOfFirstLink(Map<String,Link> linksMap){
		List<DataInLink> testDataSet=new LinkedList<>();
		int year=2017;
		int month=6-1;


		int count=0;
		for(Link link:linksMap.values()){
			if(count!=0) break;
			for(int day=1;day<=30;day++){
				Calendar calendar=Calendar.getInstance();
				calendar.set(year, month, day);
				Date date=calendar.getTime();
				for(int twominusStep=0;twominusStep<30;twominusStep++){
					DataInLink testData=new DataInLink();
					testData.setDate(date);

					testData.setLink(link);

					calendar.set(year, month, day, 8, twominusStep*2, 0);
					testData.setStartTime(calendar.getTime());

					calendar.set(year, month, day, 8, twominusStep*2+2, 0);
					testData.setEndTime(calendar.getTime());

					testDataSet.add(testData);
					count++;

				}

			}
		}
		return testDataSet;
	}

}
