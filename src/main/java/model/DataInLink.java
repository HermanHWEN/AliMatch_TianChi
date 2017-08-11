package model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import controll.Constant;
import crossValidation.OrdersOfVars;

public class DataInLink implements Cloneable{

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	private Link link;
	private Date date;
	private Date startTime;
	private Date endTime;
	private double travle_time;
	private long holidayDays;
	private int dayInWeek;

	private FlatData average;
	private FlatData standardDeviation;

	private FlatData min;
	private FlatData max;


	@Override
	public String toString() {
		return link.getLink_ID()
				+ "#" + DataInLink.df.format(date)
				+ "#[" + DataInLink.df2.format(startTime) + "," + DataInLink.df2.format(endTime)
				+ ")#" + BigDecimal.valueOf(travle_time).setScale(Constant.ACURACY_OF_TRAVEL_TIME_OUTPUT, BigDecimal.ROUND_HALF_UP);
	}

	public synchronized double powerWithOrders(OrdersOfVars order,Map<String,DataInLink> dataInLinksMap){
		if(Constant.USE_MIN_MAX_NORMALIZATION) return withMinMaxNormalization(order,dataInLinksMap);
		if(Constant.USE_ZERO_MEAN_NORMALIZATION) return withZeroMeanNormalization(order,dataInLinksMap);
		return withoutNormalization(order,dataInLinksMap);
	}

