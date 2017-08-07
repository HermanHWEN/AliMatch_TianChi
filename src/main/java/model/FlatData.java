package model;

public class FlatData {
	private double length;
	private double reciprocalOfWidth;
	private double linkClass;
	private double weight;
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
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
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
		if(weight!=0) notZero++;
		if(date!=0) notZero++;
		if(startTime!=0) notZero++;
		if(holidayDays!=0) notZero++;
		if(dayInWeek!=0) notZero++;
		return notZero;
	}
}
