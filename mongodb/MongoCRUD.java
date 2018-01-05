package mongodb;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.gridfs.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.Iterator;


public class MongoCRUD
{
    public void ConnectToDB(String host, int port, String db)
    {
        m_client = new MongoClient(host, port);
        m_database = m_client.getDatabase(db);

        //m_client_FS = new MongoClient(host, port);
        //m_database_FS = m_client_FS.getDatabase("FS");
        m_gridFSBucket = GridFSBuckets.create(m_database,"BigFiles");

        System.out.println("Connected to Database: " + m_database);
    }

    public void CreateCollection(String name)
    {
        MongoIterable<String> collectionStrings = m_database.listCollectionNames();
        for(String collectionName : collectionStrings)
        {
            if (collectionName.equalsIgnoreCase(name))
            {
                m_collection = m_database.getCollection(name);
                System.out.println("Collection: " + name + " already exists");
                return;
            }
        }
        m_database.createCollection(name);
        m_collection = m_database.getCollection(name);
        System.out.println("Collection: " + name +  "newly created");
    }

    public void InsertDocument(String rpt,String file)
    {
       Document doc = Document.parse(rpt);
       m_collection.insertOne(doc);
    }

    public void InsertLargeDocument(String filePath,String docID, String fileName)
    {
        try
        {
            InputStream readStream = new FileInputStream(new File(filePath));
            GridFSUploadOptions fsOptions = new GridFSUploadOptions().metadata(new Document("docID",docID));
            ObjectId id = m_gridFSBucket.uploadFromStream(fileName,readStream,fsOptions);
            readStream.close();
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Large file to insert not found");
        }
        catch(IOException e)
        {
            System.err.println("File exception");
        }
    }

    public void RetrieveLargeDocument()
    {
        //m_gridFSBucket.find().forEach(printGrid);
    }

    public void RetrieveDocumentByID(String ID)
    {
        m_collection.find(Filters.eq("docid", ID)).limit(1).forEach(printBlock);
    }

    public void UpdateOneDocument(String attribute, String value, Document doc)
    {
        m_collection.updateOne(Filters.eq(attribute,value), doc);
        System.out.println("Updated document: " + attribute + " : " + value);
    }

    public void UpdateOneDocument(String attribute, String value, String newAttribute, String newValue)
    {
        m_collection.updateOne(Filters.eq(attribute,value), Updates.set(newAttribute,newValue));
        System.out.println("Updated document: " + attribute + " : " + value);
    }

    public void UpdateDocuments(String attribute, String value, Document doc)
    {
        m_collection.updateMany(Filters.eq(attribute,value), doc);
        System.out.println("Updated document: " + attribute + " : " + value);
    }

    public void UpdateDocuments(String attribute, String value, String newAttribute, String newValue)
    {
        m_collection.updateMany(Filters.eq(attribute,value), Updates.set(newAttribute,newValue));
        System.out.println("Updated document: " + attribute + " : " + value);
    }

    public void DeleteOneDocument(String attribute, String value)
    {
        DeleteResult result = m_collection.deleteOne(new Document(attribute,value));
        System.out.println("Document delete result: " + result.toString());
    }

    public void DeleteDocuments(String attribute, String value)
    {
        DeleteResult result = m_collection.deleteMany(new Document(attribute,value));
        System.out.println("Document delete result: " + result.toString());
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

    Block<GridFSFile> printGrid = new Block<GridFSFile>()
    {
        public void apply(final GridFSFile gridFSFile)
        {
            System.out.println(gridFSFile.toString());
        }
    };

    private MongoClient m_client;
    private MongoDatabase m_database;
    private MongoCollection<Document> m_collection;
    private GridFSBucket m_gridFSBucket;
    private MongoClient m_client_FS;
    private MongoDatabase m_database_FS;
};
