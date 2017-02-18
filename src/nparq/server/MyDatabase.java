/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nparq.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import org.bson.Document;
import java.util.Arrays;

/**
 *
 * @author bernardovieira
 */
public class MyDatabase
{
    public MyDatabase()
    {
        try{

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient("localhost", 27017);

            // Now connect to your databases
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<Document> collection = database.getCollection("test");
            
            /*Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
            
            collection.insertOne(doc);*/
            
            Document myDoc = collection.find(exists("name")).first();
            System.out.println(myDoc.toJson());
            
            /*Document myDoc = collection.find().first();
            System.out.println(myDoc.toJson());*/
            
        }catch(Exception e){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
