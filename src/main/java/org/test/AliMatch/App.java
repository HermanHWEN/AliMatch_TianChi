package org.test.AliMatch;

import importData.ReadAsLink;
import importData.ReadAsTrainData;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import model.DataInLink;
import model.Link;
import calculateFeatures.CalculateFeatures;
import crossValidation.CrossValidation;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ParseException, InterruptedException
    {
    	Map<String,Link> linksMap=ReadAsLink.readAsLink();
    	System.out.println("Read link info # total : " + linksMap.size());
    	CalculateFeatures.calculateFeaturesOfLinks(linksMap);
    	System.out.println("Calculated weight of link # total : " + linksMap.size());
    	List<DataInLink> dataInLinks=ReadAsTrainData.readAsTrainData(linksMap);
    	System.out.println("Read training data # total : " + dataInLinks.size());
    	linksMap.clear();
    	linksMap=null;
    	System.gc();
    	System.out.println("Training model... ");
    	CrossValidation.errors(dataInLinks);
    }
}
