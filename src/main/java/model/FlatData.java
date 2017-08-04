package model;

public class FlatData {
	private double length;
	private double width;
	private double classLevel;
	private double weight;
	private double date;
	private double startTime;
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getClassLevel() {
		return classLevel;
	}
	public void setClassLevel(double classLevel) {
		this.classLevel = classLevel;
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
	public FlatData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public FlatData(String flag) {
		if("min".equals(flag)){
			this.length = Double.MIN_VALUE;
			this.width = Double.MIN_VALUE;
			this.classLevel = Double.MIN_VALUE;
			this.weight = Double.MIN_VALUE;
			this.date = Double.MIN_VALUE;
			this.startTime = Double.MIN_VALUE;
		}else{
			this.length = Double.MAX_VALUE;
			this.width = Double.MAX_VALUE;
			this.classLevel = Double.MAX_VALUE;
			this.weight = Double.MAX_VALUE;
			this.date = Double.MAX_VALUE;
			this.startTime = Double.MAX_VALUE;
		}
	}
	
}
