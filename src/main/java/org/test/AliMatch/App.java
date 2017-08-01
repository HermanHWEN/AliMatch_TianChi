package org.test.AliMatch;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import calculateFeatures.CalculateFeatures;
import crossValidation.CrossValidation;
import importData.ReadAsLink;
import importData.ReadAsTrainData;
import model.DataInLink;
import model.Link;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ParseException
    {
    	Map<String,Link> linksMap=ReadAsLink.readAsLink();
    	linksMap=CalculateFeatures.calculateFeaturesOfLinks(linksMap);
    	List<DataInLink> dataInLinks=ReadAsTrainData.readAsTrainData(linksMap);
    	CrossValidation.errors(dataInLinks);
    }
}
