import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;  
import org.rocksdb.RocksIterator;

import java.util.Vector;
import java.lang.Integer;

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

    //---------------------------- url-id ---------------------------
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
    //-------------------------------------------------------------------

    //---------------------------- id-info ---------------------------
    public void addUrlInfo(String url, String title, String lastModified, String size) throws RocksDBException{
        String id = this.getUrlID(url);
        //System.out.println(id);
        if(id != null){
            String encodeid = "id_info-id" + this.separation + id;
            byte[] value = db.get(encodeid.getBytes());
            //if(value == null){
                String encodeinfo = 
                "id_info-info" + this.separation
                + title + this.separation
                + url + this.separation
                + lastModified + this.separation
                + size;
                //System.out.println(encodeid+","+encodeinfo);
                db.put(encodeid.getBytes(),encodeinfo.getBytes());
            //}
        }
    }

    public String[] getUrlInfo(String url) throws RocksDBException{
        String id = this.getUrlID(url);
        if(id != null){
            return this.getUrlInfobyID(id);
        }
        return null;
    }

    public String[] getUrlInfobyID(String id) throws RocksDBException{
        String encodeid = "id_info-id" + this.separation + id;
        byte[] value = db.get(encodeid.getBytes());
        if(value != null){
            String info = new String(value);
            info = info.replace("id_info-info" + this.separation,"");
            String[] decodeinfo = info.split(this.separation);
            return decodeinfo;
        }
        return null;
    }

    public void printUrlInfo() throws RocksDBException{
        RocksIterator iter = db.newIterator();
        for(iter.seekToFirst(); iter.isValid(); iter.next()){
            String key = new String(iter.key());
            String[] decodekey = key.split("-");
            if(decodekey[0].equals("id_info")){
                String[] id = decodekey[1].split(this.separation);
                String[] info = this.getUrlInfobyID(id[1]);
                System.out.println(id[1] + "," + info[0] + "," + info[1] + "," + info[2] + "," + info[3]);
            }
        }
    } 
    //-------------------------------------------------------------------

    //---------------------------- id-child ---------------------------
    public void addUrlChild(String root, Vector<String> links) throws RocksDBException{
        String id = this.getUrlID(root);
        if(id != null){
            String encodeid = "id_child-id" + this.separation + id;
            String encodechild = "id_child-child";
            encodechild += this.separation + links.size();
            for(String link : links)
                encodechild += this.separation + link;
            //System.out.println(encodechild);
            db.put(encodeid.getBytes(),encodechild.getBytes());
        }
    }

    public String[] getUrlChild(String root) throws RocksDBException{
        String id = this.getUrlID(root);
        if(id != null){
            return this.getUrlChildbyID(id);
        }
        return null;
    }

    public String[] getUrlChildbyID(String id) throws RocksDBException{
        String encodeid = "id_child-id" + this.separation + id;
        //System.out.println(encodeid);
        byte[] value = db.get(encodeid.getBytes());
        if(value != null){
            String child = new String(value);
            //System.out.println(child);
            child = child.replace("id_child-child" + this.separation,"");
            String[] decodechild = child.split(this.separation);
            return decodechild;
        }
        return null;
    }

    public void printUrlChild() throws RocksDBException{
        RocksIterator iter = db.newIterator();
        for(iter.seekToFirst(); iter.isValid(); iter.next()){
            String key = new String(iter.key());
            String[] decodekey = key.split("-");
            if(decodekey[0].equals("id_child")){
                String[] id = decodekey[1].split(this.separation);
                //System.out.println(id[1]);
                String[] child = this.getUrlChildbyID(id[1]);
                //System.out.println(child[0]);
                int num = Integer.parseInt​(child[0]);
                String out = id[1];
                for(int i=1;i<=num;i++)
                    out += "," + child[i];
                System.out.println(out);
            }
        }
    }
    //-------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        try
        {
            // a static method that loads the RocksDB C++ library.
            RocksDB.loadLibrary();

            // modify the path to your database
            String path = "bd";

            InvertedIndex index = new InvertedIndex(path);
            //index.clear();
    
            index.addUrlID("https://www.cse.ust.hk/");
            //System.out.println(index.getUrlID("https://www.cse.ust.hk/"));
            //System.out.println(index.getUrlbyID(0));

            index.addUrlID("http://www.cse.ust.hk/~dlee");
            //System.out.println(index.getUrlbyID(1));

            index.printUrlID();

            index.addUrlInfo("https://www.cse.ust.hk/","HKUST","010101","1024");
            index.printUrlInfo();

            Vector<String> child = new Vector<String>();
            child.add("child1");
            child.add("child2");
            child.add("child3");
            child.add("child4");
            index.addUrlChild("https://www.cse.ust.hk/",child);
            index.printUrlChild();
        }
        catch(RocksDBException e)
        {
            System.err.println(e.toString());
        }
    }
}
