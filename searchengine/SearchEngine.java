
package searchengine;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.store.FSDirectory;
import searchengine.LuceneIndexWriter;
import java.util.regex.Pattern;


/**
 *
 * @author yuyue
 */


public class SearchEngine {
    
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        
        String indexPath = "./index";
        File f=new File(indexPath);
        Directory dir = FSDirectory.open(f.toPath());
        
        //whether there are stopwords for the query
        Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        //Analyzer analyzer = new StandardAnalyzer();

        //query
        Scanner scanner=new Scanner(System.in);
        String querystr = args.length > 0 ? args[0] : "Orb-3 Spa Enzymes non-toxic product an Endless Pool";
        
        Query q;
        //q = DoublePoint.newExactQuery("overall", 5.0);
        //q = DoublePoint.newRangeQuery("overall", 4.1, 5.0);
        q = new QueryParser("reviewText", analyzer).parse(querystr);

        //search
        long startTime=System.currentTimeMillis();
        int totalhits = 1000000;
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, totalhits);
        ScoreDoc[] hits = docs.scoreDocs;
        long endTime=System.currentTimeMillis();

        //display results
        
        System.out.println("For show, Long review texts are truncated to one hundred characters.\n");

        
        System.out.println("Found " + hits.length + " hits.");
        System.out.println("Use time： "+(endTime-startTime)+"ms. \n");        
        
        int displayNum = 10;
        int showPage = 0;
        
        do{
            System.out.print("NO. \t reviewerID \t reviewName \t\t asin \t\t reviewText \t \t \t \t \t overall \t reviewTime \t score \t\t docID \n");
            
            for(int i = showPage;i < Math.min(hits.length, showPage + displayNum); i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.print((i + 1) + ". \t" + d.get("reviewerID") + "\t" + d.get("reviewerName").substring(0, Math.min(15, d.get("reviewerName").length())) +
                        "\t\t"+ d.get("asin")+"\t" +d.get("reviewText").substring(0,Math.min(45,d.get("reviewText").length())) + "\t"+
                        d.get("overall") +"\t\t"+ d.get("reviewTime") + "\t"+ hits[i].score+"\t"+hits[i].doc+"\n");

            }

            if(hits.length > showPage + displayNum) {
                System.out.println("\nThere are still " + (hits.length - (showPage + displayNum) ) + " results to show.\r\nIf you want to see more, please click anything + Enter." +
                        "\r\nIf you want to quit, please input q.");
                String input = scanner.nextLine();
                if(input.equals("q"))
                    break;
                else
                    showPage= Math.min(hits.length, showPage + displayNum);
            }
            else
                break;
            
        }while(showPage < hits.length);
        
        while(true) {
            System.out.println("\nIf you want to see detail of any review, please input the right NO.\nIf you want to quit, please click anything + Enter.");
            String num=scanner.nextLine();
            if (isNumeric(num) && Integer.parseInt(num) > 0) {
                int i=Integer.parseInt(num);
                if(i> hits.length){
                    System.out.println("Your input NO is too big. Please check again.");
                }else{
                    int docId = hits[i-1].doc;
                    Document d = searcher.doc(docId);
                    System.out.println("\nName:\t"+d.get("reviewerName")+"\r\nText:\r\n"+d.get("reviewText"));
                }
            }else {
                break;
            }

        }
        System.out.println("Search end. Thanks for using.");


        reader.close();
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        //System.out.println(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
    
    
    
}
