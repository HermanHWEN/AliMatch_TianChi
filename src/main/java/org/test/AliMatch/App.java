package org.test.AliMatch;

import importData.ReadAsLink;
import importData.ReadAsTrainData;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

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
    	linksMap=CalculateFeatures.calculateFeaturesOfLinks(linksMap);
    	List<DataInLink> dataInLinks=ReadAsTrainData.readAsTrainData(linksMap);
    	linksMap.clear();
    	CrossValidation.errors(dataInLinks);
    }
}
