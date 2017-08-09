package calculateFeatures;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Link;

import org.apache.log4j.Logger;

import controll.Constant;

public class CalculateFeatures {
	private static Logger log = Logger.getLogger(CalculateFeatures.class);
	
	//using pagerank to as weight of each link
	//consider its outlinks as source pages of a page to update WeightFromOutLink
	//consider its inlinks as source pages of a page to update WeightFromInLink
	public static void calculateFeaturesOfLinks2(final Map<String,Link> links){

		List<Link> linksList= new ArrayList<Link>(links.values());
		initWeight(linksList);

		for(int count=0;count<Constant.ITERATIONS_OF_UPDATE_LINE_WEIGHT;count++){
			for (Link link : linksList) {
				double sumForInLink=0;
				double sumForOutLink=0;
				if(link.getOut_links()!=null){
					for(String outLinkId:link.getOut_links()){
						Link outLink=links.get(outLinkId);
						sumForOutLink+=outLink.getWeightFromOutLink()/outLink.getIn_links().size();
					}
				}
				if(link.getIn_links()!=null){
					for(String inLinkId:link.getIn_links()){
						Link inLink=links.get(inLinkId);
						sumForInLink+=inLink.getWeightFromInLink()/inLink.getOut_links().size();
					}
				}
				link.setWeightFromInLink(0.15+0.85*sumForInLink);
				link.setWeightFromOutLink(0.15+0.85*sumForOutLink);
			}
		}
	}


	public static void calculateFeaturesOfLinks(final Map<String,Link> links){
		List<Link> linksList= new ArrayList<Link>(links.values());
		initWeight(linksList);
		Map<String,Integer> numOfTriangleMap=getNumOfTriangleMap(linksList);

		BigDecimal oldWeight=null;
		BigDecimal newWeight=new BigDecimal(0);;
		int count = 0;
		do{
			count++;
			oldWeight=newWeight;
			newWeight=updateWeight(linksList,links,numOfTriangleMap);
		}while(oldWeight.doubleValue()==0 || (oldWeight.add(newWeight.multiply(BigDecimal.valueOf(-1)))).abs().divide(oldWeight,MathContext.DECIMAL128).doubleValue()>0.0001);
		//		log.info(oldWeight+"");
		//		log.info(newWeight+"");

	}

	private static void initWeight(final List<Link> linksList){
		for (Link link : linksList) {
			link.setWeightFromOutLink(1);
			link.setWeightFromInLink(1);
		}
	}
	private static BigDecimal updateWeight(final List<Link> linksList,final Map<String,Link> links,Map<String,Integer> numOfTriangleMap){

		double minWeight=0;
		for (Link link : linksList) {

			double getDegreeOfLeftNode=getDegreeOfLeftNode(link,links);
			double getDegreeOfRightNode=getDegreeOfRightNode(link,links);
			int getNumOfTriangle=numOfTriangleMap.get(link.getLink_ID());


			double u=(getDegreeOfLeftNode-getNumOfTriangle)*(getDegreeOfRightNode-getNumOfTriangle);
			double lamda=getNumOfTriangle/2+1;

			link.setWeightFromOutLink(u/lamda);
			if(link.getWeightFromOutLink()<minWeight){
				minWeight=link.getWeightFromOutLink();
			}

		}

		BigDecimal totalWeight=new BigDecimal(0);
		for (Link link : linksList) {
			//			link.setWeight(link.getWeight()-minWeight);
			//			log.info(link.getWeight());
			totalWeight=totalWeight.add(BigDecimal.valueOf(link.getWeightFromOutLink()));
		}

		return totalWeight;
	}

	private static Map<String,Integer> getNumOfTriangleMap(final List<Link> linksList){
		Map<String,Integer> res=new HashMap<String,Integer>();
		for (Link link : linksList) {
			res.put(link.getLink_ID(), getNumOfTriangle(link,linksList));
		}
		return res;
	}

	private static double getDegreeOfLeftNode(final Link targetLink,final Map<String,Link> links){
		double degree=0;
		if(targetLink.getIn_links()==null) return 0;
		for(String linkId:targetLink.getIn_links()){
			Link inLink=links.get(linkId);
			//			degree+=inLink.getWeight();
			degree++;
		}
		return degree;
	}

	private static double getDegreeOfRightNode(final Link targetLink,final Map<String,Link> links){
		double degree=0;
		if(targetLink.getOut_links()==null) return 0;
		for(String linkId:targetLink.getOut_links()){
			Link inLink=links.get(linkId);
			//			degree+=inLink.getWeight();
			degree++;
		}
		return degree;
	}

	private static int getNumOfTriangle(final Link targetLink,final List<Link> linksList){
		int count=0;

		List<Link> linksWithSameIn=linksWithSameIn(targetLink,linksList);
		List<Link> linksWithSameOut=linksWithSameOut(targetLink,linksList);
		for (Link linkWithSameOut : linksWithSameOut) {  

			for (Link linkWithSameIn : linksWithSameIn) {  
				if(linkWithSameOut.getIn_links()!=null && linkWithSameOut.getIn_links().equals(linkWithSameIn.getLink_ID())){
					count++;
				}
			}  
		}  
		return count;
	}

	private static List<Link> linksWithSameIn(final Link targetLink,final List<Link> linksList){
		List<Link> res=new ArrayList<Link>();
		for (Link link : linksList) { 
			if(isSameList(targetLink.getIn_links(),link.getIn_links())){
				res.add(link);
			}
		} 
		return res;
	}

	private static List<Link> linksWithSameOut(final Link targetLink,final List<Link> linksList){
		List<Link> res=new ArrayList<Link>();
		for (Link link : linksList) { 
			if(isSameList(targetLink.getOut_links(),link.getOut_links())){
				res.add(link);
			}
		} 
		return res;
	}

	private static boolean isSameList(final List<String> list1,final List<String> list2){
		if(list1==null) return false;
		if(list2==null) return false;
		if(list1.size()!=list2.size()) return false;

		for(String link:list1){
			boolean isFound=false;
			for(String link2:list2){
				if(link.equals(link2)){
					isFound=true;
					break;
				}
			}
			if(!isFound) return false;
		}
		return true;
	}

}
