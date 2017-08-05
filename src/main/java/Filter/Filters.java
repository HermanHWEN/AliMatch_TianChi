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
	
	public static synchronized boolean shouldAdd(DataInLink dataInLink){
		return startTimeShouldAdd(dataInLink) && monthShouldAdd(dataInLink);
	}
	public static synchronized boolean startTimeShouldAdd(DataInLink dataInLink){
		if(Constant.STARTHOUR_RANGE.length==0) 
			return true;
		
		if(dataInLink.getStartTime().getHours()>=Constant.STARTHOUR_RANGE[0] && dataInLink.getStartTime().getHours()<Constant.STARTHOUR_RANGE[1])
			return true;
		return false;
		
	}
	
	public static synchronized boolean monthShouldAdd(DataInLink dataInLink){
		
		if(Constant.NEED_MONTH.length==0) 
			return true;
		
		for(int month:Constant.NEED_MONTH){
			if(month==dataInLink.getDate().getMonth()+1) 
				return true;
		}
		return false;
		
	}
}
