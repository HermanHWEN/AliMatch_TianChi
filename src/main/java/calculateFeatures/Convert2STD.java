package calculateFeatures;

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
			average.setReciprocalOfWidth(average.getReciprocalOfWidth()+dataInLink.getLink().getReciprocalOfWidth());
			average.setLinkClass(average.getLinkClass()+dataInLink.getLink().getLinkClass());
			average.setWeightFromInLink(average.getWeightFromInLink()+dataInLink.getLink().getWeightFromInLink());
			average.setWeightFromOutLink(average.getWeightFromOutLink()+dataInLink.getLink().getWeightFromOutLink());
			average.setDate(average.getDate()+dataInLink.getDate().getDate());
			average.setStartTime(average.getStartTime()+dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2);
			average.setHolidayDays(average.getHolidayDays()+dataInLink.getHolidayDays());
			average.setDayInWeek(average.getDayInWeek()+dataInLink.getDayInWeek());
			
			min.setLength(dataInLink.getLink().getLength()<min.getLength()||index==0?dataInLink.getLink().getLength():min.getLength());
			min.setReciprocalOfWidth(dataInLink.getLink().getReciprocalOfWidth()<min.getReciprocalOfWidth()||index==0?dataInLink.getLink().getReciprocalOfWidth():min.getReciprocalOfWidth());
			min.setLinkClass(dataInLink.getLink().getLinkClass()<min.getLinkClass()||index==0?dataInLink.getLink().getLinkClass():min.getLinkClass());
			min.setWeightFromInLink(dataInLink.getLink().getWeightFromInLink()<min.getWeightFromInLink()||index==0?dataInLink.getLink().getWeightFromInLink():min.getWeightFromInLink());
			min.setWeightFromOutLink(dataInLink.getLink().getWeightFromOutLink()<min.getWeightFromOutLink()||index==0?dataInLink.getLink().getWeightFromOutLink():min.getWeightFromOutLink());
			min.setDate(dataInLink.getDate().getDate()<min.getDate()||index==0?dataInLink.getDate().getDate():min.getDate());
			min.setStartTime(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2<min.getStartTime()||index==0?dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2:min.getStartTime());
			min.setHolidayDays(dataInLink.getHolidayDays()<min.getHolidayDays()||index==0?dataInLink.getHolidayDays():min.getHolidayDays());
			min.setDayInWeek(dataInLink.getDayInWeek()<min.getDayInWeek()||index==0?dataInLink.getDayInWeek():min.getDayInWeek());
			
			max.setLength(dataInLink.getLink().getLength()>max.getLength()||index==0?dataInLink.getLink().getLength():max.getLength());
			max.setReciprocalOfWidth(dataInLink.getLink().getReciprocalOfWidth()>max.getReciprocalOfWidth()||index==0?dataInLink.getLink().getReciprocalOfWidth():max.getReciprocalOfWidth());
			max.setLinkClass(dataInLink.getLink().getLinkClass()>max.getLinkClass()||index==0?dataInLink.getLink().getLinkClass():max.getLinkClass());
			max.setWeightFromInLink(dataInLink.getLink().getWeightFromInLink()>max.getWeightFromInLink()||index==0?dataInLink.getLink().getWeightFromInLink():max.getWeightFromInLink());
			max.setWeightFromOutLink(dataInLink.getLink().getWeightFromOutLink()>max.getWeightFromOutLink()||index==0?dataInLink.getLink().getWeightFromOutLink():max.getWeightFromOutLink());
			max.setDate(dataInLink.getDate().getDate()>max.getDate()||index==0?dataInLink.getDate().getDate():max.getDate());
			max.setStartTime(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2>max.getStartTime()||index==0?dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2:max.getStartTime());
			max.setHolidayDays(dataInLink.getHolidayDays()>max.getHolidayDays()||index==0?dataInLink.getHolidayDays():max.getHolidayDays());
			max.setDayInWeek(dataInLink.getDayInWeek()>max.getDayInWeek()||index==0?dataInLink.getDayInWeek():max.getDayInWeek());
		}
		average.setLength(average.getLength()/dataInLinks.size());
		average.setReciprocalOfWidth(average.getReciprocalOfWidth()/dataInLinks.size());
		average.setLinkClass(average.getLinkClass()/dataInLinks.size());
		average.setWeightFromInLink(average.getWeightFromInLink()/dataInLinks.size());
		average.setWeightFromOutLink(average.getWeightFromOutLink()/dataInLinks.size());
		average.setDate(average.getDate()/dataInLinks.size());
		average.setStartTime(average.getStartTime()/dataInLinks.size());
		average.setHolidayDays(average.getHolidayDays()/dataInLinks.size());
		average.setDayInWeek(average.getDayInWeek()/dataInLinks.size());

		//get standardDeviation
		for(DataInLink dataInLink: dataInLinks){
			standardDeviation.setLength(standardDeviation.getLength()+Math.pow(average.getLength()-dataInLink.getLink().getLength(),2));
			standardDeviation.setReciprocalOfWidth(standardDeviation.getReciprocalOfWidth()+Math.pow(average.getReciprocalOfWidth()-dataInLink.getLink().getReciprocalOfWidth(),2));
			standardDeviation.setLinkClass(standardDeviation.getLinkClass()+Math.pow(average.getLinkClass()-dataInLink.getLink().getLinkClass(),2));
			standardDeviation.setWeightFromInLink(standardDeviation.getWeightFromInLink()+Math.pow(average.getWeightFromInLink()-dataInLink.getLink().getWeightFromInLink(),2));
			standardDeviation.setWeightFromOutLink(standardDeviation.getWeightFromOutLink()+Math.pow(average.getWeightFromOutLink()-dataInLink.getLink().getWeightFromOutLink(),2));
			standardDeviation.setDate(standardDeviation.getDate()+Math.pow(average.getDate()-dataInLink.getDate().getDate(),2));
			standardDeviation.setStartTime(standardDeviation.getStartTime()+Math.pow(average.getStartTime()-(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2),2));
			standardDeviation.setHolidayDays(standardDeviation.getHolidayDays()+Math.pow(average.getHolidayDays()-dataInLink.getHolidayDays(),2));
			standardDeviation.setDayInWeek(standardDeviation.getDayInWeek()+Math.pow(average.getDayInWeek()-dataInLink.getDayInWeek(),2));
		}
		standardDeviation.setLength(Math.sqrt(standardDeviation.getLength()/dataInLinks.size()));
		standardDeviation.setReciprocalOfWidth(Math.sqrt(standardDeviation.getReciprocalOfWidth()/dataInLinks.size()));
		standardDeviation.setLinkClass(Math.sqrt(standardDeviation.getLinkClass()/dataInLinks.size()));
		standardDeviation.setWeightFromInLink(Math.sqrt(standardDeviation.getWeightFromInLink()/dataInLinks.size()));
		standardDeviation.setWeightFromOutLink(Math.sqrt(standardDeviation.getWeightFromOutLink()/dataInLinks.size()));
		standardDeviation.setDate(Math.sqrt(standardDeviation.getDate()/dataInLinks.size()));
		standardDeviation.setStartTime(Math.sqrt(standardDeviation.getStartTime()/dataInLinks.size()));
		standardDeviation.setHolidayDays(Math.sqrt(standardDeviation.getHolidayDays()/dataInLinks.size()));
		standardDeviation.setDayInWeek(Math.sqrt(standardDeviation.getDayInWeek()/dataInLinks.size()));
		
		//exclude day of month
		standardDeviation.setDate(0);
		
		//convert data
		for(DataInLink dataInLink: dataInLinks){
			dataInLink.setAverage(average);
			dataInLink.setStandardDeviation(standardDeviation);
			
			dataInLink.setMin(min);
			dataInLink.setMax(max);
		}
	}
}
