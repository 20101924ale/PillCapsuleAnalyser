package com.example.pillanalyser;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SecondSceneController {

    @FXML
    ImageView imgView;
    Stage stage;
    @FXML
    ImageView blackAndWhiteImage;
    @FXML
    Label fileName;
    @FXML
    Label fileSize;

    @FXML
    Button colorDis;
    LinkedList<Double> rectangles = new LinkedList<>();

    @FXML
    Label totalPills;
    @FXML
    Label welcomeMessage;

    public WritableImage ii;
    private Color sampleColor;

    private ImageView imageView;

    public void open(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image file");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            file.toURI();
            //the image selected is displayed in all the image views once it is selected
            Image image = new Image(file.toURI().toString(), imgView.getFitWidth(), imgView.getFitHeight(), false, true);
            imgView.setImage(image);

            blackAndWhiteImage.setImage(image);

            // Display the file name

            fileName.setText(file.getName());

            // Display its size in kilobytes

            fileSize.setText(file.length() + "KB");
        }
    }

    public int[] blackAndWhite(ImageView imgView, int threshold) {
        //Assign width and height values from imageView
        int width = (int) imgView.getImage().getWidth();
        int height = (int) imgView.getImage().getHeight();
        //create a new writable image with its width and height the same as the imageview
        ii = new WritableImage(width, height);

        int[] imageArray = new int[height * width];

        // convert to black and white
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = imgView.getImage().getPixelReader().getArgb(x, y);

                int a = (p >> 24) & 255;
                int r = (p >> 16) & 255;
                int g = (p >> 8) & 255;
                int b = p & 255;


                // calculate average
                int avg = (r + g + b) / 3;

                // set pixel to black or white depending on intensity value
                int bw;
                if (avg < threshold) {
                    bw = 0; // black
                    // store black or white value
                    imageArray[y * width + x] = -1;
                } else {
                    bw = 255; // white
                    // store black or white value
                    imageArray[y * width + x] = y * width + x;
                }

                // replace RGB value with black or white
                p = (a << 24) | (bw << 16) | (bw << 8) | bw;


                ii.getPixelWriter().setArgb(x, y, p);

                //here...
                Color col = imgView.getImage().getPixelReader().getColor(x, y);
                ii.getPixelWriter().setColor(x, y,
                        Math.abs(col.getHue() - sampleColor.getHue()) < 10 ? Color.WHITE : Color.BLACK);

            }
        }
        //Once the image is selected after the black and white conversion
        //preform the analysis on it
        imgView.setImage(ii);
        imageAnalysis();
        printImageArray();
        return imageArray;
    }
    @FXML
    public void handleImageClick(MouseEvent event) {
        // Get the coordinates of the mouse click
        double x = event.getX();
        double y = event.getY();


        //**************************************
        sampleColor = imgView.getImage().getPixelReader().getColor((int) x, (int) y);
        blackAndWhite(blackAndWhiteImage, 128);
//**************************************

        // Calculate the pixel position in the image
        int pixelX = (int) (x * imgView.getImage().getWidth() / imgView.getFitWidth());
        int pixelY = (int) (y * imgView.getImage().getHeight() / imgView.getFitHeight());

        // Find the root value of the clicked pill
        int rootValue = findPillRoots(imageArray, pixelY * (int) imgView.getImage().getWidth() + pixelX);

        // Get the bounds of the clicked pill
        int minX = (int) imgView.getImage().getWidth();
        int minY = (int) imgView.getImage().getHeight();
        int maxX = 0;
        int maxY = 0;

        for (int j = 0; j < imageArray.length; j++) {
            if (imageArray[j] != -1) {
                int root = findPillRoots(imageArray, j);

                if (rootValue == root) {
                    int rootX = j % (int) imgView.getImage().getWidth();
                    int rootY = j / (int) imgView.getImage().getWidth();

                    if (rootX < minX) minX = rootX;
                    if (rootY < minY) minY = rootY;
                    if (rootX > maxX) maxX = rootX;
                    if (rootY > maxY) maxY = rootY;
                }
            }
            totalPills();
        }

        // Draw the rectangle based on the bounds of the clicked pill
        drawRectangles(minX, minY, maxX, maxY);

    }

    public void totalPills(){
        int totalPillsCount = disjointSetValues.size(); // Assuming disjointSetValues holds the total number of pills
        totalPills.setText("Total number of pills: " + totalPillsCount);
        System.out.println( "The total number of pills " + disjointSetValues.size());
    }

    // finds the root of each of the pixels
    public int find(int[] imageArray, int data) {
        if (imageArray[data] == data) {
            return data;
        } else {
            return find(imageArray, imageArray[data]);
        }
    }

    public void clearImages(ActionEvent event) {
        clearImageView();
    }
    private Map<String, Pill> pillMap = new HashMap<>();
    private Map<String, Integer> pillCount = new HashMap<>();
    public void setTotals(Label totalPills, ListView<String> sameTypeList) {
        pillCount.clear();
        int pills = 0;

        for (Pill p : pillMap.values()) {
            String name = p.getName();
            pillCount.put(name, pillCount.getOrDefault(name, 0) + 1);
            totalPills.setText("TOTAL PILLS: " + ++pills);
        }

        if (sameTypeList != null) {
            sameTypeList.getItems().clear();
            for (Map.Entry<String, Integer> entry : pillCount.entrySet()) {
                sameTypeList.getItems().add(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public void clearImageView() {
        imgView.setImage(null);
        blackAndWhiteImage.setImage(null);
        fileName.setText("");
        fileSize.setText("");
        // Remove rectangles from the parent Pane
        ((Pane) imgView.getParent()).getChildren().removeIf(node -> node instanceof Rectangle);
    }



    int[] imageArray;

//    public void imageAnalysis() {
//        // create array the size of the width multiplied by the height
////        imageArray = new int[(int) blackAndWhiteImage.getImage().getHeight() * (int) blackAndWhiteImage.getImage().getWidth()];
//
//        // go through pixel by pixel, if black {-1}, if white {row*width+column}
//        // iterate through the image pixel by pixel using a PixelReader object
//        PixelReader pixelReader = blackAndWhiteImage.getImage().getPixelReader();
//        for (int i = 0; i < blackAndWhiteImage.getImage().getHeight(); i++) {
//            for (int j = 0; j < blackAndWhiteImage.getImage().getWidth(); j++) {
//                // get the color of the current pixel
//                Color getColor = pixelReader.getColor(j, i);
//                // if the pixel is white, set the corresponding index in imageArray to its position
//                if (getColor.equals(Color.WHITE)) {
//                    imageArray[(i * (int) blackAndWhiteImage.getImage().getWidth()) + j] = (i * (int) blackAndWhiteImage.getImage().getWidth()) + j;
//                } else {
//                    // if the pixel is black, set the corresponding index in imageArray to -1
//                    imageArray[(i * (int) blackAndWhiteImage.getImage().getWidth()) + j] = -1;
//                }
//            }
//        }
//        // perform union operation to group adjacent white pixels into connected components
//        int right;
//        int down;
//        for (int data = 0; data < imageArray.length; data++) {
//            if ((data + 1) < imageArray.length) {
//                right = data + 1;
//            } else {
//                right = imageArray.length - 1; // goes to the very end +1 then does -1 once you run out of pixels
//            }
//
//            // checks if down is possible
//            if ((data + (int) blackAndWhiteImage.getImage().getWidth()) < imageArray.length) {
//                down = data + (int) blackAndWhiteImage.getImage().getWidth();
//            } else {
//                down = imageArray.length - 1; // goes to the very end
//            }
//
//            // perform union operation on adjacent white pixels
//            if (imageArray[data] == -1) {
//                continue;
//            }
//            if ((data + 1) % blackAndWhiteImage.getImage().getWidth() != 0) {
//                if (imageArray[right] != -1) {
//                    union(imageArray, data, right);
//                }
//            }
//            if ((data + 1) % blackAndWhiteImage.getImage().getWidth() != 1) {
//                if (imageArray[down] != -1) {
//                    union(imageArray, data, down);
//                }
//            }
//        }
//
//        // obtain the positions of the center pixels of the connected components
//        getSquarePositions(imageArray);

    public void imageAnalysis() {
        imageArray = new int[(int) blackAndWhiteImage.getImage().getHeight() * (int) blackAndWhiteImage.getImage().getWidth()];
        PixelReader pixelReader = blackAndWhiteImage.getImage().getPixelReader();
        for (int i = 0; i < blackAndWhiteImage.getImage().getHeight(); i++) {
            for (int j = 0; j < blackAndWhiteImage.getImage().getWidth(); j++) {
                Color getColour = pixelReader.getColor(j, i);
                if (getColour.equals(Color.WHITE)) {
                    imageArray[(i * (int) blackAndWhiteImage.getImage().getWidth()) + j] = (i * (int) blackAndWhiteImage.getImage().getWidth()) + j;
                } else {
                    imageArray[(i * (int) blackAndWhiteImage.getImage().getWidth()) + j] = -1;
                }
            }
        }
        int checkRight;
        int checkDown;
        for (int data = 0; data < imageArray.length; data++) {
            if ((data + 1) < imageArray.length) {
                checkRight = data + 1;
            } else {
                checkRight = imageArray.length - 1;
            }
            if ((data + (int) blackAndWhiteImage.getImage().getWidth()) < imageArray.length) {
                checkDown = data + (int) blackAndWhiteImage.getImage().getWidth();
            } else {
                checkDown = imageArray.length - 1;
            }
            if (imageArray[data] == -1) {
                continue;
            }
            if ((data + 1) % blackAndWhiteImage.getImage().getWidth() != 0) {
                if (imageArray[checkRight] != -1) {
                    union(imageArray, data, checkRight);
                }
            }
            if ((data + 1) % blackAndWhiteImage.getImage().getWidth() != 1) {
                if (imageArray[checkDown] != -1) {
                    union(imageArray, data, checkDown);
                }
            }
        }
        getRectanglePositions(imageArray);
    }


    public void getRectanglePositions(int[] imageArray) {
        ((AnchorPane) imgView.getParent()).getChildren().removeIf(c -> c instanceof Rectangle);

        for (int i = 0; i < imageArray.length; i++) {
            if (imageArray[i] != -1 && !disjointSetValues.contains(findPillRoots(imageArray, i))) {
                disjointSetValues.add(findPillRoots(imageArray, i));
            }
        }

        // Loop through the values in disjointSetValues
        for (int i = 0; i < disjointSetValues.size(); i++) {
            int rootValue = disjointSetValues.get(i);

            int minX = (int) blackAndWhiteImage.getImage().getWidth();
            int minY = (int) blackAndWhiteImage.getImage().getHeight();
            int maxX = 0;
            int maxY = 0;

            // Loop through the imageArray and find the minimum and maximum x and y values for each element that
            // is equal to the current rootValue
            for (int j = 0; j < imageArray.length; j++) {
                if (imageArray[j] != -1) {
                    int root = findPillRoots(imageArray, j);

                    if (rootValue == root) {
                        int rootX = j % (int) blackAndWhiteImage.getImage().getWidth();
                        int rootY = j / (int) blackAndWhiteImage.getImage().getWidth();

                        if (rootX < minX) minX = rootX;
                        if (rootY < minY) minY = rootY;
                        if (rootX > maxX) maxX = rootX;
                        if (rootY > maxY) maxY = rootY;
                    }
                }
            }
            // Noise Reduction
            // If the area of the component is greater than the noiseReduction value, call drawRectangles
            double area = (maxX - minX) * (maxY - minY);
            if (area > noiseReduction) {
                drawRectangles(minX, minY, maxX, maxY);
            }
        }
    }


    public void drawRectangles(double minX, double minY, double maxX, double maxY) {
        // This lays out the bounds of the original image view so the rectangles aren't drawn out of its bounds
        Bounds bounds = imgView.getLayoutBounds();
        double xScale = bounds.getWidth() / imgView.getImage().getWidth();
        double yScale = bounds.getHeight() / imgView.getImage().getHeight();

        minX *= xScale;
        maxX *= xScale;
        minY *= yScale;
        maxY *= yScale;

        // Create a new Rectangle object
        Rectangle rectangle = new Rectangle();

        // Set the position and size of the rectangle
        rectangle.setX(minX);
        rectangle.setY(minY);
        rectangle.setWidth(maxX - minX);
        rectangle.setHeight(maxY - minY);

        // Set the stroke color and width of the rectangle
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.RED);
        rectangle.setStrokeWidth(1);

        // Set the layout position of the rectangle to match that of the original image view
        rectangle.setTranslateX(imgView.getLayoutX());
        rectangle.setTranslateY(imgView.getLayoutY());

        // Add the rectangle to the parent Pane of the original image view
        ((Pane) imgView.getParent()).getChildren().add(rectangle);

        // Calculates the area of the rectangle
        double area = (maxX - minX) * (maxY - minY);

        // Add the area to the list of areas
        rectangles.add(area);

        // Sort the list of rectangle areas in ascending order
        Collections.sort(rectangles);

        // Attach a tooltip to the rectangle
        Tooltip.install(rectangle, new Tooltip("Rectangle Number: " + rectangles.size() + "\nArea (pixel units): " + area));
    }




    // Define a method to print the contents of an array of Image objects to the console
    public void printImageArray(){
        // Use the Arrays.toString() method to convert the imageArray to a string, and print the resulting string to the console
        System.out.println(Arrays.toString(imageArray));
    }

    // Define a LinkedList object to store the areas of circles that will be drawn
    LinkedList<Double> squares = new LinkedList<>();

    // Define a method to draw square over an area defined by minX, minY, maxX, maxY parameters
    public void drawSquares(double minX, double minY, double maxX, double maxY) {
        Bounds bounds = blackAndWhiteImage.getLayoutBounds();
        double xScale = bounds.getWidth() / blackAndWhiteImage.getImage().getWidth();
        double yScale = bounds.getHeight() / blackAndWhiteImage.getImage().getHeight();

        minX *= xScale;
        maxX *= xScale;
        minY *= yScale;
        maxY *= yScale;

        Rectangle square = new Rectangle();

        square.setX((minX + maxX) / 2);
        square.setY((minY + maxY) / 2);

        square.setHeight(Math.max(maxX - minX, maxY - minY) / 2);

        square.setFill(Color.TRANSPARENT);
        square.setStroke(Color.BLUE);
        square.setStrokeWidth(3);

        square.setTranslateX(blackAndWhiteImage.getLayoutX());
        square.setTranslateY(blackAndWhiteImage.getLayoutY());

        ((Pane)blackAndWhiteImage.getParent()).getChildren().add(square);

        double area = Math.PI * square.getWidth() * square.getHeight();
        squares.add(area);

        Collections.sort(squares);

        for (int i = 0; i < squares.size(); i++) {
            System.out.println(squares.get(i));
            Tooltip.install(square, new Tooltip("Pill Number: " + (i + 1) + "\nEstimated Size (pixel units): " + squares.get(i)));
        }
    }

    int noiseReduction = 200;
    List<Integer> disjointSetValues = new ArrayList<>();

    public void getSquarePositions(int[] imageArray) {
        ((AnchorPane)blackAndWhiteImage.getParent()).getChildren().removeIf(c->c instanceof Rectangle);

        for(int i = 0; i < imageArray.length; i++){
            if(imageArray[i] != -1 && !disjointSetValues.contains(find(imageArray, i))){
                disjointSetValues.add(find(imageArray, i));
            }
        }

        for(int i = 0; i < disjointSetValues.size(); i++) {
            int rootValue = disjointSetValues.get(i);
            int minX = (int) blackAndWhiteImage.getImage().getWidth();
            int minY = (int) blackAndWhiteImage.getImage().getHeight();
            int maxX = 0;
            int maxY = 0;

            for (int j = 0; j < imageArray.length; j++) {
                if (imageArray[j] != -1) {
                    int root = find(imageArray, j);

                    if (rootValue == root) {
                        int rootX = j % (int) blackAndWhiteImage.getImage().getWidth();
                        int rootY = j / (int) blackAndWhiteImage.getImage().getWidth();

                        if (rootX < minX) minX = rootX;
                        if (rootY < minY) minY = rootY;
                        if (rootX > maxX) maxX = rootX;
                        if (rootY > maxY) maxY = rootY;
                    }
                }
            }

            if ((((maxX - minX)*(maxY - minY)) > noiseReduction)) {
                drawSquares(minX, minY, maxX, maxY);
            }
        }
    }

    public int findPillRoots(int[] imageArray, int data){
        if(imageArray[data]==data){
            return data;
        }
        else{
            return findPillRoots(imageArray,imageArray[data]);
        }
    }

    public void union(int[] imageArray, int a, int b){
        imageArray[findPillRoots(imageArray,b)]=findPillRoots(imageArray,a);
    }




    @FXML
    public void colorDisjointSets(ActionEvent event) {
        PixelWriter pixelWriter = ii.getPixelWriter();
        int imageWidth = (int) blackAndWhiteImage.getImage().getWidth();
        int imageHeight = (int) blackAndWhiteImage.getImage().getHeight();

        System.out.println("Image width: " + imageWidth + ", Image height: " + imageHeight);

        for (int j = 0; j < imageHeight; j++) {
            for (int i = 0; i < imageWidth; i++) {
                int position = j * imageWidth + i;

                // Ensure that 'position' remains within the bounds of 'imageArray'
                if (position >= 0 && position < imageArray.length && imageArray[position] != -1) {
                    int root = find(imageArray, position);
                    Random rgb = new Random(root);
                    double r = rgb.nextDouble();
                    double g = rgb.nextDouble();
                    double b = rgb.nextDouble();

                    Color randomColor = new Color(r, g, b, 1.0);
                    System.out.println("Setting color at position (" + i + ", " + j + ")");
                    pixelWriter.setColor(i, j, randomColor);
                }
            }
        }
    }


    //// HSV SLIDERS

    @FXML
    Slider hueSlider;
    @FXML
    Slider hueSlider1;

    @FXML
    Slider brightnessSlider;
    @FXML
    Slider brightnessSlider1;
    @FXML
    Slider saturationSlider;
    @FXML
    Slider saturationSlider1;
    @FXML
    Slider noiseSlider;

    @FXML
    Button resetButton;

    ColorAdjust colorAdjust = new ColorAdjust();

    public void initialize() {
        hueSlider.setOnMouseDragged(event -> adjustHue());
        hueSlider.setValue(48);
        saturationSlider.setValue(48);
        saturationSlider.setOnMouseDragged(event -> adjustSaturation());
    }

    @FXML
    private void adjustSaturation() {
        if (blackAndWhiteImage.getImage() != null) {
            double saturationAdjustment = saturationSlider.getValue();

            Image originalImage = blackAndWhiteImage.getImage();
            PixelReader originalReader = originalImage.getPixelReader();
            WritableImage adjustedImage = new WritableImage((int) originalImage.getWidth(), (int) originalImage.getHeight());
            javafx.scene.image.PixelWriter pixelWriter = adjustedImage.getPixelWriter();

            for (int x = 0; x < originalImage.getWidth(); x++) {
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    Color originalColor = originalReader.getColor(x, y);

                    double adjustedSaturation = Math.max(0.0, Math.min(1.0, originalColor.getSaturation() + saturationAdjustment));
                    Color adjustedColor = Color.hsb(originalColor.getHue(), adjustedSaturation, originalColor.getBrightness(), originalColor.getOpacity());

                    pixelWriter.setColor(x, y, adjustedColor);
                }
            }

            blackAndWhiteImage.setImage(adjustedImage);
        }
    }





    @FXML
    private void adjustHue(){
        if (blackAndWhiteImage.getImage() != null) {
            double hueAdjustment = hueSlider.getValue();

            Image originalImage = blackAndWhiteImage.getImage();
            PixelReader originalReader = originalImage.getPixelReader();
            WritableImage adjustedImage = new WritableImage((int) originalImage.getWidth(), (int) originalImage.getHeight());
            javafx.scene.image.PixelWriter pixelWriter = adjustedImage.getPixelWriter();

            for (int x = 0; x < originalImage.getWidth(); x++) {
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    Color originalColor = originalReader.getColor(x, y);

                    double hue = (originalColor.getHue() + hueAdjustment) % 360;
                    Color adjustedColor = Color.hsb(hue, originalColor.getSaturation(), originalColor.getBrightness(), originalColor.getOpacity());

                    pixelWriter.setColor(x, y, adjustedColor);
                }
            }

            blackAndWhiteImage.setImage(adjustedImage);
        }
    }

    public void saturationSlider(){
//        saturationChanger(saturationSlider.getValue());
//        saturationChanger(saturationSlider1.getValue());
    }


    public void reset(ActionEvent actionEvent){

        colorAdjust.setHue(0);
        colorAdjust.setSaturation(0);
        colorAdjust.setBrightness(0);
        hueSlider.setValue(0);
        hueSlider1.setValue(0);
        saturationSlider.setValue(0);
        brightnessSlider.setValue(0);
        noiseSlider.setValue(0);
    }

    public void exit(ActionEvent event) {
        System.exit(1);
    }

}

