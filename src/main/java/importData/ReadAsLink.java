package importData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controll.Constant;
import model.Link;

public class ReadAsLink {

	public static Map<String,Link> readAsLink(){
		//read link info
		Map<String,Link> linksMap=new HashMap<String,Link>();
		List<String> linkInfos=ReadDataAsString.readTxtFile(Constant.PATH_OF_LINK_INFO,-1);
		for(int index=1;index<linkInfos.size();++index){
			String linkInfo=linkInfos.get(index);
			String[] linkFields=linkInfo.split(";");
			if(linkFields.length!=4) continue;
			Link link=new Link();
			link.setLink_ID(linkFields[0]);
			link.setLength(new Double(linkFields[1]));
			link.setWidth(new Double(linkFields[2]));
			link.setLinkClass(new Integer(linkFields[3]));
			linksMap.put(link.getLink_ID(), link);
		}
		
		//read in and out info
		List<String> linkInOutInfos=ReadDataAsString.readTxtFile(Constant.PATH_OF_LINK_TOP,-1);
		for(int index=1;index<linkInOutInfos.size();++index){
			String linkInOutInfo=linkInOutInfos.get(index);
			String[] linkFields=linkInOutInfo.split(";");
			if(linkFields.length!=3) continue;
			Link link=linksMap.get(linkFields[0]);
			
			//set in_links
			if(!"".equals(linkFields[1]) && linkFields[1]!=null){
				String[] inLinks=linkFields[1].split("#");
				link.setIn_links(Arrays.asList(inLinks));
			}
			
			//set out_links
			if(!"".equals(linkFields[2]) && linkFields[2]!=null){
				String[] outLinks=linkFields[2].split("#");
				link.setOut_links(Arrays.asList(outLinks));
			}
		}
		
		return linksMap;
		
	}
}
