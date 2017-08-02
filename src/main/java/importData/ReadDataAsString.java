package importData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ReadDataAsString {
	public static List<String> readTxtFile(String filePath){
		List<String> res=new ArrayList<String>();
        try {
                String encoding="UTF-8";
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
	
	public static List<String> readTxtFile2(String filePath) throws IOException{
		FileInputStream fin = new FileInputStream(filePath);
		  FileChannel fcin = fin.getChannel();
		  ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 50);
		  while(true)
		  {
		   buffer.clear();
		   int flag = fcin.read(buffer);
		   if(flag == -1)
		   {
		    break;
		   }
		   buffer.flip();
		   FileOutputStream fout = new FileOutputStream(ConstantPath.PATH_OF_TMP+"\\" + Math.random() + ".txt");
		   FileChannel fcout = fout.getChannel();
		   fcout.write(buffer);
		   System.out.println(buffer);
		  }
		return null;
	}
     
 
}

