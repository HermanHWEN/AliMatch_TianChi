package org.test.AliMatch;

import importData.ConstantPath;
import importData.ReadAsLink;
import importData.ReadAsTrainData;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import model.DataInLink;
import model.Link;

import org.apache.commons.lang3.StringUtils;

import outputData.WriteData;
import testData.Testing;
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
    	Function<DataInLink,Double> targetFunction=CrossValidation.getModel(dataInLinks.subList(0, 100));
    	System.out.println("Got target function.");
    	
    	
    	
    	List<DataInLink> testDataSet=Testing.getTestDataSet(linksMap);
    	
//    	List<DataInLink> testDataSet=Testing.getTestDataSetOfFirstLink(linksMap);
    	System.out.println("Generated testing data set # total : " + testDataSet.size());
    	
    	Testing.testing(targetFunction,testDataSet);
    	System.out.println("Test result generated.");
    	
    	
    	System.out.println("Writing data to specified path - "+ConstantPath.PATH_OF_RESULT);
    	WriteData.contentToTxt(ConstantPath.PATH_OF_RESULT, StringUtils.join(testDataSet, "\n"));
    	System.out.println("Done");
    }
}
