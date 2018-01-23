package TheLastOfUs.app;

import TheLastOfUs.mongodb.MongoCRUD;
import TheLastOfUs.utilities.FileUtil;
import com.mongodb.ServerAddress;
import org.bson.Document;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class  AppMain implements TLOUConstants
{
    public static void main(String[] args)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date startTime = new Date();
        System.out.println("Started connecting at: " + dateFormat.format(startTime));

        ArrayList<ServerAddress> seeds = new ArrayList<ServerAddress>();
        //seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT));
        //seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT2));
        seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT3));

        MongoCRUD mCrud = new MongoCRUD();
        mCrud.ConnectToDB(seeds, TLOUConstants.MONGO_DB, TLOUConstants.MONGO_COLLECTION, TLOUConstants.MONGO_BUCKET);

        Date endTime = new Date();
        System.out.println("Finished connecting at: " + dateFormat.format(endTime));

        /******Insert documents from dir************
        FileUtil fileUtil = new FileUtil();
        ArrayList<String> files;
        files = fileUtil.GetFileNamesFromDir(AppConstants.JSON_DIR);

         for(String file: files)
         {
            ArrayList<String> lines;
            lines = fileUtil.ParseFile(file);
            System.out.println("file name:" + file);
            for (String line : lines)
                mCrud.InsertDocument(line);
         }
         *****************************************/

        System.out.println("Started operation at: " + dateFormat.format(startTime.getTime()));

        /******Insert document***************
        List <Document> reports = new ArrayList < Document > ();
        Document doc1 = new Document();
        doc1.append("id","1");
        doc1.append("name","Sabrina");
        Document doc2 = new Document();
        doc2.append("id","2");
        doc2.append("name","Singh");
        mCrud.InsertDocument(doc1);
        ************************************/

        /*******Retrieve document***********
        List<String> documents = new ArrayList<String>();
        documents = mCrud.RetrieveDocument("id","2");
        for (String s : documents)
        {
            System.out.println("Record = " + s);
        }
        ************************************/

        /*******Update document***********/
        //mCrud.UpdateDocuments("id","1","name","Sheva");


        /*******Delete document***********/
        //mCrud.DeleteDocuments("docid","0000000000200366");

        /*******Large document***********/
        //mCrud.InsertLargeDocument("C:\\mongodb-data\\large\\large_file.json","00001","large_file.json");
        //mCrud.RetrieveLargeDocument();

        System.out.println("Ended operation at: " + dateFormat.format(endTime.getTime()));

        mCrud.Close();
    }
}