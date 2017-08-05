package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import controll.Constant;
import crossValidation.OrdersOfVars;

public class DataInLink implements Cloneable{
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Link link;
	
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	private Date date;
	private Date startTime;
	private Date endTime;
	private double travle_time;
	
	
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
		double reswithOrder=Math.pow(this.link.getLength(), order.getLengthO())*
				Math.pow(this.link.getWidth(), order.getWidthO())*
//				Math.pow(this.link.getLink_class(), order.getClassO())*
				Math.pow(this.link.getWeight(), order.getWeightO())*
				Math.pow(this.date.getDate(), order.getDateO())*
				Math.pow(this.startTime.getHours()*30+this.startTime.getMinutes()/2, order.getStartTimeO());
		return reswithOrder;
	}
	
	public synchronized double withZeroMeanNormalization(OrdersOfVars order){
		double reswithOrder=Math.pow((this.link.getLength()-average.getLength())/standardDeviation.getLength(), order.getLengthO())*
				Math.pow((this.link.getWidth()-average.getWidth())/standardDeviation.getWidth()/standardDeviation.getWidth(), order.getWidthO())*
//				Math.pow((this.link.getLink_class()-average.getClassLevel())/standardDeviation.getClassLevel(), order.getClassO())*
				Math.pow((this.link.getWeight()-average.getWeight())/standardDeviation.getWeight(), order.getWeightO())*
				Math.pow((this.date.getDate()-average.getDate())/standardDeviation.getDate(), order.getDateO())*
				Math.pow((this.startTime.getHours()*30+this.startTime.getMinutes()/2-average.getStartTime())/standardDeviation.getStartTime(), order.getStartTimeO());
		return reswithOrder;
	}
	
	public synchronized double withMinMaxNormalization(OrdersOfVars order){
		double reswithOrder=Math.pow((this.link.getLength()-min.getLength())/(max.getLength()-min.getStartTime()), order.getLengthO())*
				Math.pow((this.link.getWidth()-min.getWidth())/(max.getWidth()-min.getStartTime()), order.getWidthO())*
//				Math.pow((this.link.getLink_class()-min.getClassLevel())/(max.getClassLevel()-min.getStartTime()), order.getClassO())*
				Math.pow((this.link.getWeight()-min.getWeight())/(max.getWeight()-min.getStartTime()), order.getWeightO())*
				Math.pow((this.date.getDate()-min.getDate())/(max.getDate()-min.getStartTime()), order.getDateO())*
				Math.pow((this.startTime.getHours()*30+this.startTime.getMinutes()/2-min.getStartTime())/(max.getStartTime()-min.getStartTime()), order.getStartTimeO());
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
	
	
}
