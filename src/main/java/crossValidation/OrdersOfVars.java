package crossValidation;

import java.util.ArrayList;
import java.util.List;

import model.FlatData;

public class OrdersOfVars {

	private int lastTravelTimeO;
	private int lengthO;
	private int reciprocalOfWidthO;
	private int linkClassO;
	private int startTimeO;
	private int weightFromInLinkO;
	private int weightFromOutLinkO; 
	private int holidayDaysO;
	private int dayInWeekO;
	private int dateO;

	private int orderOfPower;

	public static List<OrdersOfVars> getOrders(int maxOrder,int maxParametersNum,FlatData standardDeviation){
		List<OrdersOfVars> ordersOfVarsList = new ArrayList<>();
		int parametersNum=0;
		for(int order=0;order<=maxOrder;order++){
			
			int startTravelTimeO;
			int endTravelTimeO;
			if(order==1){
				startTravelTimeO=1;
			}else{
				startTravelTimeO=0;
			}
			endTravelTimeO=startTravelTimeO;
			
			for(int lastTravelTimeO=startTravelTimeO;lastTravelTimeO>=endTravelTimeO;lastTravelTimeO--){
				
				int startLengthO=order-lastTravelTimeO;
				int endLengthO=0;
				if(standardDeviation.getLength()==0) 
					startLengthO=0;
				if(standardDeviation.getReciprocalOfWidth()==0 && standardDeviation.getLinkClass()==0 && standardDeviation.getStartTime()==0 && standardDeviation.getWeightFromInLink()==0
						&& standardDeviation.getWeightFromOutLink()==0 && standardDeviation.getHolidayDays()==0 
						&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
					endLengthO=startLengthO;
				
				for(int lengthO=startLengthO;lengthO>=endLengthO;lengthO--){
					
					int startWidthO=order-lastTravelTimeO-lengthO;
					int endWidthO=0;
					if(standardDeviation.getReciprocalOfWidth()==0)
						startWidthO=0;
					if(standardDeviation.getLinkClass()==0 && standardDeviation.getStartTime()==0 && standardDeviation.getWeightFromInLink()==0
							&& standardDeviation.getWeightFromOutLink()==0 && standardDeviation.getHolidayDays()==0 
							&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
						endWidthO=startWidthO;
					
					for(int reciprocalOfWidthO=startWidthO;reciprocalOfWidthO>=endWidthO;reciprocalOfWidthO--){
						
						int startLinkClassO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO;
						int endLinkClassO=0;
						if(standardDeviation.getLinkClass()==0)
							startLinkClassO=0;
						if(standardDeviation.getStartTime()==0 && standardDeviation.getWeightFromInLink()==0 && standardDeviation.getWeightFromOutLink()==0 
								&& standardDeviation.getHolidayDays()==0 && standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
							endLinkClassO=startLinkClassO;
						
						for(int linkClassO=startLinkClassO;linkClassO>=endLinkClassO;linkClassO--){
							
							int startStartTimeO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO;
							int endStartTimeO=0;
							if(standardDeviation.getStartTime()==0) 
								startStartTimeO=0;
							if(standardDeviation.getWeightFromInLink()==0 && standardDeviation.getWeightFromOutLink()==0 && standardDeviation.getHolidayDays()==0 
									&& standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
								endStartTimeO=startStartTimeO;
							
							for(int startTimeO=startStartTimeO;startTimeO>=endStartTimeO;startTimeO--){
								
								int startWeightFromInLinkO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO-startTimeO;
								int endWeightFromInLinkO=0;
								if(standardDeviation.getWeightFromInLink()==0)
									startWeightFromInLinkO=0;
								if(standardDeviation.getWeightFromOutLink()==0 && standardDeviation.getHolidayDays()==0 && standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
									endWeightFromInLinkO=startWeightFromInLinkO;
								
								
								for(int weightFromInLinkO=startWeightFromInLinkO;weightFromInLinkO>=endWeightFromInLinkO;weightFromInLinkO--){
									int startWeightFromOutLinkO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO-startTimeO-weightFromInLinkO;
									int endWeightFromOutLinkO=0;
									if(standardDeviation.getWeightFromOutLink()==0)
										startWeightFromInLinkO=0;
									if(standardDeviation.getHolidayDays()==0 && standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0)
										endWeightFromInLinkO=startWeightFromInLinkO;
									
									for(int weightFromOutLinkO=startWeightFromOutLinkO;weightFromOutLinkO>=endWeightFromOutLinkO;weightFromOutLinkO--){
										
										int startHolidayDaysO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO-startTimeO-weightFromInLinkO-weightFromOutLinkO;
										int endHolidayDaysO=0;
										if(standardDeviation.getHolidayDays()==0) 
											startHolidayDaysO=0;
										if(standardDeviation.getDayInWeek()==0 && standardDeviation.getDate()==0) 
											endHolidayDaysO=startHolidayDaysO;
										
										for(int holidayDaysO=startHolidayDaysO;holidayDaysO>=endHolidayDaysO;holidayDaysO--){
											
											int startDayInWeeksO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO-startTimeO-weightFromInLinkO-weightFromOutLinkO-holidayDaysO;
											int endDayInWeekO=0;
											if(standardDeviation.getDayInWeek()==0) 
												startDayInWeeksO=0;
											if(standardDeviation.getDate()==0)
												endDayInWeekO=startDayInWeeksO;
											
											for(int dayInWeekO=startDayInWeeksO;dayInWeekO>=endDayInWeekO;dayInWeekO--){
												
												int startDateO=order-lastTravelTimeO-lengthO-reciprocalOfWidthO-linkClassO-startTimeO-weightFromInLinkO-weightFromOutLinkO-holidayDaysO-dayInWeekO;
												int endDateO=startDateO;
												if(standardDeviation.getDate()==0) 
													startDateO=0;
												
												for(int dateO=startDateO;dateO>=endDateO;dateO--){
													
													OrdersOfVars ordersOfVars=new OrdersOfVars();
													ordersOfVars.setLastTravelTimeO(lastTravelTimeO);
													ordersOfVars.setLengthO(lengthO);
													ordersOfVars.setReciprocalOfWidthO(reciprocalOfWidthO);
													ordersOfVars.setLinkClassO(linkClassO);
													ordersOfVars.setWeightFromInLinkO(weightFromInLinkO);
													ordersOfVars.setWeightFromOutLinkO(weightFromOutLinkO);
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
		if(lastTravelTimeO!=0)
			res.append("lastTravelT^"+lastTravelTimeO+"*");
		if(lengthO!=0)
			res.append("len^"+lengthO+"*");
		if(reciprocalOfWidthO!=0)
			res.append("wih^-"+reciprocalOfWidthO+"*");
		if(linkClassO!=0)
			res.append("lCla^"+linkClassO+"*");
		if(startTimeO!=0)
			res.append("startT^"+startTimeO+"*");
		if(weightFromInLinkO!=0)
			res.append("wetFI^"+weightFromInLinkO+"*");
		if(weightFromOutLinkO!=0)
			res.append("wetFO^"+weightFromOutLinkO+"*");
		if(holidayDaysO!=0)
			res.append("hoDs^"+holidayDaysO+"*");
		if(dayInWeekO!=0)
			res.append("dIW^"+dayInWeekO+"*");
		if(dateO!=0)
			res.append("dIM^"+dateO+"*");
		if(lastTravelTimeO+lengthO+reciprocalOfWidthO+linkClassO+startTimeO+weightFromInLinkO+weightFromOutLinkO+holidayDaysO+dayInWeekO+dateO==0){
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
	public int getReciprocalOfWidthO() {
		return reciprocalOfWidthO;
	}
	public void setReciprocalOfWidthO(int reciprocalOfWidthO) {
		this.reciprocalOfWidthO = reciprocalOfWidthO;
	}
	public int getLinkClassO() {
		return linkClassO;
	}
	public void setLinkClassO(int classO) {
		this.linkClassO = classO;
	}
	public int getWeightFromOutLinkO() {
		return weightFromOutLinkO;
	}
	public void setWeightFromOutLinkO(int weightFromOutLinkO) {
		this.weightFromOutLinkO = weightFromOutLinkO;
	}
	public int getWeightFromInLinkO() {
		return weightFromInLinkO;
	}
	public void setWeightFromInLinkO(int weightFromInLinkO) {
		this.weightFromInLinkO = weightFromInLinkO;
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

	public int getLastTravelTimeO() {
		return lastTravelTimeO;
	}

	public void setLastTravelTimeO(int lastTravelTimeO) {
		this.lastTravelTimeO = lastTravelTimeO;
	}



}
