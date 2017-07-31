package org.test.AliMatch;

import java.text.ParseException;

import importData.ReadAsLink;
import importData.ReadAsTrainData;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
			ReadAsTrainData.readAsTrainData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
