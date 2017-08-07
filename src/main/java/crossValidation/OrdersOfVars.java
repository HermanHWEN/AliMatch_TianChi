package crossValidation;

import java.util.ArrayList;
import java.util.List;

import model.FlatData;

public class OrdersOfVars {
	
	private int lengthO;
	private int widthO;
	private int linkClassO;
	private int startTimeO;
	private int weightO; 
	private int holidayDaysO;
	private int dayInWeekO;
	private int dateO;
	
	private int orderOfPower;
	
	public static List<OrdersOfVars> getOrders(int maxOrder,int maxParametersNum,FlatData standardDeviation){
		List<OrdersOfVars> ordersOfVarsList = new ArrayList<>();
		int parametersNum=0;
		for(int order=0;order<=maxOrder;order++){

			int startLengthO=order;
			int endLengthO=0;
			if(standardDeviation.getLength()==0) 
				startLengthO=0;
			
			for(int lengthO=startLengthO;lengthO>=endLengthO;lengthO--){

				int startWidthO=order-lengthO;
				int endWidthO=0;
				if(standardDeviation.getWidth()==0)
					startWidthO=0;
				if(standardDeviation.getLinkClass()==0 && standardDeviation.getStartTime()==0 
						&& standardDeviation.getWeight()==0 && standardDeviation.getHolidayDays()==0 
						&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
					endWidthO=startWidthO;
				
				for(int widthO=startWidthO;widthO>=endWidthO;widthO--){

					int startLinkClassO=order-lengthO-widthO;
					int endLinkClassO=0;
					if(standardDeviation.getLinkClass()==0)
						startLinkClassO=0;
					if(standardDeviation.getStartTime()==0 && standardDeviation.getWeight()==0 && standardDeviation.getHolidayDays()==0 
							&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
						endLinkClassO=startLinkClassO;
					
					for(int linkClassO=startLinkClassO;linkClassO>=endLinkClassO;linkClassO--){
						
						int startStartTimeO=order-lengthO-widthO-linkClassO;
						int endStartTimeO=0;
						if(standardDeviation.getStartTime()==0) 
							startStartTimeO=0;
						if(standardDeviation.getWeight()==0 && standardDeviation.getHolidayDays()==0 
								&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
							endStartTimeO=startStartTimeO;
						
						for(int startTimeO=startStartTimeO;startTimeO>=endStartTimeO;startTimeO--){
							
							int startWeightO=order-lengthO-widthO-linkClassO-startTimeO;
							int endWeightO=0;
							if(standardDeviation.getWeight()==0)
								startWeightO=0;
							if(standardDeviation.getHolidayDays()==0 && standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
								endWeightO=startWeightO;
							
							for(int weightO=startWeightO;weightO>=endWeightO;weightO--){
								
								int startHolidayDaysO=order-lengthO-widthO-linkClassO-startTimeO-weightO;
								int endHolidayDaysO=0;
								if(standardDeviation.getHolidayDays()==0) 
									startHolidayDaysO=0;
								if(standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0) 
									endHolidayDaysO=startHolidayDaysO;
								
								for(int holidayDaysO=startHolidayDaysO;holidayDaysO>=endHolidayDaysO;holidayDaysO--){
									
									int startDayInWeeksO=order-lengthO-widthO-linkClassO-startTimeO-weightO-holidayDaysO;
									int endDayInWeekO=0;
									if(standardDeviation.getDayInWeek()==0) 
										startDayInWeeksO=0;
									if(standardDeviation.getDate()==0)
										endDayInWeekO=startDayInWeeksO;
									
									for(int dayInWeekO=startDayInWeeksO;dayInWeekO>=endDayInWeekO;dayInWeekO--){
										
										int startDateO=order-lengthO-widthO-linkClassO-startTimeO-weightO-holidayDaysO-dayInWeekO;
										int endDateO=startDateO;
										if(standardDeviation.getDate()==0) 
											startDateO=0;
										
										for(int dateO=startDateO;dateO>=endDateO;dateO--){
											
											OrdersOfVars ordersOfVars=new OrdersOfVars();
											ordersOfVars.setLengthO(lengthO);
											ordersOfVars.setWidthO(widthO);
											ordersOfVars.setLinkClassO(linkClassO);
											ordersOfVars.setWeightO(weightO);
											ordersOfVars.setDateO(dateO);
											ordersOfVars.setStartTimeO(startTimeO);
											ordersOfVars.setHolidayDaysO(holidayDaysO);
											ordersOfVars.setDayInWeekO(dayInWeekO);
											ordersOfVars.setOrderOfPower(order);
											ordersOfVarsList.add(ordersOfVars);
											parametersNum++;
											if(parametersNum>maxParametersNum && maxParametersNum!=-1) return ordersOfVarsList;
										}
									}
								}
							}

						}
					}
				}
			}
		}
		return ordersOfVarsList;

	}
	
	public static List<String> getOrdersStr(int maxOrder,int maxParametersNum,FlatData standardDeviation){
		List<String> strRes=new ArrayList<>();
		List<OrdersOfVars> res=OrdersOfVars.getOrders(maxOrder,maxParametersNum,standardDeviation);
		for(OrdersOfVars r:res){
			strRes.add(r.toString());
		}
		return strRes;
	}

	

	public static int getParametersNum(int maxOrder,FlatData standardDeviation){
		return OrdersOfVars.getOrders(maxOrder,-1,standardDeviation).size()-1;
	}
	@Override
	public String toString() {
		
		StringBuffer res=new StringBuffer();
		if(lengthO!=0)
			res.append("length^"+lengthO+"*");
		if(widthO!=0)
			res.append("width^"+widthO+"*");
		if(linkClassO!=0)
			res.append("linkClass^"+linkClassO+"*");
		if(startTimeO!=0)
			res.append("startTime^"+startTimeO+"*");
		if(weightO!=0)
			res.append("weight^"+weightO+"*");
		if(holidayDaysO!=0)
			res.append("holidayDays^"+holidayDaysO+"*");
		if(dayInWeekO!=0)
			res.append("dayInWeek^"+dayInWeekO+"*");
		if(dateO!=0)
			res.append("dateInMonth^"+dateO+"*");
		if(lengthO+widthO+linkClassO+startTimeO+weightO+holidayDaysO+dayInWeekO+dateO==0){
			return "1";
		}
		return res.substring(0, res.length()-1);
	}
	
	

	public int getLengthO() {
		return lengthO;
	}
	public void setLengthO(int lengthO) {
		this.lengthO = lengthO;
	}
	public int getWidthO() {
		return widthO;
	}
	public void setWidthO(int widthO) {
		this.widthO = widthO;
	}
	public int getLinkClassO() {
		return linkClassO;
	}
	public void setLinkClassO(int classO) {
		this.linkClassO = classO;
	}
	public int getWeightO() {
		return weightO;
	}
	public void setWeightO(int weightO) {
		this.weightO = weightO;
	}
	public int getDateO() {
		return dateO;
	}
	public void setDateO(int dateO) {
		this.dateO = dateO;
	}
	public int getStartTimeO() {
		return startTimeO;
	}
	public void setStartTimeO(int startTimeO) {
		this.startTimeO = startTimeO;
	}

	public int getHolidayDaysO() {
		return holidayDaysO;
	}

	public void setHolidayDaysO(int holidayDaysO) {
		this.holidayDaysO = holidayDaysO;
	}

	public int getDayInWeekO() {
		return dayInWeekO;
	}

	public void setDayInWeekO(int dayInWeekO) {
		this.dayInWeekO = dayInWeekO;
	}

	public int getOrderOfPower() {
		return orderOfPower;
	}

	public void setOrderOfPower(int orderOfPower) {
		this.orderOfPower = orderOfPower;
	}
	
	
	
}
