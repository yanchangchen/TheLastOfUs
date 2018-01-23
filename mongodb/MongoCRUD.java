package TheLastOfUs.mongodb;

import com.mongodb.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.gridfs.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.*;


public class MongoCRUD
{
    public void ConnectToDB(ArrayList<ServerAddress> seeds, String db, String collection, String bucket)
    {
        m_client = new MongoClient("35.227.62.44",27019);
        //m_client = new MongoClient(seeds);
        m_database = m_client.getDatabase(db);
        m_DB = m_client.getDB(db);
        m_gridFSConnection = new GridFS(m_DB, bucket);

        MongoIterable<String> collectionStrings = m_database.listCollectionNames();
        for(String collectionName : collectionStrings)
        {
            if (collectionName.equalsIgnoreCase(collection))
            {
                m_collection = m_database.getCollection(collection);
                m_DBCollection = m_DB.getCollection(collection);
                return;
            }
        }
        m_database.createCollection(collection);
        m_collection = m_database.getCollection(collection);
        m_DBCollection = m_DB.getCollection(collection);
    }

    public void InsertDocument(String rpt)
    {
       Document doc = Document.parse(rpt);
       m_collection.insertOne(doc);
    }

    public void InsertDocument(Document doc)
    {
        m_collection.insertOne(doc);
    }

    public void InsertLargeDocument(String filePath, HashMap<String,String> metaData)
    {
        try
        {
            File inputFileLocation = new File(filePath);
            GridFSInputFile gfsFile = m_gridFSConnection.createFile(inputFileLocation);
            gfsFile.save();

            BasicDBObject mongoInputQuery = new BasicDBObject();
            for(Map.Entry entry:metaData.entrySet())
                mongoInputQuery.put(entry.getKey().toString(),entry.getValue()); // insert document

            mongoInputQuery.put("attachment", gfsFile);		// insert attachment
            m_DBCollection.insert(mongoInputQuery);
        }
        catch(IOException e)
        {
            System.out.println("IOException when trying to create large file");
        }
    }

    public List<String> RetrieveDocument(String attribute, String value)
    {
        List<String> jsonDocs = new ArrayList<String>();
        FindIterable<Document> results = m_collection.find(Filters.eq(attribute,value));
        MongoCursor<Document> cursor = results.iterator();
        while(cursor.hasNext())
        {
            Document doc = cursor.next();
            jsonDocs.add(doc.toJson());
        }
        return jsonDocs;
    }

    public void RetrieveLargeDocument(String attribute, String value, String outputPath)
    {
        BasicDBObject mongoOutputQuery = new BasicDBObject();
        mongoOutputQuery.put(attribute, value);
        DBCursor cursor = m_DBCollection.find(mongoOutputQuery);	//retrieve document n attachment object
        DBObject retrieveDocument = (DBObject) cursor.next().get("attachment");

        GridFS retrieveGFSFile = new GridFS(m_DB, "BigFileBucket");
        GridFSDBFile fileOutput = retrieveGFSFile.findOne((ObjectId) retrieveDocument.get("_id")); // retrieve attachment file
        try
        {
            fileOutput.writeTo(outputPath + "\\" + fileOutput.getFilename());    // output file to destination
        }catch(IOException e)
        {
            System.out.println("IOException when writing to file path for large file");
        }
    }

    public long UpdateOneDocument(String attribute, String value, Document doc)
    {
        UpdateResult result = m_collection.updateOne(Filters.eq(attribute,value), doc);
        return result.getModifiedCount();
    }

    public long UpdateOneDocument(String attribute, String value, String newAttribute, String newValue)
    {
        UpdateResult result = m_collection.updateOne(Filters.eq(attribute,value), Updates.set(newAttribute,newValue));
        return result.getModifiedCount();
    }

    public long UpdateDocuments(String attribute, String value, Document doc)
    {
        UpdateResult result = m_collection.updateMany(Filters.eq(attribute,value), doc);
        return result.getModifiedCount();
    }

    public long UpdateDocuments(String attribute, String value, String newAttribute, String newValue)
    {
        UpdateResult result = m_collection.updateMany(Filters.eq(attribute,value), Updates.set(newAttribute,newValue));
        return result.getModifiedCount();
    }

    public long DeleteOneDocument(String attribute, String value)
    {
        DeleteResult result = m_collection.deleteOne(new Document(attribute,value));
        return result.getDeletedCount();
    }

    public long DeleteDocuments(String attribute, String value)
    {
        DeleteResult result = m_collection.deleteMany(new Document(attribute,value));
        return result.getDeletedCount();
    }

    public void DisplayAllDocuments()
    {
        FindIterable<Document> iterdoc = m_collection.find();
        Iterator iter = iterdoc.iterator();

        while(iter.hasNext())
        {
            System.out.println(iter.next());
        }
    }

    public void DisplayAllCollections()
    {
        for(String collectionName : m_database.listCollectionNames())
            System.out.println("Collection name: " + collectionName);
    }

    public void Close()
    {
        m_client.close();
    }

    Block<Document> printBlock = new Block<Document>()
    {
        public void apply(final Document document)
        {
            System.out.println(document.toJson());
        }
    };

    private MongoClient m_client;
    private MongoDatabase m_database;
    private MongoCollection<Document> m_collection;
    private GridFS m_gridFSConnection;
    private DBCollection m_DBCollection;
    private DB m_DB;
};
