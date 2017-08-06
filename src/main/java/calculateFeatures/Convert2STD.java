package calculateFeatures;

import java.util.ArrayList;
import java.util.List;

import model.DataInLink;
import model.FlatData;

public class Convert2STD {
	public static void convert2STD(List<DataInLink> dataInLinks){
		
		FlatData average=new FlatData();
		FlatData standardDeviation=new FlatData();
		
		FlatData min=new FlatData();
		FlatData max=new FlatData();
		//get average
		for(int index=0;index<dataInLinks.size();index++){
			DataInLink dataInLink=dataInLinks.get(index);
			average.setLength(average.getLength()+dataInLink.getLink().getLength());
			average.setWidth(average.getWidth()+dataInLink.getLink().getWidth());
			average.setLinkClass(average.getLinkClass()+dataInLink.getLink().getLinkClass());
			average.setWeight(average.getWeight()+dataInLink.getLink().getWeight());
			average.setDate(average.getDate()+dataInLink.getDate().getDate());
			average.setStartTime(average.getStartTime()+dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2);
			average.setHolidayDays(average.getHolidayDays()+dataInLink.getHolidayDays());
			average.setDayInWeek(average.getDayInWeek()+dataInLink.getDayInWeek());
			
			min.setLength(dataInLink.getLink().getLength()<min.getLength()||index==0?dataInLink.getLink().getLength():min.getLength());
			min.setWidth(dataInLink.getLink().getWidth()<min.getWidth()||index==0?dataInLink.getLink().getWidth():min.getWidth());
			min.setLinkClass(dataInLink.getLink().getLinkClass()<min.getLinkClass()||index==0?dataInLink.getLink().getLinkClass():min.getLinkClass());
			min.setWeight(dataInLink.getLink().getWeight()<min.getWeight()||index==0?dataInLink.getLink().getWeight():min.getWeight());
			min.setDate(dataInLink.getDate().getDate()<min.getDate()||index==0?dataInLink.getDate().getDate():min.getDate());
			min.setStartTime(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2<min.getStartTime()||index==0?dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2:min.getStartTime());
			min.setHolidayDays(dataInLink.getHolidayDays()<min.getHolidayDays()||index==0?dataInLink.getHolidayDays():min.getHolidayDays());
			min.setDayInWeek(dataInLink.getDayInWeek()<min.getDayInWeek()||index==0?dataInLink.getDayInWeek():min.getDayInWeek());
			
			max.setLength(dataInLink.getLink().getLength()>max.getLength()||index==0?dataInLink.getLink().getLength():max.getLength());
			max.setWidth(dataInLink.getLink().getWidth()>max.getWidth()||index==0?dataInLink.getLink().getWidth():max.getWidth());
			max.setLinkClass(dataInLink.getLink().getLinkClass()>max.getLinkClass()||index==0?dataInLink.getLink().getLinkClass():max.getLinkClass());
			max.setWeight(dataInLink.getLink().getWeight()>max.getWeight()||index==0?dataInLink.getLink().getWeight():max.getWeight());
			max.setDate(dataInLink.getDate().getDate()>max.getDate()||index==0?dataInLink.getDate().getDate():max.getDate());
			max.setStartTime(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2>max.getStartTime()||index==0?dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2:max.getStartTime());
			max.setHolidayDays(dataInLink.getHolidayDays()>max.getHolidayDays()||index==0?dataInLink.getHolidayDays():max.getHolidayDays());
			max.setDayInWeek(dataInLink.getDayInWeek()>max.getDayInWeek()||index==0?dataInLink.getDayInWeek():max.getDayInWeek());
		}
		average.setLength(average.getLength()/dataInLinks.size());
		average.setWidth(average.getWidth()/dataInLinks.size());
		average.setLinkClass(average.getLinkClass()/dataInLinks.size());
		average.setWeight(average.getWeight()/dataInLinks.size());
		average.setDate(average.getDate()/dataInLinks.size());
		average.setStartTime(average.getStartTime()/dataInLinks.size());
		average.setHolidayDays(average.getHolidayDays()/dataInLinks.size());
		average.setDayInWeek(average.getDayInWeek()/dataInLinks.size());

		//get standardDeviation
		for(DataInLink dataInLink: dataInLinks){
			standardDeviation.setLength(standardDeviation.getLength()+Math.pow(average.getLength()-dataInLink.getLink().getLength(),2));
			standardDeviation.setWidth(standardDeviation.getWidth()+Math.pow(average.getWidth()-dataInLink.getLink().getWidth(),2));
			standardDeviation.setLinkClass(standardDeviation.getLinkClass()+Math.pow(average.getLinkClass()-dataInLink.getLink().getLinkClass(),2));
			standardDeviation.setWeight(standardDeviation.getWeight()+Math.pow(average.getWeight()-dataInLink.getLink().getWeight(),2));
			standardDeviation.setDate(standardDeviation.getDate()+Math.pow(average.getDate()-dataInLink.getDate().getDate(),2));
			standardDeviation.setStartTime(standardDeviation.getStartTime()+Math.pow(average.getStartTime()-(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2),2));
			standardDeviation.setHolidayDays(standardDeviation.getHolidayDays()+Math.pow(average.getHolidayDays()-dataInLink.getHolidayDays(),2));
			standardDeviation.setDayInWeek(standardDeviation.getDayInWeek()+Math.pow(average.getDayInWeek()-dataInLink.getDayInWeek(),2));
		}
		standardDeviation.setLength(Math.sqrt(standardDeviation.getLength()/dataInLinks.size()));
		standardDeviation.setWidth(Math.sqrt(standardDeviation.getWidth()/dataInLinks.size()));
		standardDeviation.setLinkClass(Math.sqrt(standardDeviation.getLinkClass()/dataInLinks.size()));
		standardDeviation.setWeight(Math.sqrt(standardDeviation.getWeight()/dataInLinks.size()));
		standardDeviation.setDate(Math.sqrt(standardDeviation.getDate()/dataInLinks.size()));
		standardDeviation.setStartTime(Math.sqrt(standardDeviation.getStartTime()/dataInLinks.size()));
		standardDeviation.setHolidayDays(Math.sqrt(standardDeviation.getHolidayDays()/dataInLinks.size()));
		standardDeviation.setDayInWeek(Math.sqrt(standardDeviation.getDayInWeek()/dataInLinks.size()));
		
		
		//convert data
		for(DataInLink dataInLink: dataInLinks){
			dataInLink.setAverage(average);
			dataInLink.setStandardDeviation(standardDeviation);
			
			dataInLink.setMin(min);
			dataInLink.setMax(max);
		}
	}
}
