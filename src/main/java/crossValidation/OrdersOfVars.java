package crossValidation;

import java.util.ArrayList;
import java.util.List;

public class OrdersOfVars {
	
	private int lengthO;
	private int widthO;
	private int linkClassO;
	private int startTimeO;
	private int weightO; 
	private int holidayDaysO;
	private int dayInWeekO;
	private int dateO;
	
	public static List<OrdersOfVars> getOrders(int maxOrder,int maxParametersNum){
		List<OrdersOfVars> ordersOfVarsList = new ArrayList<>();
		int parametersNum=0;
		for(int order=0;order<=maxOrder;order++){

			for(int lengthO=order;lengthO>=0;lengthO--){
				for(int widthO=order-lengthO;widthO>=0;widthO--){
					for(int classO=order-lengthO-widthO;classO>=0;classO--){
						for(int startTimeO=order-lengthO-widthO-classO;startTimeO>=0;startTimeO--){
							for(int weightO=order-lengthO-widthO-classO-startTimeO;weightO>=0;weightO--){
								for(int holidayDaysO=order-lengthO-widthO-classO-startTimeO-weightO;holidayDaysO>=0;holidayDaysO--){
									for(int dayInWeekO=order-lengthO-widthO-classO-startTimeO-weightO-holidayDaysO;dayInWeekO>=0;dayInWeekO--){
										int dateO=order-lengthO-widthO-classO-startTimeO-weightO-holidayDaysO-dayInWeekO;
										OrdersOfVars ordersOfVars=new OrdersOfVars();
										ordersOfVars.setLengthO(lengthO);
										ordersOfVars.setWidthO(widthO);
										ordersOfVars.setLinkClassO(classO);
										ordersOfVars.setWeightO(weightO);
										ordersOfVars.setDateO(dateO);
										ordersOfVars.setStartTimeO(startTimeO);
										ordersOfVars.setHolidayDaysO(holidayDaysO);
										ordersOfVars.setDayInWeekO(dayInWeekO);
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
	
	public static List<String> getOrdersStr(int maxOrder,int maxParametersNum){
		List<String> strRes=new ArrayList<>();
		List<OrdersOfVars> res=OrdersOfVars.getOrders(maxOrder,maxParametersNum);
		for(OrdersOfVars r:res){
			strRes.add(r.toString());
		}
		return strRes;
	}

	

	public static int getParametersNum(int maxOrder){
		return OrdersOfVars.getOrders(maxOrder,-1).size()-1;
	}
	@Override
	public String toString() {
		return "[" + lengthO + "," + widthO + "," + linkClassO+ "," 
				+ startTimeO+ "," + weightO+ "," + holidayDaysO
				+ "," + dayInWeekO+ "," + dateO  + "]";
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
	
}
