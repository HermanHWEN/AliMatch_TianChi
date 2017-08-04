package calculateFeatures;

import java.util.ArrayList;
import java.util.List;

import model.DataInLink;
import model.FlatData;
import model.STDDataInLink;

public class Convert2STD {
	public static List<DataInLink> convert2STD(List<DataInLink> dataInLinks) throws CloneNotSupportedException{
		
		List<DataInLink> stdDataInLinks=new ArrayList<DataInLink>();
		FlatData average=new FlatData();
		FlatData standardDeviation=new FlatData();

		//get average
		for(DataInLink dataInLink: dataInLinks){
			average.setLength(average.getLength()+dataInLink.getLink().getLength());
			average.setWidth(average.getWidth()+dataInLink.getLink().getWidth());
			average.setClassLevel(average.getClassLevel()+dataInLink.getLink().getLink_class());
			average.setWeight(average.getWeight()+dataInLink.getLink().getWeight());
			average.setDate(average.getDate()+dataInLink.getDate().getDate());
			average.setStartTime(average.getStartTime()+dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2);
		}
		average.setLength(average.getLength()/dataInLinks.size());
		average.setWidth(average.getWidth()/dataInLinks.size());
		average.setClassLevel(average.getClassLevel()/dataInLinks.size());
		average.setWeight(average.getWeight()/dataInLinks.size());
		average.setDate(average.getDate()/dataInLinks.size());
		average.setStartTime(average.getStartTime()/dataInLinks.size());


		//get standardDeviation
		for(DataInLink dataInLink: dataInLinks){
			standardDeviation.setLength(Math.pow(average.getLength()-dataInLink.getLink().getLength(),2));
			standardDeviation.setWidth(Math.pow(average.getWidth()-dataInLink.getLink().getWidth(),2));
			standardDeviation.setClassLevel(Math.pow(average.getClassLevel()-dataInLink.getLink().getLink_class(),2));
			standardDeviation.setWeight(Math.pow(average.getWeight()-dataInLink.getLink().getWeight(),2));
			standardDeviation.setDate(Math.pow(average.getDate()-dataInLink.getDate().getDate(),2));
			standardDeviation.setStartTime(Math.pow(average.getStartTime()-(dataInLink.getStartTime().getHours()*30+dataInLink.getStartTime().getMinutes()/2),2));
		}
		standardDeviation.setLength(Math.sqrt(average.getLength()/dataInLinks.size()));
		standardDeviation.setWidth(Math.sqrt(average.getWidth()/dataInLinks.size()));
		standardDeviation.setClassLevel(Math.sqrt(average.getClassLevel()/dataInLinks.size()));
		standardDeviation.setWeight(Math.sqrt(average.getWeight()/dataInLinks.size()));
		standardDeviation.setDate(Math.sqrt(average.getDate()/dataInLinks.size()));
		standardDeviation.setStartTime(Math.sqrt(average.getStartTime()/dataInLinks.size()));
		
		
		//convert data
		for(DataInLink dataInLink: dataInLinks){
			DataInLink stdDataInLink=dataInLink.clone();
//			stdDataInLink.set
		}
		return stdDataInLinks;
	}
}
