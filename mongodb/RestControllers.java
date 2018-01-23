package com.example.demo.TheLastOfUs.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import com.example.demo.TheLastOfUs.app.*;

@RestController
public class RestControllers {

    MongoClient mongoClient;
    DBCollection collection;
    
    @RequestMapping(value ="searchReport", method = RequestMethod.POST)
    public String searchReport(@RequestParam(value="searchTerm") String searchTerm) 
    {
    	initaliseConnection();
		
		BasicDBObject mongoQuery = new BasicDBObject();
		BasicDBObject innerQuery = new BasicDBObject();
		BasicDBObject mongoFilter = new BasicDBObject();
		String returnJson = "";
		
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
    	
		closeConnection();
    	return returnJson;
    }
    
    @RequestMapping(value ="openReport", method = RequestMethod.POST)
    public String openReport(@RequestParam(value="docid") String docid) 
    {
    	initaliseConnection();
		
		BasicDBObject mongoQuery = new BasicDBObject();
		BasicDBObject mongoFilter = new BasicDBObject();
		mongoQuery.put("docid", docid);
		DBCursor cursor = collection.find(mongoQuery);
    	String returnJson = cursor.next().toString();
    	
    	closeConnection();
    	return returnJson;
    }
    
    @RequestMapping(value ="createReport", method = RequestMethod.POST)
    public String createReport(@RequestParam(value="docid") String docid,@RequestParam(value="title") String title, @RequestParam(value="content") String content,@RequestParam(value="date") String date,@RequestParam(value="source") String source) 
    {
    	initaliseConnection();
    	String returnJson = "";
    	
		try 
		{
			BasicDBObject mongoQuery = new BasicDBObject();
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
		}
    	
		closeConnection();
    	return returnJson;
    }
    
    @RequestMapping(value ="updateReport", method = RequestMethod.POST)
    public String updateReport(@RequestParam(value="docid") String docid,@RequestParam(value="title") String title, @RequestParam(value="content") String content,@RequestParam(value="date") String date,@RequestParam(value="source") String source) 
    {
    	initaliseConnection();
    	String returnJson = "";
		
		try 
		{
			BasicDBObject mongoQuery = new BasicDBObject();
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
		}
    	
		closeConnection();
    	return returnJson;
    }   
    
    public void initaliseConnection()
    {
//    	List<MongoCredential> credential = new ArrayList<>();
//		credential.add(MongoCredential.createScramSha1Credential("superAdmin", "admin", "mongoDB".toCharArray()));
//		mongoClient = new MongoClient(new ServerAddress("localhost", 27017), credential);
    	
    	 ArrayList<ServerAddress> seeds = new ArrayList<ServerAddress>();
         seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT));
         seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT2));
         seeds.add(new ServerAddress(TLOUConstants.MONGO_HOST, TLOUConstants.MONGO_PORT3));
         mongoClient = new MongoClient(seeds);
		
         DB db = mongoClient.getDB(TLOUConstants.MONGO_DB);
		
         collection = db.getCollection(TLOUConstants.MONGO_COLLECTION);
    }
    
    public void closeConnection()
    {
    	mongoClient.close();
    }
}
