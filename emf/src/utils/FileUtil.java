package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    
    // read file line by line
	/**
	 * 
	 * @param fileName
	 * @return Lines of the file, stored in List
	 */
    public static List<String> readByLine(String fileName) {
        List<String> lines = new ArrayList<String>();
        String line = null;
        LineNumberReader reader = null;
        
        try {
            // create a reader instance
            reader = new LineNumberReader(new FileReader(fileName));
            // read line by line
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch(FileNotFoundException e) {
        	System.out.print("File not found!---------> "+fileName);
        	e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            // close lineNumberReader
            try {
                if(reader != null) {
                    reader.close();
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        return lines;
    }
    
}
