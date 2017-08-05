package Filter;

import java.util.Iterator;
import java.util.List;

import controll.Constant;
import model.DataInLink;

public class Filters {
	
	public static synchronized List<DataInLink> filterStartTime(List<DataInLink> dataInLinks){
		Iterator<DataInLink> it=dataInLinks.iterator();
		while(it.hasNext()){
			DataInLink dataInLink = it.next();
            if(dataInLink.getStartTime().getHours()!=8){
            	it.remove();
            }
        }
		return dataInLinks;
		
	}
	
	public static synchronized boolean startTimeShouldRemoved(DataInLink dataInLink){
		
		if(dataInLink.getStartTime().getHours()<Constant.STARTHOUR_RANGE[0] || dataInLink.getStartTime().getHours()>=Constant.STARTHOUR_RANGE[1])
			return true;
		return false;
		
	}
}
