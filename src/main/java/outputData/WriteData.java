package outputData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WriteData {
	public static void contentToTxt(String filePath, String content) {  
        try {  
            File f = new File(filePath); 
            f.getParentFile().mkdirs();
            if (!f.exists()) {  
                f.createNewFile();// create when doesn't exist 
            }  
  
            BufferedWriter output = new BufferedWriter(new FileWriter(f));  
            output.write(content);  
            output.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
  
        }  
    }  
}
