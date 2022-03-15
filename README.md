# search-engine
HKUST COMP4321
Run
  1. Git clone to local
  2. Change to this project path
  3. In terminal input: java -cp lib/rocksdbjni-6.19.0-linux64.jar:lib/jsoup-1.13.1.jar:/root/project/phase1/:.  Crawler 

Compile
  1. Compile Crawler.java: javac -cp lib/rocksdbjni-6.19.0-linux64.jar:lib/jsoup-1.13.1.jar:/root/project/phase1/  Crawler.java
  2. Compile InvertedIndex.java
    2.1. Move project/phase1/temp/InvertedIndex.java to  project/phase1/InvertedIndex.java
    2.2. Compile InvertedIndex.java: javac -cp lib/rocksdbjni-6.19.0-linux64.jar InvertedIndex.java 
    2.3. Move project/phase1/InvertedIndex.java back to  project/phase1/temp/InvertedIndex.java (If miss this step, project cannot run.)
    2.4. Replace project/phase1/mypackage/InvertedIndex.class by project/phase1/InvertedIndex.class

Progress
  1. InvertedIndex.java (skeleton is from lab1) (Use '::' to separate elements, any character without conflict can be used)
    1.1. Finish mapping url<=>id  
      1.1.1. void addUrlID(String url) //Give a new url a new id
      1.1.2. String getUrlID(String url) //Input url get id
      1.1.3. String getUrlbyID(int id) //Input id get url
      1.1.4. void printUrlID() //Print
    1.2. Finish mapping id<=>info (lastModified & size & title)
      1.2.1. void addUrlInfo(String url, String title, String lastModified, String size)
      1.2.2. String[] getUrlInfo(String url) //(title,url,lastModified,size)
      1.2.3. String[] getUrlInfobyID(String url)
      1.2.4. void printUrlInfo()
    1.3. Finish mapping id<=>child (All children links of a link)
      1.3.1. void addUrlChild(String root, Vector<String> links)
      1.3.2. String[] getUrlChild(String root)
      1.3.3. String[] getUrlChildbyID(String id)
      1.3.4. void printUrlChild()

  2. Crawler.java (skeleton is from lab2)
    2.1. void CreateRocksDB(String RocksDBPath)
    2.2. void addUrlList(Vector<String> urllist)
    2.3. void addUrlInfo(String url, Response res)
    2.4. void addUrlInfo(String url, Response res)
    2.5. void printDatabase(int mode)


Todo
  1. InvertedIndex.java
    1.1. Mapping words<=>id
    1.2. Mapping words<=>url (Examples: 'words-url_url::0':'words-url_words::0:cat::1:dog::2:pig')
    1.3. Mapping Frequent words<=>url
    1.4. Mapping Frequent words<=>Frequency
  2. Crawler.java
    2.1. Remove stopwords from raw content of pages (Use stopwords.txt)
    2.2. Compute words frequency
    2.3. Write spider_result.txt
  3. Document
  
  
