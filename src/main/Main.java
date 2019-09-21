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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
public class Main extends Application {
    public static void handleUrl(String url) throws IOException, MalformedURLException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.outerHtml();
        int fileName = url.hashCode();
        File dir = new File("cache");
        dir.mkdir();
        File f = new File("cache/" + fileName + ".html");
        f.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        writer.write(text);
        writer.close();
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
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
