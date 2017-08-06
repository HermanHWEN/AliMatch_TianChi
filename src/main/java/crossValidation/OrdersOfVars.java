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

			for(int lengthO=order;lengthO>=0;lengthO--){
				if(standardDeviation.getLength()==0) lengthO=0;
				
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					if(standardDeviation.getWidth()==0) widthO=0;
					
					for(int linkClassO=order-lengthO-widthO;linkClassO>=0;linkClassO--){
						if(standardDeviation.getLinkClass()==0) linkClassO=0;
						
						for(int startTimeO=order-lengthO-widthO-linkClassO;startTimeO>=0;startTimeO--){
							if(standardDeviation.getStartTime()==0) startTimeO=0;
							
							for(int weightO=order-lengthO-widthO-linkClassO-startTimeO;weightO>=0;weightO--){
								if(standardDeviation.getWeight()==0) weightO=0;
								
								for(int holidayDaysO=order-lengthO-widthO-linkClassO-startTimeO-weightO;holidayDaysO>=0;holidayDaysO--){
									if(standardDeviation.getHolidayDays()==0) holidayDaysO=0;
									
									for(int dayInWeekO=order-lengthO-widthO-linkClassO-startTimeO-weightO-holidayDaysO;dayInWeekO>=0;dayInWeekO--){
										if(standardDeviation.getDayInWeek()==0) dayInWeekO=0;
										
										int dateO=order-lengthO-widthO-linkClassO-startTimeO-weightO-holidayDaysO-dayInWeekO;
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
		return res.substring(0, res.length());
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
