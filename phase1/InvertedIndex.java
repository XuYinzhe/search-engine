import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;  
import org.rocksdb.RocksIterator;


public class InvertedIndex
{
    private RocksDB db;
    private Options options;
    private int urlID;
    private int wordID;
    private String separation="::";

    InvertedIndex(String dbPath) throws RocksDBException
    {
        // the Options class contains a set of configurable DB options
        // that determines the behaviour of the database.
        this.options = new Options();
        this.options.setCreateIfMissing(true);
        this.urlID = 0;
        this.wordID = 0;

        // creat and open the database
        this.db = RocksDB.open(options, dbPath);
    }

    public void addUrlID(String url) throws RocksDBException{
        String encodeUrl = "url_id-url" + this.separation + url;
        byte[] value = db.get(encodeUrl.getBytes());
        if(value == null){
            String encodeid = "url_id-id" + this.separation + this.urlID;
            this.urlID+=1;
            db.put(encodeUrl.getBytes(),encodeid.getBytes());
        }
    }

    public String getUrlID(String url) throws RocksDBException{
        String encodeUrl = "url_id-url" + this.separation + url;
        byte[] value = db.get(encodeUrl.getBytes());
        if(value != null){
            String id = new String(value);
            String[] decodeid = id.split(this.separation);
            return decodeid[1];
        }
        return null;
    }

    public String getUrlbyID(int id) throws RocksDBException{
        String encodeid = "url_id-id" + this.separation + id;
        //System.out.println(encodeid);
        RocksIterator iter = db.newIterator();
        for(iter.seekToFirst(); iter.isValid(); iter.next()){
            String value = new String(iter.value());
            //String[] decodeid = value.split(":");
            //System.out.println(decodeid[1]);
            if(value.equals(encodeid)){
                String url = new String(iter.key());
                String[] decodeurl = url.split(this.separation);
                return decodeurl[1];
            }
        }
        return null;
    }

    public void printUrlID() throws RocksDBException{
        RocksIterator iter = db.newIterator();
        for(iter.seekToFirst(); iter.isValid(); iter.next()){
            String key = new String(iter.key());
            String[] decodekey = key.split("-");
            if(decodekey[0].equals("url_id"))
                System.out.println(new String(iter.key()) + ", " + new String(iter.value()));
        }
        
    }

    public void addEntry(String word, int x, int y) throws RocksDBException
    {
        // Add a "docX Y" entry for the key "word" into hashtable
        // ADD YOUR CODES HERE
        String value="";
        if(this.db.get(word.getBytes())==null){
            value = "doc" + x + " " + y + " ";
        }
        else{
            String curValue = new String(this.db.get(word.getBytes()));
            value = curValue + "doc" + x + " " + y + " ";
        }
        this.db.put(word.getBytes(), value.getBytes());
    }
    public void delEntry(String word) throws RocksDBException
    {
        // Delete the word and its list from the hashtable
        // ADD YOUR CODES HERE
        this.db.remove(word.getBytes());
    } 
    public void printAll() throws RocksDBException
    {
        // Print all the data in the hashtable
        // ADD YOUR CODES HERE
        RocksIterator iter = this.db.newIterator();
        for(iter.seekToFirst(); iter.isValid(); iter.next()){
            System.out.println(new String(iter.key()) + " = " + new String(iter.value()));
        }
    }    
    
    public static void main(String[] args)
    {
        try
        {
            // a static method that loads the RocksDB C++ library.
            RocksDB.loadLibrary();

            // modify the path to your database
            String path = "bd";

            InvertedIndex index = new InvertedIndex(path);
            index.clear();
    
            /** 
            index.addEntry("cat", 2, 6);
            index.addEntry("dog", 1, 33);
            System.out.println("First print");
            index.printAll();
            
            index.addEntry("cat", 8, 3);
            index.addEntry("dog", 6, 73);
            index.addEntry("dog", 8, 83);
            index.addEntry("dog", 10, 5);
            index.addEntry("cat", 11, 106);
            System.out.println("Second print");
            index.printAll();
            
            index.delEntry("dog");
            System.out.println("Third print");
            index.printAll();
            */
            index.addUrlID("https://www.cse.ust.hk/");
            System.out.println(index.getUrlID("https://www.cse.ust.hk/"));
            System.out.println(index.getUrlbyID(0));

            index.addUrlID("http://www.cse.ust.hk/~dlee");
            System.out.println(index.getUrlbyID(1));

            index.printUrlID();
        }
        catch(RocksDBException e)
        {
            System.err.println(e.toString());
        }
    }
}
