package main;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheUtils {
    public final static String BASE_URI = "https://en.wikipedia.org";

    /**
     * Removes special characters from the input string
     * @param url The string to be transformed
     * @return
     */
    public static String generateFileName(String url){
        String removePunctPattern = "[\\.\\/:]";
        return url.replaceAll(removePunctPattern,"");
    }

    /**
     * Goes through control file and adds files to cache based on links from those files
     * @throws IOException
     * @throws ParseException
     */
    public static void initialize() throws IOException, ParseException {
        File links = new File("links.txt");
        Scanner file = new Scanner(links);
        //This pattern excludes all files, special wikipedia pages, and disambiguation pages
        Pattern urlPattern = Pattern.compile("\\/wiki\\/((?!((Wikipedia:)|(File:))).)*(?<!(_\\(disambiguation\\)))");
        while (file.hasNextLine()){
            Document doc = (handleUrl(file.nextLine()));
            Elements linkElements = doc.select("a");
            int numLinks = 0;
            int i = 0;
            while (numLinks < 6 && i<linkElements.size()) {
                Element e = linkElements.get(i);
                i++;
                String link = (e.attr("href"));
                    Matcher m = urlPattern.matcher(link);
                    if(m.matches()){
                        numLinks++;
                        String flink = BASE_URI + (e.attr("href"));
                        handleUrl(flink);
                    }
            }
        }
    }

    /**
     * Takes in a url, creates and stores an html file from the url
     * Checks when files stored in cache were last updated, if later than web, redownload
     * @param url The url for the website to be downloaded
     * @return The jsoup Document created by the method
     * @throws IOException
     * @throws ParseException
     */
    public static Document handleUrl(String url) throws IOException, ParseException {
        String fileName = generateFileName(url);
        File dir = new File("cache");
        dir.mkdir();
        File f = new File("cache/" + fileName + ".html");
        if(!f.createNewFile()){
            //file exists, check when local file last modified
            long localMod = f.lastModified();
            // Check when website was last modified
            Connection.Response conn = Jsoup.connect(url).execute();
            String dString = conn.header("Last-Modified");
            // Pattern based on format of HTTP last modified header
            SimpleDateFormat format = new SimpleDateFormat("EEE',' dd MMM YYYY HH':'mm':'ss zz");
            Date date = format.parse(dString);
            long webMod = date.getTime();
            // If website modified after local, get page again
            if (webMod>localMod){
                f.delete();
                f.createNewFile();
                Document doc = Jsoup.connect(url).get();
                String text = doc.outerHtml();
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                writer.write(text);
                writer.close();
                return doc;
            }else{
                return Jsoup.parse(f,"UTF-8","");
            }
        }else{
            //file doesn't exist, download
            Document doc = Jsoup.connect(url).get();
            String text = doc.outerHtml();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(text);
            writer.close();
            return doc;
        }

    }

    /**
     * Takes in a jsoup document and returns a HashTable with words and word frequencies
     * @param doc The jsoup document to be analyzed
     * @return A HashTable
     */
    public static HashTable getWordsTable(Document doc){
        HashTable table = new HashTable(doc.title());
        String content = doc.text();
        String delimiters ="[ .!?@\\[\\]/()\\-—,\"\']";
        String[] words = content.split(delimiters);
        for (int i = 0; i <words.length ; i++) {
            table.add(words[i]);
        }
        return table;
    }

    /**
     * Transforms all cached files into HashTables
     * @return A list of HashTables representing word frequencies of all cached documents
     */
    public static HashTable[] getAllCachedTables(){
        File cacheFolder = new File("cache/");
        File[] files = cacheFolder.listFiles();
        HashTable[] out = new HashTable[files.length];
        try{
            for (int i = 0; i <files.length ; i++) {
                Document doc = Jsoup.parse(files[i],"UTF-8","");
                out[i] = getWordsTable(doc);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return out;
    }
}
