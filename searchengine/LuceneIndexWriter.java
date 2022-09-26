
package searchengine;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import java.util.List;
import java.util.Set;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author yuyue
 */
public class LuceneIndexWriter {
    
    String indexPath = "./index";

    String jsonFilePath = "./json_files/reviews_Musical_Instruments_5.json";

    IndexWriter indexWriter = null;
    Directory dir = null;
    Analyzer analyzer = null;

    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex(){
        JSONArray jsonObjects = parseJSONFile();
        openIndex();
        addDocuments(jsonObjects);
        finish();
    }

    /**
     * Parse a Json file. The file path should be included in the constructor
     */
    public JSONArray parseJSONFile(){

        //Get the JSON file, in this case is in ~/resources/test.json
        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects= JSONValue.parse(readerJson);
        JSONArray arrayObjects=(JSONArray)fileObjects;

        return arrayObjects;

    }

    public boolean openIndex(){
        try {
            File f=new File(indexPath);
            dir = FSDirectory.open(f.toPath());
            analyzer = new StandardAnalyzer();
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
     */
    public void addDocuments(JSONArray jsonObjects){
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                //Class type = object.get(field).getClass();
                //System.out.println(field);
                if(field.equals("reviewText")){
                    doc.add(new TextField(field, (String)object.get(field), Field.Store.YES));
                    //System.out.println(new TextField(field, (String)object.get(field), Field.Store.YES));              
                }else if(field.equals("reviewID")){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                    doc.add(new SortedDocValuesField(field, new BytesRef((String)object.get(field))));
                }else if(field.equals("asin")){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
                    doc.add(new SortedDocValuesField(field, new BytesRef((String)object.get(field))));
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
    
}
