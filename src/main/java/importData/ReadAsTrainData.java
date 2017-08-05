package importData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.DataInLink;
import model.Link;

import org.apache.commons.lang3.StringUtils;

import controll.Constant;

public class ReadAsTrainData {

	public static List<DataInLink> readAsTrainData(Map<String,Link> links) throws ParseException, InterruptedException{
//		CopyOnWriteArrayList<DataInLink> dataInLinks=new CopyOnWriteArrayList<DataInLink>();
		
		List<String> uniqueKeys=Collections.synchronizedList(new ArrayList<String>());
		List<DataInLink> dataInLinks=Collections.synchronizedList(new ArrayList<DataInLink>());
		List<String> linkInfos=ReadDataAsString.readTxtFile(Constant.PATH_OF_TRAINING_DATA,Constant.SIZE_OF_DATA);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

		int totalThreadNum=10;
		int step=linkInfos.size()/totalThreadNum+1;

		List<Thread> convertTxtToDataLinkTs=new ArrayList<Thread>();

		for(int threadIndex=0;threadIndex<totalThreadNum;++threadIndex){
			ConvertTxtToDataLink convertTxtToDataLinkI=new ConvertTxtToDataLink(threadIndex,uniqueKeys,dataInLinks,linkInfos,df,df2,links,threadIndex*step,(threadIndex+1)*step-1);
			Thread convertTxtToDataLink=new Thread(convertTxtToDataLinkI);
			convertTxtToDataLinkTs.add(convertTxtToDataLink);
			convertTxtToDataLink.start();
		}

		for(Thread convertTxtToDataLink: convertTxtToDataLinkTs){
			convertTxtToDataLink.join();
		}

//		int count=0;
//		for(DataInLink dataInLink: dataInLinks){
//			if(dataInLink==null){
//				System.out.println(count);
//			}
//			count++;
//		}
		System.gc();
		return dataInLinks;
	}

}

class ConvertTxtToDataLink implements Runnable{
	int round;
	List<String> uniqueKeys;
	List<DataInLink> dataInLinks;
	List<String> linkInfos;
	DateFormat df;
	DateFormat df2;
	Map<String,Link> links;

	int startIndex;
	int endIndex;

	public ConvertTxtToDataLink(int round,List<String> uniqueKeys,List<DataInLink> dataInLinks,final List<String> linkInfos, DateFormat df, DateFormat df2, Map<String, Link> links, int startIndex, int endIndex) {
		this.round=round;
		this.uniqueKeys = uniqueKeys;
		this.dataInLinks = dataInLinks;
		this.linkInfos = linkInfos;
		this.df = df;
		this.df2 = df2;
		this.links = links;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public void run() {

		for(int index = startIndex; index<=endIndex && index<linkInfos.size(); ++index){
			if(index==0) continue;
			String linkInfo=linkInfos.get(index);
			String[] linkFields=linkInfo.split(";");
			if(linkFields.length!=4) continue;

			synchronized(this){
				DataInLink dataInLink=new DataInLink();
				dataInLink.setLink(links.get(linkFields[0]));
				String[] times=linkFields[2].substring(1, linkFields[2].length()-1).split(",");
				if(StringUtils.isNotEmpty(linkFields[1])){
					try{
						synchronized(df){
							dataInLink.setDate(df.parse(linkFields[1].trim()));
						}
					}catch(ParseException e){
						System.out.println("Thread : "+round + "# index :"+index+" date: "+linkFields[1]);
					}
					
				}
				if(StringUtils.isNotEmpty(times[0])){
					try{
						synchronized(df2){
							dataInLink.setStartTime(df2.parse(times[0].trim()));
						}
					}catch(ParseException e){
						System.out.println("Thread "+round + "# index :"+index+" Start time: "+times[0]);
					}
				}
				if(StringUtils.isNotEmpty(times[1])){
					try{
						synchronized(df2){
							dataInLink.setEndTime(df2.parse(times[1].trim()));
						}
					}catch(ParseException e){
						System.out.println("Thread "+round + "# index :"+index+" end time: "+times[1]);
					}
				}
				
				dataInLink.setTravle_time(new Double(linkFields[3]));
				if(uniqueKeys.contains(dataInLink.getLink().getLink_ID()+times[0].trim())) continue;
				dataInLinks.add(dataInLink);
			}
		}
		System.gc();
	}

	public List<DataInLink> getDataInLinks() {
		return dataInLinks;
	}

	public void setDataInLinks(List<DataInLink> dataInLinks) {
		this.dataInLinks = dataInLinks;
	}

	public List<String> getLinkInfos() {
		return linkInfos;
	}

	public void setLinkInfos(List<String> linkInfos) {
		this.linkInfos = linkInfos;
	}

	public Map<String, Link> getLinks() {
		return links;
	}

	public void setLinks(Map<String, Link> links) {
		this.links = links;
	}

	public DateFormat getDf() {
		return df;
	}

	public void setDf(DateFormat df) {
		this.df = df;
	}

	public DateFormat getDf2() {
		return df2;
	}

	public void setDf2(DateFormat df2) {
		this.df2 = df2;
	}
}
