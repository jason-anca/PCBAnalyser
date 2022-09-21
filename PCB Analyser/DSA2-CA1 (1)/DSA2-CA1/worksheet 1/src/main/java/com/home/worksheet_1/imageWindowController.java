package com.home.worksheet_1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class imageWindowController{
@FXML
    ImageView imageView;
    static Color imageColour;
    static PixelReader pixelReader;
    @FXML
    private void initialize(){
        imageView.setImage(homeController.imageDisplay);
    }

    @FXML
    private void homePage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(myApp.class.getResource("homeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 750);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        stage.setTitle("Home View");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void setToGrayScale(ActionEvent actionEvent) {

        PixelReader pixelReader = imageView.getImage().getPixelReader();
        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
               Color imageColour = pixelReader.getColor(i, j);
               System.out.println(j);
               System.out.println(i);
                Color color = new Color((imageColour.getRed()*0.2162 + imageColour.getBlue()* 0.0722 + imageColour.getGreen()*0.7152)/3, (imageColour.getRed()*0.2162 + imageColour.getBlue()* 0.0722 + imageColour.getGreen()*0.7152)/3, (imageColour.getRed()*0.2162 + imageColour.getBlue()* 0.0722 + imageColour.getGreen()*0.7152)/3, 1.0);


                writableImage.getPixelWriter().setColor(i,j,color);
            }
        }
        imageView.setImage(writableImage);
    }
}