	public synchronized double withoutNormalization(OrdersOfVars order,Map<String,DataInLink> dataInLinksMap){
		double reswithOrder=1;
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(this.startTime);
		calendar.add(Calendar.MINUTE, -2);
		DataInLink lastDataInLink=dataInLinksMap.get(link.getLink_ID()
				+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DATE)
				+calendar.get(Calendar.HOUR)+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND));
		if(lastDataInLink!=null){
			double lastTravelTime=lastDataInLink.getTravle_time();
			reswithOrder*=Math.pow(lastTravelTime, order.getLastTravelTimeO());
		}
		reswithOrder*=Math.pow(this.link.getLength(), order.getLengthO());
		reswithOrder*=Math.pow(this.link.getReciprocalOfWidth(), order.getReciprocalOfWidthO());
		reswithOrder*=Math.pow(this.link.getLinkClass(), order.getLinkClassO());
		reswithOrder*=Math.pow(this.link.getWeightFromInLink(), order.getWeightFromInLinkO());
		reswithOrder*=Math.pow(this.link.getWeightFromOutLink(), order.getWeightFromOutLinkO());
		reswithOrder*=Math.pow(this.date.getDate(), order.getDateO());
		reswithOrder*=Math.pow(this.startTime.getHours()*30+this.startTime.getMinutes()/2, order.getStartTimeO());
		return reswithOrder;
	}

	public synchronized double withZeroMeanNormalization(OrdersOfVars order,Map<String,DataInLink> dataInLinksMap){
		double reswithOrder=1;
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(this.startTime);
		calendar.add(Calendar.MINUTE, -2);
		DataInLink lastDataInLink=dataInLinksMap.get(link.getLink_ID()
				+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DATE)
				+calendar.get(Calendar.HOUR)+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND));
		if(lastDataInLink!=null){
			double lastTravelTime=lastDataInLink.getTravle_time();
			reswithOrder*=Math.pow(lastTravelTime, order.getLastTravelTimeO());
		}
		if(standardDeviation.getLength()!=0)
			reswithOrder*=Math.pow((this.link.getLength()-average.getLength())/standardDeviation.getLength(), order.getLengthO());
		if(standardDeviation.getReciprocalOfWidth()!=0)
			reswithOrder*=Math.pow((this.link.getReciprocalOfWidth()-average.getReciprocalOfWidth())/standardDeviation.getReciprocalOfWidth(), order.getReciprocalOfWidthO());
		if(standardDeviation.getLinkClass()!=0)
			reswithOrder*=Math.pow((this.link.getLinkClass()-average.getLinkClass())/standardDeviation.getLinkClass(), order.getLinkClassO());
		if(standardDeviation.getWeightFromInLink()!=0)
			reswithOrder*=Math.pow((this.link.getWeightFromInLink()-average.getWeightFromInLink())/standardDeviation.getWeightFromInLink(), order.getWeightFromInLinkO());
		if(standardDeviation.getWeightFromOutLink()!=0)
			reswithOrder*=Math.pow((this.link.getWeightFromOutLink()-average.getWeightFromOutLink())/standardDeviation.getWeightFromOutLink(), order.getWeightFromOutLinkO());
		if(standardDeviation.getDate()!=0)
			reswithOrder*=Math.pow((this.date.getDate()-average.getDate())/standardDeviation.getDate(), order.getDateO());
		if(standardDeviation.getStartTime()!=0)
			reswithOrder*=Math.pow((this.startTime.getHours()*30+this.startTime.getMinutes()/2-average.getStartTime())/standardDeviation.getStartTime(), order.getStartTimeO());
		return reswithOrder;
	}

	public synchronized double withMinMaxNormalization(OrdersOfVars order,Map<String,DataInLink> dataInLinksMap){
		double reswithOrder=1;
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(this.startTime);
		calendar.add(Calendar.MINUTE, -2);
		DataInLink lastDataInLink=dataInLinksMap.get(link.getLink_ID()
				+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DATE)
				+calendar.get(Calendar.HOUR)+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND));
		if(lastDataInLink!=null){
			double lastTravelTime=lastDataInLink.getTravle_time();
			reswithOrder*=Math.pow(lastTravelTime, order.getLastTravelTimeO());
		}
		if(standardDeviation.getLength()!=0)
			reswithOrder*=Math.pow((this.link.getLength()-min.getLength())/(max.getLength()-min.getLength()), order.getLengthO());
		if(standardDeviation.getReciprocalOfWidth()!=0)
			reswithOrder*=Math.pow((this.link.getReciprocalOfWidth()-min.getReciprocalOfWidth())/(max.getReciprocalOfWidth()-min.getReciprocalOfWidth()), order.getReciprocalOfWidthO());
		if(standardDeviation.getLinkClass()!=0)
			reswithOrder*=Math.pow((this.link.getLinkClass()-min.getLinkClass())/(max.getLinkClass()-min.getLinkClass()), order.getLinkClassO());
		if(standardDeviation.getWeightFromInLink()!=0)
			reswithOrder*=Math.pow((this.link.getWeightFromInLink()-min.getWeightFromInLink())/(max.getWeightFromInLink()-min.getWeightFromInLink()), order.getWeightFromInLinkO());
		if(standardDeviation.getWeightFromOutLink()!=0)
			reswithOrder*=Math.pow((this.link.getWeightFromOutLink()-min.getWeightFromOutLink())/(max.getWeightFromOutLink()-min.getWeightFromOutLink()), order.getWeightFromOutLinkO());
		if(standardDeviation.getDate()!=0)
			reswithOrder*=Math.pow((this.date.getDate()-min.getDate())/(max.getDate()-min.getDate()), order.getDateO());
		if(standardDeviation.getStartTime()!=0)
			reswithOrder*=Math.pow((this.startTime.getHours()*30+this.startTime.getMinutes()/2-min.getStartTime())/(max.getStartTime()-min.getStartTime()), order.getStartTimeO());
		return reswithOrder;
	}




	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public double getTravle_time() {
		return travle_time;
	}
	public void setTravle_time(double travle_time) {
		this.travle_time = travle_time;
	}
	public FlatData getAverage() {
		return average;
	}
	public void setAverage(FlatData average) {
		this.average = average;
	}
	public FlatData getStandardDeviation() {
		return standardDeviation;
	}
	public void setStandardDeviation(FlatData standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	public FlatData getMin() {
		return min;
	}
	public void setMin(FlatData min) {
		this.min = min;
	}
	public FlatData getMax() {
		return max;
	}
	public void setMax(FlatData max) {
		this.max = max;
	}

	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}

	public long getHolidayDays() {
		return holidayDays;
	}

	public void setHolidayDays(long holidays) {
		this.holidayDays = holidays;
	}

	public int getDayInWeek() {
		return dayInWeek;
	}

	public void setDayInWeek(int dayInWeek) {
		this.dayInWeek = dayInWeek;
	}


}
