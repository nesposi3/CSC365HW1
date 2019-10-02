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
        Label label = new Label("Enter URL:");
        btn.setText("Enter");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String url = urlField.getText();
                try{
                    Document doc = CacheUtils.handleUrl(url);
                    HashTable table = CacheUtils.getWordsTable(doc);
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
        root.getChildren().addAll(btn,urlField);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
}
