package app;

import mongodb.MongoCRUD;
import utilities.FileUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class  AppMain implements AppConstants
{
    public static void main(String[] args)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

        Date connStartTime = new Date();
        System.out.println("Started connecting at: " + dateFormat.format(connStartTime));

        MongoCRUD mCrud = new MongoCRUD();
        mCrud.ConnectToDB(AppConstants.MONGO_HOST, Integer.parseInt(AppConstants.MONGO_PORT), AppConstants.MONGO_DB, AppConstants.MONGO_COLLECTION);

        Date connEndTime = new Date();
        System.out.println("Finished connecting at: " + dateFormat.format(connEndTime));

        /******File Code************
        FileUtil fileUtil = new FileUtil();

        ArrayList<String> files;
        files = fileUtil.GetFileNamesFromDir(AppConstants.JSON_DIR);
        ***************************/

        Date opStartTime = new Date();
        System.out.println("Started operation at: " + dateFormat.format(opStartTime));

        /*******Retrieve document***********/
        mCrud.RetrieveDocument("docid","0000000000200366");

        /*******Update document***********/
        //mCrud.UpdateDocuments("docid","0000000000200364","createdBy","HUMAN");

        /*******Delete document***********/
        //mCrud.DeleteDocuments("docid","0000000000200366");

        /*******Large document***********/
        //mCrud.InsertLargeDocument("C:\\mongodb-data\\large\\large_file.json","00001","large_file.json");
        //mCrud.RetrieveLargeDocument();

        /*******Insert documents************
        for(String file: files)
        {
            ArrayList<String> lines;
            lines = fileUtil.ParseFile(file);
            System.out.println("file name:" + file);
            for (String line : lines)
                mCrud.InsertDocument(line);
        }
        ***********************************/

        Date opEndTime = new Date();
        System.out.println("Ended operation at: " + dateFormat.format(opEndTime));

        mCrud.Close();
    }
}