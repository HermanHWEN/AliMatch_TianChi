package model;

import java.util.List;

public class Link {
	private String link_ID;
	public String getLink_ID() {
		return link_ID;
	}
	public void setLink_ID(String link_ID) {
		this.link_ID = link_ID;
	}
	private double length;
	private double reciprocalOfWidth;
	private double width;
	private int linkClass;
	
	private double weightFromOutLink;
	private double weightFromInLink;
	
	private List<String> in_links;
	private List<String> out_links;
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
		if(width!=0){
			reciprocalOfWidth=1/width;
		}
	}
	public int getLinkClass() {
		return linkClass;
	}
	public void setLinkClass(int link_class) {
		this.linkClass = link_class;
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
	
	public List<String> getIn_links() {
		return in_links;
	}
	public void setIn_links(List<String> in_links) {
		this.in_links = in_links;
	}
	public List<String> getOut_links() {
		return out_links;
	}
	public void setOut_links(List<String> out_links) {
		this.out_links = out_links;
	}
	public double getReciprocalOfWidth() {
		return reciprocalOfWidth;
	}
	public void setReciprocalOfWidth(double reciprocalOfWidth) {
		this.reciprocalOfWidth = reciprocalOfWidth;
	}
	
	
}
