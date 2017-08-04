package crossValidation;

public class OrdersOfVars {
	
	private int lengthO;
	private int widthO;
	private int classO;
	private int weightO;
	private int dateO;
	private int startTimeO;
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
	public int getClassO() {
		return classO;
	}
	public void setClassO(int classO) {
		this.classO = classO;
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
	@Override
	public String toString() {
		return "[" + lengthO + "," + widthO + "," + classO + "," + weightO
				+ "," + dateO + "," + startTimeO + "]";
	}
	
	

}
