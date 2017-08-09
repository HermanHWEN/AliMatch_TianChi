package model;

public class FlatData {
	private double length;
	private double reciprocalOfWidth;
	private double linkClass;
	private double weightFromOutLink;
	private double weightFromInLink;
	private double date;
	private double startTime;
	private double holidayDays;
	private double dayInWeek;
	private int notZero;
	
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getReciprocalOfWidth() {
		return reciprocalOfWidth;
	}
	public void setReciprocalOfWidth(double reciprocalOfWidth) {
		this.reciprocalOfWidth = reciprocalOfWidth;
	}
	public double getLinkClass() {
		return linkClass;
	}
	public void setLinkClass(double classLevel) {
		this.linkClass = classLevel;
	}
	public double getWeightFromOutLink() {
		return weightFromOutLink;
	}
	public void setWeightFromOutLink(double weightFromOutLink) {
		this.weightFromOutLink = weightFromOutLink;
	}
	
	public double getWeightFromInLink() {
		return weightFromInLink;
	}
	public void setWeightFromInLink(double weightFromInLink) {
		this.weightFromInLink = weightFromInLink;
	}
	public double getDate() {
		return date;
	}
	public void setDate(double date) {
		this.date = date;
	}
	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public double getHolidayDays() {
		return holidayDays;
	}
	public void setHolidayDays(double holidayDays) {
		this.holidayDays = holidayDays;
	}
	public double getDayInWeek() {
		return dayInWeek;
	}
	public void setDayInWeek(double dayInWeek) {
		this.dayInWeek = dayInWeek;
	}
	public int getNotZero() {
		if(length!=0) notZero++;
		if(reciprocalOfWidth!=0) notZero++;
		if(linkClass!=0) notZero++;
		if(weightFromInLink!=0) notZero++;
		if(weightFromOutLink!=0) notZero++;
		if(date!=0) notZero++;
		if(startTime!=0) notZero++;
		if(holidayDays!=0) notZero++;
		if(dayInWeek!=0) notZero++;
		return notZero;
	}
}
