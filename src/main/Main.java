package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.Hashtable;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            CacheUtils.initialize();
        }catch (IOException e){

        }catch (ParseException e){

        }
        primaryStage.setTitle("Website Similarity");
        Button btn = new Button();
        TextField urlField = new TextField();
        Text closest = new Text();
        closest.setText(" Enter a wikipedia url to determine its similarity to the cached files!");
        btn.setText("Enter");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String url = urlField.getText();
                try{
                    HashTable[] cachedTables = CacheUtils.getAllCachedTables();
                    Document doc = CacheUtils.handleUrl(url);
                    HashTable table = CacheUtils.getWordsTable(doc);
                    String closestString = SimilarityUtils.similarity(table,cachedTables);
                    closest.setText("The closest website to " + table.getName() + " is: " + closestString);
                }catch (IOException ioe){
                    ioe.printStackTrace();

                }
                catch (ParseException p){
                    p.printStackTrace();
                }
            }
        });
        GridPane root = new GridPane();
        GridPane.setValignment(btn, VPos.CENTER);
        GridPane.setValignment(urlField, VPos.CENTER);
        GridPane.setConstraints(btn,2,0);
        GridPane.setConstraints(urlField,1,0);
        GridPane.setConstraints(closest,1,1);
        root.getChildren().addAll(btn,urlField,closest);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
}
