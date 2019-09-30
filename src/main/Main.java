package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jsoup.*;
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
import main.HashTable;
import main.HashTable.Node;

public class Main extends Application {
    public final static String BASE_URI = "https://en.wikipedia.org";
    public final static Pattern URL_PATTERN = Pattern.compile("\\/wiki\\/.*");
    public static void initialize() throws IOException, MalformedURLException, ParseException {
        File links = new File("links.txt");
        Scanner file = new Scanner(links);
        while (file.hasNextLine()){
            Document doc = (handleUrl(file.nextLine()));
            Elements linkElements = doc.select("a");
            for (Element e:
                 linkElements) {
                String link = (e.attr("href"));
                if(!link.contains("File:")){
                    Matcher m = URL_PATTERN.matcher(link);
                    if(m.matches()){
                        String flink = BASE_URI + (e.attr("href"));
                        System.out.println(flink);
                    }
                }


            }
        }
    }
    public static Document handleUrl(String url) throws IOException, MalformedURLException, ParseException {
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
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        Button initialize = new Button("Initialize");
        TextField urlField = new TextField();
        Label label = new Label("Enter URL:");
        btn.setText("Enter");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String url = urlField.getText();
                try{
                    handleUrl(url);
                }catch (IOException ioe){
                    ioe.printStackTrace();

                }
                catch (ParseException p){
                    p.printStackTrace();
                }
            }
        });
        initialize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    initialize();
                }catch (IOException ioe){
                    ioe.printStackTrace();

                }
                catch (ParseException p){
                    p.printStackTrace();
                }
            }
        });
        GridPane root = new GridPane();
        GridPane.setConstraints(btn,1,0);
        GridPane.setConstraints(urlField,0,0);
        GridPane.setConstraints(initialize,0,2);
        root.getChildren().addAll(btn,urlField,initialize);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
}
