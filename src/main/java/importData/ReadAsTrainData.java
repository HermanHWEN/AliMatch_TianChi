package importData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.DataInLink;
import model.Link;

public class ReadAsTrainData {
	
	public static List<DataInLink> readAsTrainData(Map<String,Link> links) throws ParseException{
		List<DataInLink> dataInLinks=new ArrayList<DataInLink>();
		List<String> linkInfos=ReadDataAsString.readTxtFile(ConstantPath.PATH_OF_TRAINING_DATA);
		for(int index=1;index<linkInfos.size();++index){
			String linkInfo=linkInfos.get(index);
			String[] linkFields=linkInfo.split(";");
			if(linkFields.length!=4) continue;
			
			DataInLink dataInLink=new DataInLink();
			DateFormat df=new SimpleDateFormat("yyyy-mm-dd",Locale.ENGLISH);
			dataInLink.setLink(links.get(linkFields[0]));
			dataInLink.setDate(df.parse(linkFields[1]));
			String[] times=linkFields[2].substring(1, linkFields[2].length()-1).split(",");
			
			DateFormat df2=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss",Locale.ENGLISH);
			dataInLink.setStartTime(df2.parse(times[0]));
			
			dataInLink.setEndTime(df2.parse(times[1]));
			
			dataInLink.setTravle_time(new Double(linkFields[3]));
			dataInLinks.add(dataInLink);
		}
		return dataInLinks;
	}

}
