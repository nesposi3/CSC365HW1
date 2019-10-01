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
    public static void initialize() throws IOException, ParseException {
        File links = new File("links.txt");
        Scanner file = new Scanner(links);
        Pattern urlPattern = Pattern.compile("\\/wiki\\/.*");
        while (file.hasNextLine()){
            Document doc = (handleUrl(file.nextLine()));
            Elements linkElements = doc.select("a");
            for (Element e:
                    linkElements) {
                String link = (e.attr("href"));
                if(!link.contains("File:")){
                    Matcher m = urlPattern.matcher(link);
                    if(m.matches()){
                        String flink = BASE_URI + (e.attr("href"));
                        System.out.println(flink);
                    }
                }


            }
        }
    }
    public static Document handleUrl(String url) throws IOException, ParseException {
        int fileName = url.hashCode();
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
}
