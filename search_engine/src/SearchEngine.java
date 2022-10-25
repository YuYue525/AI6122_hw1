
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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;


/**
 *
 * @author yuyue
 */


public class SearchEngine {
    
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[1-9][0-9]*");
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
        System.out.println("=====Welcome to our Search Engine!=====\r\n");
        boolean isOver = false;
        while(!isOver){

            System.out.println("Please choose your query type: [1/2/3/4/5/q]\r\n");
            System.out.println("1. Type '1' for term query on [asin/reviewText/reviewerName/reviewerID/summary].");
            System.out.println("2. Type '2' for phrase query on [reviewText/reviewerName/summary].");
            System.out.println("3. Type '3' for range search on [overall].");
            System.out.println("4. Type '4' for other high-level query on [reviewText/reviewerName/summary].");
            System.out.println("5. Type '5' for multiple field searching.");
            System.out.println("Type 'q' to quit.\r\n");
            System.out.println("Your choice: ");
            String input = scanner.nextLine();
            if(input.equals("1"))
            {
                System.out.println("Please type the search field: ");
                String fieldString = scanner.nextLine();
                System.out.println("Please type the search term query: ");
                String queryString;
                if(fieldString.equals("asin") || fieldString.equals("reviewerID"))
                    queryString = scanner.nextLine();
                else
                    queryString = scanner.nextLine().toLowerCase();
                System.out.println("Please type the max number of results: ");
                int totalhits = Integer.parseInt(scanner.nextLine());
                
                Query termQuery = new TermQuery(new Term(fieldString, queryString));
                
                searchQueryAndDisplay(totalhits, termQuery, dir, scanner);
                
                System.out.println("Do you want to continue to search? [type 'y' to continue]\r\n");
                String again = scanner.nextLine();
                if(again.equals("y"))
                    continue;
                else
                    break;
                
            }else if(input.equals("2")){
                System.out.println("Please type the search field: ");
                String fieldString = scanner.nextLine();
                System.out.println("Please type the search phrase query: ");
                String queryString = scanner.nextLine().toLowerCase();
                System.out.println("Please type the max number of results: ");
                int totalhits = Integer.parseInt(scanner.nextLine());
                
                String[] tokens = queryString.split(" ");
                int i = 0;
                PhraseQuery.Builder builder = new PhraseQuery.Builder();
                for (String token :tokens){
                    builder.add(new Term(fieldString, token), i);
                    i++;
                }
                PhraseQuery phraseQuery = builder.build();
                
                searchQueryAndDisplay(totalhits, phraseQuery, dir, scanner);
                
                System.out.println("Do you want to continue to search? ['y' to continue] \r\n");
                String again = scanner.nextLine();
                if(!again.equals("y"))
                    break;
      
            }else if(input.equals("3")){
                System.out.println("Please type the lower bound: ");
                double low = Double.parseDouble(scanner.nextLine());
                System.out.println("Please type the higher bound: ");
                double high = Double.parseDouble(scanner.nextLine());
                System.out.println("Please type the max number of results: ");
                int totalhits = Integer.parseInt(scanner.nextLine());
                
                Query rangeQuery = DoublePoint.newRangeQuery("overall", low, high);

                searchQueryAndDisplay(totalhits, rangeQuery, dir, scanner);
                
                System.out.println("Do you want to continue to search? ['y' to continue] \r\n");
                String again = scanner.nextLine();
                if(!again.equals("y"))
                    break;
                
            }else if(input.equals("4")){
                System.out.println("Please type the search field: ");
                String fieldString = scanner.nextLine();
                System.out.println("Please type the search query: ");
                String queryString = scanner.nextLine();
                System.out.println("Please type the max number of results: ");
                int totalhits = Integer.parseInt(scanner.nextLine());
                
                QueryParser queryParser = new QueryParser(fieldString, analyzer);
                Query parsedQuery = queryParser.parse(queryString);
                
                searchQueryAndDisplay(totalhits, parsedQuery, dir, scanner);
                
                System.out.println("Do you want to continue to search? ['y' to continue] \r\n");
                String again = scanner.nextLine();
                if(!again.equals("y"))
                    break;
            }else if(input.equals("5")){
                BooleanQuery.Builder builder = new BooleanQuery.Builder();
                
                while(true){
                    System.out.println("Please type the search field: ['q' for quit]");
                    String fieldString = scanner.nextLine();
                    if(fieldString.equals("asin") || fieldString.equals("reviewerID")){
                        System.out.println("Please type the " + fieldString + ": ");
                        String stringField = scanner.nextLine();
                        Query stringFieldQuery = new TermQuery(new Term(fieldString, stringField));
                        
                        OUTER_2:
                        while (true) {
                            System.out.println("Please choose BooleanClause type: [1/2/3]");
                            System.out.println("1. MUST");
                            System.out.println("2. SHOULD");
                            System.out.println("3. MUST_NOT");
                            String type = scanner.nextLine();
                            switch (type) {
                                case "1" -> {
                                    builder.add(stringFieldQuery, BooleanClause.Occur.MUST);
                                    break OUTER_2;
                                }
                                case "2" -> {
                                    builder.add(stringFieldQuery, BooleanClause.Occur.SHOULD);
                                    break OUTER_2;
                                }
                                case "3" -> {
                                    builder.add(stringFieldQuery, BooleanClause.Occur.MUST_NOT);
                                    break OUTER_2;
                                }
                                default -> System.out.println("Wrong input, please enter again:");
                            }
                        } 

                    }else if(fieldString.equals("overall")){
                        System.out.println("Please type the lower bound: ");
                        double low = Double.parseDouble(scanner.nextLine());
                        System.out.println("Please type the higher bound: ");
                        double high = Double.parseDouble(scanner.nextLine());
                        Query rangeQuery = DoublePoint.newRangeQuery("overall", low, high);
                        OUTER_1:
                        while (true) {
                            System.out.println("Please choose BooleanClause type: [1/2/3]");
                            System.out.println("1. MUST");
                            System.out.println("2. SHOULD");
                            System.out.println("3. MUST_NOT");
                            String type = scanner.nextLine();
                            switch (type) {
                                case "1" -> {
                                    builder.add(rangeQuery, BooleanClause.Occur.MUST);
                                    break OUTER_1;
                                }
                                case "2" -> {
                                    builder.add(rangeQuery, BooleanClause.Occur.SHOULD);
                                    break OUTER_1;
                                }
                                case "3" -> {
                                    builder.add(rangeQuery, BooleanClause.Occur.MUST_NOT);
                                    break OUTER_1;
                                }
                                default -> System.out.println("Wrong input, please enter again:");
                            }
                        } 
                    }else if(fieldString.equals("reviewText") || fieldString.equals("reviewerName") || fieldString.equals("summary")){
                        System.out.println("Please type the search query: ");
                        String queryString = scanner.nextLine();
                        
                        QueryParser queryParser = new QueryParser(fieldString, analyzer);
                        Query parsedQuery = queryParser.parse(queryString);

                        OUTER:
                        while (true) {
                            System.out.println("Please choose BooleanClause type: [1/2/3]");
                            System.out.println("1. MUST");
                            System.out.println("2. SHOULD");
                            System.out.println("3. MUST_NOT");
                            String type = scanner.nextLine();
                            switch (type) {
                                case "1" -> {
                                    builder.add(parsedQuery, BooleanClause.Occur.MUST);
                                    break OUTER;
                                }
                                case "2" -> {
                                    builder.add(parsedQuery, BooleanClause.Occur.SHOULD);
                                    break OUTER;
                                }
                                case "3" -> {
                                    builder.add(parsedQuery, BooleanClause.Occur.MUST_NOT);
                                    break OUTER;
                                }
                                default -> System.out.println("Wrong input, please enter again:");
                            }
                        } 
                    }else if (fieldString.equals("q"))
                        break;
                    else
                        System.out.println("Wrong input, please enter again:");
                }

                System.out.println("Please type the max number of results: ");
                int totalhits = Integer.parseInt(scanner.nextLine());
                
                BooleanQuery booleanQuery = builder.build();
                
                searchQueryAndDisplay(totalhits, booleanQuery, dir, scanner);
                
                System.out.println("Do you want to continue to search? ['y' to continue] \r\n");
                String again = scanner.nextLine();
                if(!again.equals("y"))
                    break;
            }else if(input.equals("q")){
                System.out.println("See you!\r\n");
                break;
            }else
                System.out.println("Wrong input! Please enter again: \r\n");
        }
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        //System.out.println(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
    
