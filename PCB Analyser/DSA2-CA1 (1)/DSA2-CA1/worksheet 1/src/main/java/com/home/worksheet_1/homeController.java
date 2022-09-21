package com.home.worksheet_1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.security.auth.callback.LanguageCallback;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class homeController {
    //Global variables and constructors.
    static Image imageDisplay;
    static File file;
    double listenerHue;
    double listenerSaturation;
    double listenerBrightness;
    double listenerRed;
    double listenerGreen;
    double listenerBlue;
    double noiseNew;
    int width, height;
    int[] disArray;

    HashMap<Integer, ArrayList<Integer>> disMap;
    Color listenerColor;

    // TODO: 13/04/2022 Add tests

    @FXML
    private void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    ImageView imageView, bwView, sampledView, randomView;
    @FXML
    Label imageSizeLabel, imageResLabel, imageNameLabel, hexLabel, noiseVal;
    @FXML
    Slider noise;
    @FXML
    Polygon hexColour;
    @FXML
    WritableImage wrImage;

    @FXML
    private void initialize() {
        mouseListener();
        openFileAuto();
        sliderListener();
    }

    @FXML
    //Allows selection of files of specific extensions form the current PC
    public void openFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPG, PNG files (*.jpg, *.png)", "*.JPG", "*.JPEG", "*.gif", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilter);
        fileChooser.setTitle("Open File");
        file = fileChooser.showOpenDialog(new Stage());
        imageDisplay = new Image(file.toURI().toString(), imageView.getFitWidth(), imageView.getFitHeight(), true, true);
        imageView.setImage(imageDisplay);
        imageDetails(file);
    }

    //Same Method as above but as an initialization method.
    public void openFileAuto() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.JPEG", "*.gif", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilter);
        fileChooser.setTitle("Open File");
        file = fileChooser.showOpenDialog(new Stage());
        imageDisplay = new Image(file.toURI().toString(), imageView.getFitWidth(), imageView.getFitHeight(), true, true);
        imageView.setImage(imageDisplay);
        imageDetails(file);

    }

    //Black and white conversion using R,G,B, Hue, Saturation and Brightness
    //Red is hard to work with when using hues since it has a range that messes with the hue maths.
    //Since I'm getting hue ranges that goes from selected value to selected value + and - 26, Red ranges from 340 to 20 which really messes with the ranges but overall, hue is more accurate.
    //I used R,G,B values when it comes to the red range of the spectrum but hues for the rest of the colours.
    //Booleans in place to see if the selected colour range goes above 360 or below 0(for detecting red) and switch to using booleans when they are true.
    //In the same process, I'm adding any white pixels with the value of -1 to an array list and black pixels are added as a unique value using some maths.
    private void convertToBlackWhite() {
        PixelReader pixelReader = imageView.getImage().getPixelReader();
        width = (int) imageView.getImage().getWidth();
        height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        disArray = new int[width * height];
        boolean rollAbove;
        boolean rollBelow;

        for (int i = 0; i < width; i++) {//col
            for (int j = 0; j < height; j++) {//row
                disArray[(j * width) + i] = (j * width) + i; //PixelVal
                //Hue, Sat, Bright
                double selectedPixelHue = pixelReader.getColor(i, j).getHue();
                double selectedPixelSat = pixelReader.getColor(i, j).getSaturation();
                double selectedPixelBright = pixelReader.getColor(i, j).getBrightness();
                //RGB
                double selectedRed = pixelReader.getColor(i, j).getRed();
                double selectedGreen = pixelReader.getColor(i, j).getGreen();
                double selectedBlue = pixelReader.getColor(i, j).getBlue();
                //Hue vals
                double increasedPixelHue = listenerHue + 26;
                double decreasedPixelHue = listenerHue - 26;
                //Sat vals
                double increasedPixelSat = listenerSaturation + 0.26;
                double decreasedPixelSat = listenerSaturation - 0.26;
                //Bright vals
                double increasedPixelBright = listenerBrightness + 0.21;
                double decreasedPixelBright = listenerBrightness - 0.21;
                //Boolean
                rollAbove = increasedPixelHue > 360;
                rollBelow = decreasedPixelHue < 0;

                //If the boundaries are not exceeded, a range is used to make black/white
                if (!rollAbove && !rollBelow) {
                    if ((selectedPixelHue <= increasedPixelHue) && (selectedPixelHue >= decreasedPixelHue)) {
                        if ((selectedPixelSat <= increasedPixelSat) && (selectedPixelSat >= decreasedPixelSat)) {
                            if ((selectedPixelBright >= decreasedPixelBright) && (selectedPixelBright <= increasedPixelBright)) {
                                writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                            } else {
                                writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                                disArray[(j * width) + i] = -1;
                            }
                        } else {
                            writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                            disArray[(j * width) + i] = -1;
                        }
                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                    //If the decreased hue is below 0...
                } else if (rollBelow) {
                    if ((selectedBlue <= 0.2) && (selectedRed >= 0.8) && (selectedGreen <= 0.2)) {
                        writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                    //If the hue is above 360(hue ranges from 360 to 0)...
                } else if (rollAbove) {
                    if ((selectedBlue <= 0.2) && (selectedRed >= 0.8) && (selectedGreen <= 0.2)) {
                        writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                } else if ((selectedBlue >= 0.8) && (selectedRed >= 0.8) && (selectedGreen <= 0.8)) {
                    writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                } else {
                    writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                    disArray[(j * width) + i] = -1;
                }
            }
        }
        wrImage = writableImage;
        bwView.setImage(writableImage);
    }

    private void blackWhiteConversion() {
        PixelReader pixelReader = imageView.getImage().getPixelReader();
        width = (int) imageView.getImage().getWidth();
        height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        disArray = new int[width * height];
        boolean rollAbove;
        boolean rollBelow;

        for (int i = 0; i < width; i++) {//col
            for (int j = 0; j < height; j++) {//row
                disArray[(j * width) + i] = (j * width) + i;
                double pixelHue = pixelReader.getColor(i, j).getHue();
                double pixelSat = pixelReader.getColor(i, j).getSaturation();
                double pixelBright = pixelReader.getColor(i, j).getBrightness();

                double pixelRed = pixelReader.getColor(i, j).getRed();
                double pixelGreen = pixelReader.getColor(i, j).getGreen();
                double pixelBlue = pixelReader.getColor(i, j).getBlue();

                double selectedHue = listenerHue;
                double increasedPixelHue = listenerHue + 25;
                double decreasedPixelHue = listenerHue - 25;

                double increasedPixelSat = listenerSaturation + 0.25;
                double decreasedPixelSat = listenerSaturation - 0.25;

                double increasedPixelBright = listenerBrightness + 0.20;
                double decreasedPixelBright = listenerBrightness - 0.20;

                rollAbove = increasedPixelHue < 360;
                rollBelow = decreasedPixelHue > 0;

                if (!rollAbove && !rollBelow) {
                    if ((pixelHue <= increasedPixelHue) && (pixelHue >= decreasedPixelHue)) {
                        if ((pixelSat <= increasedPixelSat) && (pixelSat >= decreasedPixelSat)) {
                            if ((pixelBright >= decreasedPixelBright) && (pixelBright <= increasedPixelBright)) {
                                writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                            } else {
                                writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                                disArray[(j * width) + i] = -1;
                            }
                        } else {
                            writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                            disArray[(j * width) + i] = -1;
                        }

                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                } else if (rollBelow) {
                    selectedHue = 360 - selectedHue;
                    if ((pixelHue <= increasedPixelHue) && (selectedHue >= decreasedPixelHue)) {
                        if ((pixelBright <= increasedPixelBright) && (pixelBright >= decreasedPixelBright)) {
                            if ((pixelSat <= increasedPixelSat) && (pixelSat >= decreasedPixelSat)) {
                                writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                            } else {
                                writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                                disArray[(j * width) + i] = -1;
                            }
                        } else {
                            writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                            disArray[(j * width) + i] = -1;
                        }
                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                } else if (rollAbove) {
                    selectedHue = 360 - selectedHue;
                    if ((selectedHue <= increasedPixelHue) && (pixelHue >= decreasedPixelHue)) {
                        if ((pixelBright <= increasedPixelBright) && (pixelBright >= decreasedPixelBright)) {
                            if ((pixelSat <= increasedPixelSat) && (pixelSat >= decreasedPixelSat)) {
                                writableImage.getPixelWriter().setColor(i, j, Color.BLACK);
                            } else {
                                writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                                disArray[(j * width) + i] = -1;
                            }
                        } else {
                            writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                            disArray[(j * width) + i] = -1;
                        }
                    } else {
                        writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                        disArray[(j * width) + i] = -1;
                    }
                }
            }
        }
        wrImage = writableImage;
        bwView.setImage(writableImage);
    }

    //Basic information about the displayed image such as image resolution, file size, file name.
    private void imageDetails(File file) {
        float imageSize = file.length();
        imageSize = (imageSize / 1024) / 1024;
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();
        imageNameLabel.setText("Image name: " + file.getName());
        imageSizeLabel.setText("Image size: " + imageSize + " MB");
        imageResLabel.setText("Image resolution: " + imageWidth + " x " + imageHeight);

    }

    //Mouse listener using lambda expression. Whenever you click within the bounds of the image view, it does various things such as
    //get the colour, hue, brightness, saturation and R,G,B values of the selected x and y coordinate
    //Also displays that information in the console for easy debugging and helped me to do maths with those values displayed such as creating the tooltips.
    //This method also calls the black and white conversion method, grouping method for disjoint sets, does automatic noise reduction after conversion just for ease of testing, and also creates the rectangles on the selected colour and it's range.
    //I've added a rectangle and a label that displays the current pixel information after clicking, again for debugging, but also it shows the user what they clicked since it might be difficult to tell when working with a PCB.
    private void mouseListener() {
        imageView.setOnMouseClicked(e -> {
            PixelReader pixelReader = imageView.getImage().getPixelReader();
            listenerColor = pixelReader.getColor((int) e.getX(), (int) e.getY());
            listenerBrightness = listenerColor.getBrightness();
            listenerHue = listenerColor.getHue();
            listenerSaturation = listenerColor.getSaturation();
            listenerRed = listenerColor.getRed();
            listenerGreen = listenerColor.getGreen();
            listenerBlue = listenerColor.getBlue();
            System.out.println("\nPixel information of: " + "\nMouse X: " + (int) e.getX() + "\nMouse Y: " + (int) e.getY());
            System.out.println("================================================");
            System.out.println("Hue: " + listenerHue + "\nBrightness: " + listenerBrightness + "\nSaturation: " + listenerSaturation);
            System.out.println("Hex: " + listenerColor);
            System.out.println("R: " + listenerRed + "\n" + "G: " + listenerGreen + "\n" + "B: " + listenerBlue + "\n");
            convertToBlackWhite();
            group();
            noiseReduction();
            createRectangles();
            randomColour();
            hexColour.setFill(listenerColor);
            hexLabel.setText(listenerColor.toString());
        });
    }

    //Union and find that was provided in the notes
    public void union(int[] a, int p, int q) {
        a[find(a, q)] = find(a, p);
    }

    public int find(int[] a, int root) {
        if (a[root] < 0) return a[root];
        if (a[root] == root) {
            return root;
        } else {
            return find(a, a[root]);
        }
    }

    public void group() {
        //Create hashmap.
        //Goes through the length of the array and checks if the pixel to right and below and adds them to the array.
        disMap = new HashMap<>();
        for (int i = 0; i < disArray.length; i++) {
            if (disArray[i] >= 0) {
                if (((i + 1) % width != 0) && (disArray[i + 1] != -1)) {
                    union(disArray, i, i + 1);
                }

                if (((i + width) < disArray.length) && (disArray[i + width] != -1)) {
                    union(disArray, i, i + width);
                }
            }
        }
        //Goes through the length of the array and if an element is not -1, then the actualRoot is found.
        //If the hashmap key contains the root then get and add it.
        //Otherwise, a new list is made and the values are added to the map.
        for (int i = 0; i < disArray.length; i++) {
            if (disArray[i] != -1) {
                int actualRoot = find(disArray, disArray[i]);
                if (disMap.containsKey(actualRoot)) {
                    ArrayList<Integer> disVals = disMap.get(actualRoot);
                    disVals.add(i);

                } else {
                    ArrayList<Integer> disVals = new ArrayList<>();
                    disVals.add(i);
                    disMap.put(actualRoot, disVals);
                }

            }
        }
        System.out.println("Number of disjoint sets: " + disMap.size());

    }

// TODO: 05/04/2022 Fix the black white conversion to be more accurate and less smelly : Not a priority

    //Noise reduction for false positives in the black and white conversion
    //Goes through the sets of the hash map and removes the set that is less than or equal to the noise reduction specified by the program or user.
    //In the set's place, white pixels are added.
    private void noiseReduction() {
        width = (int) imageView.getImage().getWidth();
        height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = wrImage;
        noiseNew = noise.getValue();
        for (Integer theKey : disMap.keySet())
            if (disMap.get(theKey).size() <= noiseNew) {
                for (int j = 0; j < disMap.get(theKey).size(); j++) {
                    int col = disMap.get(theKey).get(j) % width; //Uses remainder to find column
                    int row = disMap.get(theKey).get(j) / width; //Divides to find row
                    writableImage.getPixelWriter().setColor(col, row, new Color(1, 1, 1, 1));
                }

            }

        bwView.setImage(writableImage);
        disMap.keySet().removeIf(row -> disMap.get(row).size() <= noiseNew);
        System.out.println("Noise reduction: " + noiseNew);
        System.out.println("Number of disjoint sets after noise reduction: " + disMap.size());
    }


    //Listener for the slider so that I can update a label with the current value of the slider since a user can't tell otherwise
    private void sliderListener() { //https://stackoverflow.com/questions/40593284/how-can-i-define-a-method-for-slider-change-in-controller-of-a-javafx-program
        noise.valueProperty().addListener((observable, noiseOld, noiseNew) -> {
            noiseVal.setText(Integer.toString(noiseNew.intValue()));
        });
    }

    //Creates rectangles around the bounds of any set on the image view
    //Gets the starting and ending x and y values so that a rectangle of a specific width and height can be created around the sets.
    //If statements allow the creation of rectangles to show specific tooltips for components.
    //Tooltips display information such a component number and pixel size.
    //Example in the CA pdf uses red rectangles but I made them cyan since I find it hard to see the red on the green pcb.
    //Added text to the rectangles to display the sizes of each component without the need to hover over the component.
    //Made yellow for so it's easier to see on different components
    private void createRectangles() {
        ((Pane) imageView.getParent()).getChildren().removeIf(x -> x instanceof Rectangle);
        ((Pane) imageView.getParent()).getChildren().removeIf(x -> x instanceof Text);
        ArrayList<Integer> rectangleArray = new ArrayList<>();
        int componentNo = 0;
        rectangleArray = componentSize();

        for (int root : rectangleArray){
            componentNo++;
            ArrayList<Integer> values = disMap.get(root);
            int startX, startY, endX, endY;
            startX = startY = Integer.MAX_VALUE;
            endX = endY = Integer.MIN_VALUE;

            for (int element : values) {
                int col = element % width;
                int row = element / width;
                if (startX > col) {
                    startX = col;
                }
                if (startY > row) {
                    startY = row;
                }
                if (endX < col) {
                    endX = col;
                }
                if (endY < row) {
                    endY = row;
                }

            }
            Rectangle rectangle = new Rectangle(startX, startY, (endX - startX), (endY - startY));
            double rectangleX = rectangle.getX();
            double rectangleY = rectangle.getY() + (endY - startY);

            Font font = Font.font("Ubuntu", FontWeight.BOLD, FontPosture.REGULAR, 15);
            Text text = new Text();
            text.setLayoutX(rectangleX);
            text.setLayoutY(rectangleY);
            text.setFont(font);
            text.setStroke(Color.YELLOW);
            text.setStrokeWidth(1);

            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.CYAN);
            rectangle.setStrokeWidth(2);
            rectangle.setTranslateX(imageView.getLayoutX());
            rectangle.setTranslateY(imageView.getLayoutY());


            double rectangleSize = (int) rectangle.getHeight()*rectangle.getWidth();
            int setSize = disMap.get(root).size();
            if (listenerHue >= 25 && listenerHue <= 50) {
                if (listenerBrightness <= 0.85 && listenerBrightness >= 0.50) {
                    if (listenerSaturation <= 0.75 && listenerSaturation >= 0.25) {
                        Tooltip tooltip = new Tooltip("Resistor" + "\nEstimated Pixel Size: " + rectangleSize +  "\nSet size: " + setSize +  "\nComponent number: " + componentNo );
                        Tooltip.install(rectangle, tooltip);
                        text.setText(String.valueOf(componentNo));
                        ((Pane) imageView.getParent()).getChildren().add(text);
                        ((Pane) imageView.getParent()).getChildren().add(rectangle);

                        sampledColour();
                    }
                }

            } else if (listenerHue >= 200 && listenerHue <= 250) {
                if (listenerBrightness <= 0.35 && listenerBrightness >= 0.2) {
                    if (listenerSaturation <= 0.3 && listenerSaturation >= 0.2) {
                        if (disMap.get(root).size() >= 130) {
                            Tooltip tooltip = new Tooltip("Integrated Circuit Board" + "\nEstimated Pixel Size: " + rectangleSize + "\nSet Size: " + setSize +  "\nComponent number: " + componentNo);
                            Tooltip.install(rectangle, tooltip);
                            text.setText(String.valueOf(componentNo));
                            ((Pane) imageView.getParent()).getChildren().add(text);
                            ((Pane) imageView.getParent()).getChildren().add(rectangle);
                            sampledColour();
                        }
                    }
                }
            }
        }

    }
    //Create an arraylist, go through the hash map, find the largest set size.
    //Put the root into the first element of the array. Go through the hash map again,
    //Find the largest that's smaller than the previous set.
    //if root is not the same as previous root
    private ArrayList<Integer> componentSize(){
        ArrayList<Integer> stSize = new ArrayList<>();
        int int1 = Integer.MAX_VALUE;
        int hashSize = disMap.size();

        for (int i = 0; i < disMap.size(); i++){
            int int2 = 0;
            int root1 = 0;
            for (Integer root : disMap.keySet()){
                if (disMap.get(root).size() > int2 && disMap.get(root).size() <= int1 && !stSize.contains(root)){
                    int2 = disMap.get(root).size();
                    root1 = root;
                }
            }
            stSize.add(root1);
            int1 = int2;
        }
        return stSize;

    }

    //Uses the already black and white converted image to replace black pixels with the selected colour from the mouse listener
    private void sampledColour() {
        PixelReader pixelReader = bwView.getImage().getPixelReader();
        width = (int) imageView.getImage().getWidth();
        height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double red = pixelReader.getColor(i, j).getRed();
                double blue = pixelReader.getColor(i, j).getBlue();
                double green = pixelReader.getColor(i, j).getGreen();
                if ((green < 1) && (blue < 1) && (red < 1)) {
                    writableImage.getPixelWriter().setColor(i, j, listenerColor);
                } else {
                    writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                }

            }
            sampledView.setImage(writableImage);
        }
    }

    //Same code as sampled colour but changes colour displayed to a random RGB value instead of the sampled colour.
    private void randomColour() {
        PixelReader pixelReader = bwView.getImage().getPixelReader();
        width = (int) imageView.getImage().getWidth();
        height = (int) imageView.getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        Color randomColor = new Color(r, g, b, 1);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double red = pixelReader.getColor(i, j).getRed();
                double blue = pixelReader.getColor(i, j).getBlue();
                double green = pixelReader.getColor(i, j).getGreen();
                if ((green < 1) && (blue < 1) && (red < 1)) {
                    writableImage.getPixelWriter().setColor(i, j, randomColor);
                } else {
                    writableImage.getPixelWriter().setColor(i, j, Color.WHITE);
                }

            }
            randomView.setImage(writableImage);
        }
    }



    @FXML
    private void removeRectangles(ActionEvent actionEvent) {
        ((Pane) imageView.getParent()).getChildren().removeIf(x -> x instanceof Rectangle);
    }

    //Action event methods for calling manually since some are done automatically by the mouse listener.
    //Important for debugging and for the user especially since you can't click through rectangle objects.
    @FXML
    private void sampleColourAV(ActionEvent actionEvent) {
        sampledColour();
    }
    @FXML
    //Action event version for the noise reduction method.
    private void noiseReductionAV(ActionEvent actionEvent) {
        noiseReduction();
    }

    @FXML
    private void createRectanglesAV(ActionEvent actionEvent){
        createRectangles();
    }

}