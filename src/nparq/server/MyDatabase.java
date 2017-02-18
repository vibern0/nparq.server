/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nparq.server;

import com.mongodb.BasicDBList;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Updates.inc;
import java.util.ArrayList;
import org.bson.Document;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import org.bson.BsonArray;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;

/**
 *
 * @author bernardovieira
 */
public class MyDatabase
{
    private MongoClient mongo;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private static final String DATABASE_NAME = "nparq2";
    private static final String COLLECTION_NAME = "shift";
            
    public MyDatabase()
    {
        try
        {
            // To connect to mongodb server
            mongo = new MongoClient("localhost", 27017);

            // Now connect to your databases
            database = mongo.getDatabase(DATABASE_NAME);
            collection = database.getCollection(COLLECTION_NAME);
            
            /*Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
            
            collection.insertOne(doc);*/
            
            /*Document myDoc = collection.find(exists("name")).first();
            System.out.println(myDoc.toJson());*/
            
            /*Document myDoc = collection.find().first();
            System.out.println(myDoc.toJson());*/
            
        }
        catch(Exception e)
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public ArrayList<JSONObject> search(String city_name, ArrayList<String> wants)
    {
        ArrayList<JSONObject> search_result = new ArrayList<>();
        //
        
        Block<Document> printBlock = new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
                System.out.println(document.toJson());
                search_result.add(new JSONObject(document));
            }
        };
        
        ArrayList<Bson> ibson = new ArrayList<>();
        ibson.add(eq("city", city_name));
        
        if(wants != null)
        {
            for(String want : wants)
            {
                ibson.add(exists(want));
            }
        }
        
        
        collection.find(and(ibson)).forEach(printBlock);
        
        //
        return search_result;
    }
    
    public void add(JSONObject obj)
    {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        Document doc = new Document("ref", (long)currentTimestamp.getTime())
            .append("city", obj.get("city"))
            .append("name", obj.get("name"))
            .append("photo", "no_photo")
            .append("contains", obj.get("contains"))
            .append("votes", 0)
            .append("up_votes", 0)
            .append("down_votes", 0);

        collection.insertOne(doc);
    }
    
    public void vote(long ref, boolean up)
    {
        if(up)
        {
            collection.updateOne(eq("ref", ref), inc("up_votes", 1));
        }
        else
        {
            collection.updateOne(and(eq("ref", ref),
                    gte("up_votes", 0)), inc("up_votes", -1));
        }
    }
}
