package outputData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class WriteData {
	public static void contentToTxt(String filePath, String content) {  
        try {  
            File f = new File(filePath); 
            f.getParentFile().mkdirs();
            if (!f.exists()) {  
                f.createNewFile();// create when doesn't exist 
            }  
            BufferedReader input = new BufferedReader(new FileReader(f));  
  
            input.close();  
  
            BufferedWriter output = new BufferedWriter(new FileWriter(f));  
            output.write(content);  
            output.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
  
        }  
    }  
}
