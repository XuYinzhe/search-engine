import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;

import java.util.HashSet;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.BufferedWriter;
import org.jsoup.HttpStatusException;
import java.lang.RuntimeException;

import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;  
import org.rocksdb.RocksIterator;


/** The data structure for the crawling queue.
 */
class Link{
	String url;
	int level;
	Link (String url, int level) {  
	    this.url = url;
	    this.level = level; 
	}  
}

@SuppressWarnings("serial")
/** This is customize exception for those pages that have been visited before.
 */
class RevisitException 
	extends RuntimeException {
	public RevisitException() {
	    super();
	}
}

public class Crawler {
	private HashSet<String> urls;     // the set of urls that have been visited before
	public Vector<Link> todos; // the queue of URLs to be crawled
	private int max_crawl_depth = 1;  // feel free to change the depth limit of the spider.
	private int max_pages = 1;
	private InvertedIndex db = null;

	Crawler(String _url, int pages) {
		this.todos = new Vector<Link>();
		this.todos.add(new Link(_url, 1));
		this.urls = new HashSet<String>();
		this.max_pages = pages;
	}
	
	/**
	 * Send an HTTP request and analyze the response.
	 * @return {Response} res
	 * @throws HttpStatusException for non-existing pages
	 * @throws IOException
	 */
	public Response getResponse(String url) throws HttpStatusException, IOException {
		if (this.urls.contains(url)) {
			throw new RevisitException(); // if the page has been visited, break the function
		 }
		
		Connection conn = Jsoup.connect(url).followRedirects(false);
		// the default body size is 2Mb, to attain unlimited page, use the following.
		// Connection conn = Jsoup.connect(this.url).maxBodySize(0).followRedirects(false);
		Response res;
		try {
			/* establish the connection and retrieve the response */
			 res = conn.execute();
			 /* if the link redirects to other place... */
			 if(res.hasHeader("location")) {
				 String actual_url = res.header("location");
				 if (this.urls.contains(actual_url)) {
					throw new RevisitException();
				 }
				 else {
					 this.urls.add(actual_url);
				 }
			 }
			 else {
				 this.urls.add(url);
			 }
		} catch (HttpStatusException e) {
			throw e;
		}
		/* Get the metadata from the result */
		/** 
		String lastModified = res.header("last-modified");
		int size = res.bodyAsBytes().length;
		String htmlLang = res.parse().select("html").first().attr("lang");
		String bodyLang = res.parse().select("body").first().attr("lang");
		String lang = htmlLang + bodyLang;
		System.out.printf("Last Modified: %s\n", lastModified);
		System.out.printf("Size: %d Bytes\n", size);
		System.out.printf("Language: %s\n", lang);
		*/
		return res;
	}
	
	/** Extract words in the web page content.
	 * note: use StringTokenizer to tokenize the result
	 * @param {Document} doc
	 * @return {Vector<String>} a list of words in the web page body
	 */
	public Vector<String> extractWords(Document doc) {
		 Vector<String> result = new Vector<String>();
		// ADD YOUR CODES HERE
		 String contents = doc.body().text(); 
	     StringTokenizer st = new StringTokenizer(contents);
	     while (st.hasMoreTokens()) {
            result.add(st.nextToken());
	     }
	     return result;		
	}
	
	/** Extract useful external urls on the web page.
	 * note: filter out images, emails, etc.
	 * @param {Document} doc
	 * @return {Vector<String>} a list of external links on the web page
	 */
	public Vector<String> extractLinks(Document doc) {
		Vector<String> result = new Vector<String>();
		// ADD YOUR CODES HERE
        Elements links = doc.select("a[href]");
        for (Element link: links) {
        	String linkString = link.attr("href");
        	// filter out emails
        	if (linkString.contains("mailto:")) {
        		continue;
        	}
            result.add(link.attr("href"));
        }
        return result;
	}
	
	
	/** Use a queue to manage crawl tasks.
	 */
	public void crawlLoop() {

		

		while(!this.todos.isEmpty()) {
			Link focus = this.todos.remove(0);
			if (focus.level > this.max_crawl_depth) break; // stop criteria
			if (this.urls.contains(focus.url)) continue;   // ignore pages that has been visited
			/* start to crawl on the page */
			try {
				Response res = this.getResponse(focus.url);
				Document doc = res.parse();
				
				Vector<String> words = this.extractWords(doc);		
				System.out.println("\nWords:");
				for(String word: words)
					System.out.print(word + ", ");
		
				Vector<String> links = this.extractLinks(doc);
				System.out.printf("\n\nLinks:");
				for(String link: links) {
					System.out.println(link);
					this.todos.add(new Link(link, focus.level + 1)); // add links
				}
			} catch (HttpStatusException e) {
	            // e.printStackTrace ();
				System.out.printf("\nLink Error: %s\n", focus.url);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} catch (RevisitException e) {
	    	}
		}
		
	}

	public void CreateRocksDB(String RocksDBPath){
		try{
			RocksDB.loadLibrary();
			this.db = new InvertedIndex(RocksDBPath);
			//this.db.clear();
		}
		catch(RocksDBException e){
			System.err.println(e.toString());
		}
	}

	public void addUrlList(Vector<String> urllist){
		for(String url : urllist){
			try{
				this.db.addUrlID(url);
			}
			catch(RocksDBException e){
				System.err.println(e.toString());
			}
		}
	}

	public void addUrlInfo(String url, Response res){
		try{
			String lastModified = res.header("last-modified");
			int size = res.bodyAsBytes().length;
			String title = res.parse().title();

			String size_ = size + " bytes";
			this.db.addUrlInfo(url,title,lastModified,size_);
		}
		catch(RocksDBException e){
			System.err.println(e.toString());
		}
	}

	public void addUrlChild(String root, Vector<String> links){
		try{
			
		}
		catch(RocksDBException e){
			System.err.println(e.toString());
		}
	}
	
	public static void main (String[] args) {
		String url = "https://www.cse.ust.hk/";
		String dbPath = 'db';
		int max_pages=1;
		Crawler crawler = new Crawler(url, max_pages);
		crawler.CreateRocksDB(dbPath);
		crawler.crawlLoop();
		System.out.println("\nSuccessfully Returned");
	}
}
	