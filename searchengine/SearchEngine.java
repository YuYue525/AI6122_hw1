package SearchEngine;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import org.json.simple.JSONArray;

import java.io.*;
import java.util.Scanner;


/**
 *
 * @author yuyue
 */


public class SearchEngine {
    
    
    public static void main(String[] args) throws IOException, ParseException {
        
        LuceneIndexWriter luceneIndexWriter = new LuceneIndexWriter("./index/", "/json_files/reviews_Musical_Instruments_5.json");
        JSONArray jsonArray = luceneIndexWriter.parseJSONFile();
        if (luceneIndexWriter.openIndex()){
            luceneIndexWriter.addDocuments(jsonArray);
        }
        luceneIndexWriter.finish();

        
        /*
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        
        // 1. create the index
        Directory index = new ByteBuffersDirectory();
       

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "Lucene in Action", "193398817");
        addDoc(w, "Lucene for Dummies", "55320055Z");
        addDoc(w, "Managing Gigabytes", "55063554A");
        addDoc(w, "The Art of Computer Science", "9900333X");
        w.close();
        */
        

        // 2. query
        Scanner scanner=new Scanner(System.in);
        String querystr;

        /*
        if(args.length > 0){
            querystr=args[0];
        }
        else{
            System.out.println("What do you want to query?");
            querystr=scanner.nextLine();
        }
         */


        System.out.println("What fields do you want to query?\n1:name\n2:text");
        Query q;
        String f;
        querystr=scanner.next();
        if(querystr.equals("1")){
            f="reviewerName";
        }else{
            f="reviewText";
        }
        System.out.println("If you want to do fuzzy query, please enter Y");
        boolean b=scanner.next().equals("Y")?true:false;
        System.out.println("What do you want to query?");
        querystr=scanner.next();
        if(b) {
            q = new QueryParser(f, luceneIndexWriter.analyzer).parse(querystr+"~0.5");
        }else{
            q = new QueryParser(f, luceneIndexWriter.analyzer).parse(querystr);
        }
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.


        // 3. search
        int hitsPerPage = 30;
        IndexReader reader = DirectoryReader.open(luceneIndexWriter.dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("asin") + "\t" + d.get("reviewerName") + "\t" + d.get("reviewText"));
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
        luceneIndexWriter.dir.close();
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
