package nparq.server;

import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Updates.inc;
import java.util.ArrayList;
import org.bson.Document;
import java.util.Calendar;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MyDatabase
{
    private MongoClient mongo;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private static final String DATABASE_NAME = "nparq3";
    private static final String COLLECTION_NAME = "shift";
            
    public MyDatabase()
    {
        try
        {
            mongo = new MongoClient("localhost", 27017);
            
            database = mongo.getDatabase(DATABASE_NAME);
            collection = database.getCollection(COLLECTION_NAME);
        }
        catch(Exception e)
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public boolean find(String place_name)
    {
        Document doc = collection.find(eq("name", place_name)).first();
        return (doc != null);
    }
    
    public JSONArray search(String city_name,
            ArrayList<String> wants, ArrayList<String> nwants)
    {
        JSONArray json_array = new JSONArray();
        Block<Document> printBlock = new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
                json_array.add(document);
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
        if(nwants != null)
        {
            for(String nwant : nwants)
            {
                ibson.add(exists(nwant, false));
            }
        }
        
        collection.find(and(ibson)).forEach(printBlock);
        return json_array;
    }
    
    public void add(JSONObject obj)
    {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        Document doc = new Document("ref", (long)currentTimestamp.getTime())
            .append("city", obj.get("city"))
            .append("name", obj.get("name"))
            .append("lat", obj.get("lat"))
            .append("long", obj.get("long"))
            .append("photo", "no_photo")
            .append("contains", obj.get("contains"))
            .append("validated", false)
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
