package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				+ ")#" + travle_time;
	}

	public synchronized double powerWithOrders(OrdersOfVars order){
		if(Constant.USE_MIN_MAX_NORMALIZATION) return withMinMaxNormalization(order);
		if(Constant.USE_ZERO_MEAN_NORMALIZATION) return withZeroMeanNormalization(order);
		return withoutNormalization(order);
	}

	public synchronized double withoutNormalization(OrdersOfVars order){
		double reswithOrder=1;
		reswithOrder*=Math.pow(this.link.getLength(), order.getLengthO());
		reswithOrder*=Math.pow(this.link.getWidth(), order.getWidthO());
		reswithOrder*=Math.pow(this.link.getLinkClass(), order.getLinkClassO());
		reswithOrder*=Math.pow(this.link.getWeight(), order.getWeightO());
		reswithOrder*=Math.pow(this.date.getDate(), order.getDateO());
		reswithOrder*=Math.pow(this.startTime.getHours()*30+this.startTime.getMinutes()/2, order.getStartTimeO());
		return reswithOrder;
	}

	public synchronized double withZeroMeanNormalization(OrdersOfVars order){
		double reswithOrder=1;
		if(standardDeviation.getLength()!=0)
			reswithOrder*=Math.pow((this.link.getLength()-average.getLength())/standardDeviation.getLength(), order.getLengthO());
		if(standardDeviation.getWidth()!=0)
			reswithOrder*=Math.pow((this.link.getWidth()-average.getWidth())/standardDeviation.getWidth(), order.getWidthO());
		if(standardDeviation.getLinkClass()!=0)
			reswithOrder*=Math.pow((this.link.getLinkClass()-average.getLinkClass())/standardDeviation.getLinkClass(), order.getLinkClassO());
		if(standardDeviation.getWeight()!=0)
			reswithOrder*=Math.pow((this.link.getWeight()-average.getWeight())/standardDeviation.getWeight(), order.getWeightO());
		if(standardDeviation.getDate()!=0)
			reswithOrder*=Math.pow((this.date.getDate()-average.getDate())/standardDeviation.getDate(), order.getDateO());
		if(standardDeviation.getStartTime()!=0)
			reswithOrder*=Math.pow((this.startTime.getHours()*30+this.startTime.getMinutes()/2-average.getStartTime())/standardDeviation.getStartTime(), order.getStartTimeO());
		return reswithOrder;
	}

	public synchronized double withMinMaxNormalization(OrdersOfVars order){
		double reswithOrder=1;
		if(standardDeviation.getLength()!=0)
			reswithOrder*=Math.pow((this.link.getLength()-min.getLength())/(max.getLength()-min.getLength()), order.getLengthO());
		if(standardDeviation.getWidth()!=0)
			reswithOrder*=Math.pow((this.link.getWidth()-min.getWidth())/(max.getWidth()-min.getWidth()), order.getWidthO());
		if(standardDeviation.getLinkClass()!=0)
			reswithOrder*=Math.pow((this.link.getLinkClass()-min.getLinkClass())/(max.getLinkClass()-min.getLinkClass()), order.getLinkClassO());
		if(standardDeviation.getWeight()!=0)
			reswithOrder*=Math.pow((this.link.getWeight()-min.getWeight())/(max.getWeight()-min.getWeight()), order.getWeightO());
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
