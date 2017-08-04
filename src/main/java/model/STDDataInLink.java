package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import crossValidation.OrdersOfVars;

public class STDDataInLink {
	
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
	@Override
	public String toString() {
		return link.getLink_ID()
				+ "#" + STDDataInLink.df.format(date)
				+ "#[" + STDDataInLink.df2.format(startTime) + "," + STDDataInLink.df2.format(endTime)
				+ ")#" + travle_time;
	}
	
	public synchronized double powerWithOrders(OrdersOfVars order){
		double reswithOrder=Math.pow(this.link.getLength(), order.getLengthO())*
				Math.pow(this.link.getWidth(), order.getWidthO())*
				Math.pow(this.link.getLink_class(), order.getClassO())*
				Math.pow(this.link.getWeight(), order.getWeightO())*
				Math.pow(this.date.getDate(), order.getDateO())*
				Math.pow(this.startTime.getHours()*30+this.startTime.getMinutes()/2, order.getStartTimeO());
		return reswithOrder;
	}
	
	
	
	
}
