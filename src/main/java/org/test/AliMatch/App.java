package org.test.AliMatch;

import importData.Constant;
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
import calculateFeatures.Convert2STD;
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
    	
    	Convert2STD.convert2STD(dataInLinks);
    	
    	System.out.println("Read training data # total : " + dataInLinks.size());
    	System.gc();
    	
    	System.out.println("Training model... ");
    	Function<DataInLink,Double> targetFunction=CrossValidation.getModel(dataInLinks);
    	System.out.println("Got target function.");
    	
    	
    	System.gc();
    	List<DataInLink> testDataSet=Testing.getTestDataSet(linksMap);
    	
//    	List<DataInLink> testDataSet=Testing.getTestDataSetOfFirstLink(linksMap);
    	System.out.println("Generated testing data set # total : " + testDataSet.size());
    	
    	Testing.testing(targetFunction,testDataSet);
    	System.out.println("Test result generated.");
    	
    	
    	System.out.println("Writing data to specified path - "+Constant.PATH_OF_RESULT);
    	WriteData.contentToTxt(Constant.PATH_OF_RESULT, StringUtils.join(testDataSet, "\n"));
    	System.out.println("Done");
    }
}