    private static void searchQueryAndDisplay(int totalhits, Query query, Directory dir, Scanner scanner) throws IOException {
        //search
        long startTime=System.currentTimeMillis();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(query, totalhits);
        ScoreDoc[] hits = docs.scoreDocs;
        long endTime=System.currentTimeMillis();
        
        //display results
        
        System.out.println("For show, Long review texts are truncated.\n");

        
        System.out.println("Found " + hits.length + " hits.");
        System.out.println("Use time： "+(endTime-startTime)+"ms. \n");        
        
        int displayNum = 10;
        int showPage = 0;
        
        do{
            System.out.print("NO. \treviewerID \treviewName \tasin \t\treviewText \t \t \t \t \toverall \treviewTime \tscore \t\tdocID \n");
            
            for(int i = showPage;i < Math.min(hits.length, showPage + displayNum); i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                try{
                    System.out.print((i + 1) + ". \t" + d.get("reviewerID") + "\t" + d.get("reviewerName").substring(0, Math.min(7, d.get("reviewerName").length())) +
                        "\t\t"+ d.get("asin")+"\t" +d.get("reviewText").substring(0, Math.min(45,d.get("reviewText").length())) + "\t"+
                        d.get("overall") +"\t\t"+ d.get("reviewTime") + "\t"+ hits[i].score+"\t"+hits[i].doc+"\n");
                }catch(Exception e){
                    continue;
                }
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
                    System.out.println("\nName:\r\n"+d.get("reviewerName")+"\r\nText:\r\n"+d.get("reviewText"));
                }
            }else {
                break;
            }

        }
        System.out.println("Search end. Thanks for using.\r\n");
    }
    
}
