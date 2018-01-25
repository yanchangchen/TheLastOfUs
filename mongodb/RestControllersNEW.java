package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

@RestController
public class RestControllers {

    MongoClient mongoClient;
    DBCollection collection;
    DB db;
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="searchReport", method = RequestMethod.POST)
    public String searchReport(@RequestParam(value="searchTerm") String searchTerm) 
    {
    	initaliseConnection();	
    	BasicDBObject mongoQuery = new BasicDBObject();
    	BasicDBObject mongoFilter = new BasicDBObject();
		BasicDBObject innerQuery = new BasicDBObject();
		String returnJson = "";
		
		try
		{
			mongoQuery.put("$text", innerQuery);
			innerQuery.put("$search",searchTerm);		
			mongoFilter.put("docid", 1);
			mongoFilter.put("title", 1);
			mongoFilter.put("_id", 0);
	
			DBCursor cursor = collection.find(mongoQuery,mongoFilter);
			while(cursor.hasNext())
			{
				String json = cursor.next().toString();
				returnJson = json + "," + returnJson;
			}
			
			returnJson = returnJson.substring(0, returnJson.length()-1);
			returnJson = "[" + returnJson + "]";
		}
		
		catch(Exception e)
		{
			returnJson = "no record found";
			// returnJson = "[]";
		}
    	
		closeConnection();
    	return returnJson;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="openReport", method = RequestMethod.POST)
    public String openReport(@RequestParam(value="docid") String docid) 
    {
    	initaliseConnection();
    	BasicDBObject mongoQuery = new BasicDBObject();
    	String returnJson = "";
    	
    	try
		{
			mongoQuery.put("docid", docid);
			DBCursor cursor = collection.find(mongoQuery);
	    	returnJson = cursor.next().toString();
		}
		
		catch(Exception e)
		{
			returnJson = "no record found";
			//returnJson = "[]";
		}
		
    	closeConnection();
    	return returnJson;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="createReport", method = RequestMethod.POST)
    public String createReport(@RequestParam(value="docid") String docid,@RequestParam(value="title") String title, @RequestParam(value="content") String content,@RequestParam(value="date") String date,@RequestParam(value="source") String source) 
    {
    	initaliseConnection();
    	BasicDBObject mongoQuery = new BasicDBObject();
    	String returnJson = "";
    	
		try 
		{
			mongoQuery.put("docid", docid);
			mongoQuery.put("title", title);
			mongoQuery.put("content", content);
			mongoQuery.put("date", date);
			mongoQuery.put("source", source);
			collection.insert(mongoQuery);
	    	returnJson = "created";
		}
		
		catch(Exception e)
		{
			returnJson = "failed";
			//returnJson = "[]";
		}
    	
		closeConnection();
    	return returnJson;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="createReportwithAtt", method = RequestMethod.POST)
    public String createReportwithAtt(@RequestParam(value="docid") String docid,@RequestParam(value="title") String title, @RequestParam(value="content") String content,@RequestParam(value="date") String date,@RequestParam(value="source") String source,@RequestParam(value="attachment") MultipartFile attachment) 
    {
    	initaliseConnection();
    	BasicDBObject mongoQuery = new BasicDBObject();
    	String returnJson = "";
    	
		try 
		{
			mongoQuery.put("docid", docid);
			mongoQuery.put("title", title);
			mongoQuery.put("content", content);
			mongoQuery.put("date", date);
			mongoQuery.put("source", source);
			
			GridFS gfsConnection = new GridFS(db, "file");
			GridFSInputFile gfsFile = gfsConnection.createFile(attachment.getBytes());
			gfsFile.setFilename(attachment.getOriginalFilename());
			gfsFile.save();			
			mongoQuery.put("attachment",gfsFile);

			collection.insert(mongoQuery);
	    	returnJson = "Attachment created";
		}
		
		catch(Exception e)
		{
			returnJson = "failed";
			//returnJson = "[]";
		}
    	
		closeConnection();
    	return returnJson;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="openReportwithAtt", method = RequestMethod.POST)
    public HttpResponse openReportwithAtt(@RequestParam(value="docid") String docid) throws ClientProtocolException, IOException 
    {
    	HttpClient httpclient = HttpClients.createDefault();
    	HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");	// get connection url
    	HttpEntity entity = null;
    	   
    	initaliseConnection();
    	BasicDBObject mongoQuery = new BasicDBObject();
    	
    	try
		{
			mongoQuery.put("docid", docid);
			DBCursor cursor = collection.find(mongoQuery);
	    	DBObject retrieveDocument = (DBObject) cursor.next().get("attachment");
	    	GridFS retrieveGFSFile = new GridFS(db, "file");
	    	GridFSDBFile fileOutput = retrieveGFSFile.findOne((ObjectId) retrieveDocument.get("_id"));
	 	
	    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
	    	builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    	
	    	File file1 = new File("C:\\Users\\ITA-Mark\\Desktop\\" + (String) retrieveDocument.get("filename"));
			fileOutput.writeTo(file1);
			FileBody fileBody = new FileBody(file1,(String) retrieveDocument.get("filename")); //image should be a String
			builder.addTextBody("docid", docid);
			builder.addPart("my_file", fileBody); 
			entity = builder.build();
		}
		
		catch(Exception e)
		{
//			//returnJson = "[]";
		}
    	
    	httppost.setEntity(entity);
    	
    	//Execute and get the response.
    	HttpResponse response = httpclient.execute(httppost);
    	
    	closeConnection();
    	return response;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value ="updateReport", method = RequestMethod.POST)
    public String updateReport(@RequestParam(value="docid") String docid,@RequestParam(value="title") String title, @RequestParam(value="content") String content,@RequestParam(value="date") String date,@RequestParam(value="source") String source) 
    {
    	initaliseConnection();
    	BasicDBObject mongoQuery = new BasicDBObject();
    	String returnJson = "";
		
		try 
		{
			BasicDBObject mongoUpdate = new BasicDBObject();
			mongoQuery.put("docid", docid);
			mongoUpdate.put("docid", docid);
			mongoUpdate.put("title", title);
			mongoUpdate.put("content", content);
			mongoUpdate.put("date", date);
			mongoUpdate.put("source", source);
			
			collection.update(mongoQuery, mongoUpdate);
	    	returnJson = "updated";
		}
		
		catch(Exception e)
		{
			returnJson = "failed";
			//returnJson = "[]";
		}
    	
		closeConnection();
    	return returnJson;
    }   
    
    public void initaliseConnection()
    {
    	List<MongoCredential> credential = new ArrayList<>();
		credential.add(MongoCredential.createScramSha1Credential("superAdmin", "admin", "mongoDB".toCharArray()));
		mongoClient = new MongoClient(new ServerAddress("localhost", 27017), credential);
		db = mongoClient.getDB("mydb2");
		collection = db.getCollection("mydb2");
    }
    
    public void closeConnection()
    {
    	mongoClient.close();
    }
}
