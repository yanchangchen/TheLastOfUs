import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoGridFS
{
	public static void main(String[] args) throws IOException 
	{
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB("mydb1");
		DBCollection collection = db.getCollection("mydb1");
		
		// ===========================================================================================================
		//GridFS input file into Mongo
		File inputFileLocation = new File("testdoc.docx");
		GridFS gfsConnection = new GridFS(db, "file");
		GridFSInputFile gfsFile = gfsConnection.createFile(inputFileLocation);
		gfsFile.save();
		
		BasicDBObject mongoInputQuery = new BasicDBObject();
		mongoInputQuery.put("docid", "1");				// insert document
		mongoInputQuery.put("attachment", gfsFile);		// insert attachment
		collection.insert(mongoInputQuery);
		System.out.println("Inserted");
		
		// ===========================================================================================================
		// GridFS retrieve file from Mongo
		BasicDBObject mongoOutputQuery = new BasicDBObject();
		mongoOutputQuery.put("docid", "1");
		DBCursor cursor = collection.find(mongoOutputQuery);	//retrieve document n attachment object
		DBObject retrieveDocument = (DBObject) cursor.next().get("attachment");
		
		GridFS retrieveGFSFile = new GridFS(db, "file");
		GridFSDBFile fileOutput = retrieveGFSFile.findOne((ObjectId) retrieveDocument.get("_id"));			// retrieve attachment file
		fileOutput.writeTo("(String) retrieveDocument.get("filename"));	// output file to destination
		System.out.println("Completed");
		
		mongoClient.close();
	}
}
