package TheLastOfUs.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtil
{
   public ArrayList<String> GetFileNamesFromDir(String dir)
   {
       try {
           File folder = new File(dir);
           File[] files = folder.listFiles();

           ArrayList<String> list = new ArrayList<String>();
           for (File file : files) {
               if (file.isFile())
                   list.add(file.getCanonicalPath());
           }

           return list;
       }
       catch (Exception e)
       {
           System.err.format("Exception occurred trying to read '%s'.", dir);
           e.printStackTrace();

           return null;
       }
   }

   public ArrayList<String> ParseFile(String filePath)
   {
       try
       {
           BufferedReader reader = new BufferedReader(new FileReader(filePath));
           ArrayList<String> list = new ArrayList<String>();
           String line;

           while ((line = reader.readLine()) != null)
           {
               //System.out.println("content: " + line);
               list.add(line);
           }

           reader.close();

           return list;
       }
       catch (Exception e)
       {
           System.err.format("Exception occurred trying to read '%s'.", filePath);
           e.printStackTrace();

           return null;
       }
   }
}
