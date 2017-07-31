package importData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadDataAsString {
	public static List<String> readTxtFile(String filePath){
		List<String> res=new ArrayList<String>();
        try {
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //file exists?
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	res.add(lineTxt);
                    }
                    read.close();
        }else{
            System.out.println("can't find file.");
        }
        } catch (Exception e) {
            System.out.println("reading error.");
            e.printStackTrace();
        }
		return res;
     
    }    
     
 
}

