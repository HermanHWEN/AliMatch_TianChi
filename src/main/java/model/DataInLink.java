package model;

import java.util.Date;

public class DataInLink {
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
	
	public Date getStartMinus() {
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
	
	
}
