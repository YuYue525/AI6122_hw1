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
import java.util.regex.Pattern;


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


        System.out.println("If you want to query by name, please input 1.\r\nOtherwise, It will be searched by review text.");
        Query q;
        String f;
        querystr=scanner.nextLine();
        if(querystr.equals("1")){
            f="reviewerName";
        }else{
            f="reviewText";
        }
        System.out.println("If you want to do fuzzy query, please input 1");
        boolean b= scanner.nextLine().equals("1");
        System.out.println("What do you want to query?");
        querystr=scanner.nextLine();
        if(b) {
            q = new QueryParser(f, luceneIndexWriter.analyzer).parse(querystr+"~0.5");
        }else{
            q = new QueryParser(f, luceneIndexWriter.analyzer).parse(querystr);
        }
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.


        // 3. search
        long startTime=System.currentTimeMillis();
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(luceneIndexWriter.dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        long endTime=System.currentTimeMillis();

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        System.out.println("Use time： "+(endTime-startTime)+"ms");
        System.out.println("For show, Long review texts will be truncated to one hundred characters");
        int showPage=0;
        do{
            System.out.print(("NO") + ". " + "   ID     " + "\t" +("overall")+ "\t"+("helpful")+ "\t"+("reviewTime")+"\t");
            System.out.printf("%-40s\t","Name");
            System.out.println("Text");
            for(int i=showPage;i<Math.min(hits.length,10+showPage);++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.print((i + 1) + ". " + d.get("asin") + "\t"+d.get("overall")+ "\t\t"+d.get("helpful")+ "\t\t"+d.get("reviewTime")+"\t" );
                System.out.printf("%-40s\t",d.get("reviewerName"));
                int len=d.get("reviewText").length();
                System.out.printf("%-100s\t\r\n",d.get("reviewText").substring(0,Math.min(100,len)));
            }

            if(hits.length>showPage+10) {
                System.out.println("There are still " + (hits.length - showPage - 10 ) + " results to show.\r\nIf you want to see more, please push Enter." +
                        "\r\nIf you want to quit, please input q.");
                if(scanner.nextLine().equals("q")){
                    break;
                }
                else{
                    showPage= Math.min(hits.length, showPage + 10);
                }
            }
        }while(showPage<hits.length);

        while(true) {
            System.out.println("If you want to see detail of any review, please input the right NO.\r\n" +
                    "If you want to quit, please push Enter.");
            String num=scanner.nextLine();
            if (isNumeric(num)) {
                int i=Integer.parseInt(num);
                if(i> hits.length){
                    System.out.println("Your input NO is too big. Please check again.");
                }else{
                    int docId = hits[i-1].doc;
                    Document d = searcher.doc(docId);
                    System.out.println("Name:\t"+d.get("reviewerName")+"\r\nText:\r\n"+d.get("reviewText"));
                }
            }else break;
        }
        System.out.println("Search end. Thanks for using.");

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

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[1-9][0-9]+");
        return pattern.matcher(str).matches();
    }
    
    
}
