
package searchengine;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import static java.lang.Runtime.version;
import java.util.*;
import java.util.Set;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author yuyue
 */
public class LuceneIndexWriter {
    
    String indexPath = null;

    String[] jsonFilePaths = null;

    IndexWriter indexWriter = null;
    Directory dir = null;
    Analyzer analyzer = null;

    public LuceneIndexWriter(String indexPath, String[] jsonFilePaths) {
        this.indexPath = indexPath;
        this.jsonFilePaths = jsonFilePaths;
    }

    public void createIndex(){
        ArrayList<JSONArray> jsonObjects = parseJSONFile();
        openIndex();
        for (JSONArray jsonObject : jsonObjects){
            addDocuments(jsonObject);
        }
        finish();
    }

    /**
     * Parse a Json file. The file path should be included in the constructor
     */
    public ArrayList<JSONArray> parseJSONFile(){
        ArrayList<JSONArray> results = new ArrayList<JSONArray>();
        //Get the JSON file, in this case is in ~/resources/test.json
        for(String jsonFilePath : jsonFilePaths){
            InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
            System.out.println(jsonFilePath);
            Reader readerJson = new InputStreamReader(jsonFile);

            //Parse the json file using simple-json library
            Object fileObjects= JSONValue.parse(readerJson);
            JSONArray arrayObjects=(JSONArray)fileObjects;
            results.add(arrayObjects); 
        }
        return results;

    }

    public boolean openIndex(){
        try {
            File f=new File(indexPath);
            dir = FSDirectory.open(f.toPath());
            //analyzer = new EnglishAnalyzer();
            analyzer = new StandardAnalyzer();
            //analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //Always overwrite the directory
            iwc.setOpenMode(OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, iwc);

            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());

        }
        return false;

    }

    /**
     * Add documents to the index
     * @param jsonObjects
     */
    public void addDocuments(JSONArray jsonObjects){
        long startTime=System.currentTimeMillis();
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                //Class type = object.get(field).getClass();
                //System.out.println(field);
                if(field.equals("reviewText")){
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                    //System.out.println(new TextField(field, (String)object.get(field), Field.Store.YES));
                    
                }else if(field.equals("reviewerName")){
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                    
                }else if(field.equals("summary")){
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                
                }else if(field.equals("reviewTime")){
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                    
                }else if(field.equals("reviewerID")){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                    // System.out.println(new StringField(field, (String)object.get(field), Field.Store.YES));

                }else if(field.equals("asin")){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                    //System.out.println(new StringField(field, (String)object.get(field), Field.Store.YES));
                    
                }else if(field.equals("overall")){
                    //System.out.println(object.get(field));
                    doc.add(new DoublePoint(field, (double)object.get(field)));
                    //doc.add(new NumericDocValuesField(field, (double)object.get(field)));
                    doc.add(new StoredField(field, (double)object.get(field)));
                    
                }
                
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
        long endTime=System.currentTimeMillis();
        
        System.out.println("Indexing Time: " + (endTime-startTime) + " ms");
    }

    /**
     * Write the document to the index and close it
     */
    public void finish(){
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = "./index/";
        String[] jsonFilePaths = { 
            "../data/reviews_Musical_Instruments_5.json", 
            "../data/reviews_Patio_Lawn_and_Garden_5.json"
        };
        LuceneIndexWriter luceneIndexWriter = new LuceneIndexWriter(indexPath, jsonFilePaths);
        luceneIndexWriter.createIndex();
    }
    
}
