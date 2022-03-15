# search-engine
HKUST COMP4321<br>
### Run
  1. Git clone to local
  2. Change to this project path
  3. In terminal input: java -cp lib/rocksdbjni-6.19.0-linux64.jar:lib/jsoup-1.13.1.jar:/root/project/phase1/:.  Crawler 



### Compile
  1. Compile Crawler.java: javac -cp lib/rocksdbjni-6.19.0-linux64.jar:lib/jsoup-1.13.1.jar:/root/project/phase1/  Crawler.java<br>
  2. Compile InvertedIndex.java<br>
    2.1. Move project/phase1/temp/InvertedIndex.java to  project/phase1/InvertedIndex.java<br>
    2.2. Compile InvertedIndex.java: javac -cp lib/rocksdbjni-6.19.0-linux64.jar InvertedIndex.java <br>
    2.3. Move project/phase1/InvertedIndex.java back to  project/phase1/temp/InvertedIndex.java (If miss this step, project cannot run.)<br>
    2.4. Replace project/phase1/mypackage/InvertedIndex.class by project/phase1/InvertedIndex.class<br>



### Progress
#### InvertedIndex.java (skeleton is from lab1) (Use '::' to separate elements, any character without conflict can be used)<br>
   1. Finish mapping url<=>id  <br>
      - void addUrlID(String url) //Give a new url a new id<br>
      - String getUrlID(String url) //Input url get id<br>
      - String getUrlbyID(int id) //Input id get url<br>
      - void printUrlID() //Print<br>
   1. Finish mapping id<=>info (lastModified & size & title)<br>
      - void addUrlInfo(String url, String title, String lastModified, String size)<br>
      - String[] getUrlInfo(String url) //(title,url,lastModified,size)<br>
      - String[] getUrlInfobyID(String url)<br>
      - void printUrlInfo()<br>
   1. Finish mapping id<=>child (All children links of a link)<br>
      - void addUrlChild(String root, Vector<String> links)<br>
      - String[] getUrlChild(String root)<br>
      - String[] getUrlChildbyID(String id)<br>
      - void printUrlChild()<br>

 #### Crawler.java (skeleton is from lab2)<br>
 1. void CreateRocksDB(String RocksDBPath)<br>
 2. void addUrlList(Vector<String> urllist)<br>
 3. void addUrlInfo(String url, Response res)<br>
 4. void addUrlInfo(String url, Response res)<br>
 5. void printDatabase(int mode)<br>

  

### Todo
  1. InvertedIndex.java<br>
    1.1. Mapping words<=>id<br>
    1.2. Mapping words<=>url (Examples: 'words-url_url::0':'words-url_words::0:cat::1:dog::2:pig')<br>
    1.3. Mapping Frequent words<=>url<br>
    1.4. Mapping Frequent words<=>Frequency<br>
  2. Crawler.java<br>
    2.1. Remove stopwords from raw content of pages (Use stopwords.txt)<br>
    2.2. Compute words frequency<br>
    2.3. Write spider_result.txt<br>
  3. Document<br>
  
  
