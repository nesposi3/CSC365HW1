package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
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
        closest.setText("Enter a wikipedia url to determine its similarity to the cached files!");
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
                    closest.setText("The closest wikipedia article to " + table.getName() + " is: " + closestString);
                }catch (IOException ioe){
                    ioe.printStackTrace();

                }
                catch (ParseException p){
                    p.printStackTrace();
                }
            }
        });
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        GridPane.setMargin(closest,new Insets(5,5,5,5));
        closest.setFont(new Font(12));
        GridPane.setValignment(btn, VPos.CENTER);
        GridPane.setValignment(urlField, VPos.CENTER);
        GridPane.setHalignment(btn, HPos.CENTER);
        GridPane.setHalignment(urlField,HPos.CENTER);
        GridPane.setConstraints(btn,1,1);
        GridPane.setConstraints(urlField,0,1);
        GridPane.setConstraints(closest,0,0);
        root.getChildren().addAll(btn,urlField,closest);
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}
