package client.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageUtils {
    private final static String basePath = "client/src/main/resources/images/";

    /***
     * Loads a file into an Image
     * @param fileName the name of the file under the basePath directory
     * @return an Image containing the loaded file, or null
     */
    public Image loadImageFile(String fileName){
        try {
            return new Image(new FileInputStream(basePath + fileName));
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + basePath + fileName);
            return null;
        }
    }

    /***
     * Generates an ImageView for a given Image
     * @param image the Image to use
     * @param size the size, in pixels
     * @return an ImageView corresponding to the Image
     */
    public ImageView generateImageView(Image image, int size){
        ImageView imageView;
        if(image!=null) imageView = new ImageView(image);
        else imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setCache(true);
        return imageView;
    }

    /***
     * Generates an ImageView for a given Image
     * @param fileName the name of the file under the basePath directory
     * @param size the size, in pixels
     * @return an ImageView corresponding to the Image
     */
    public ImageView generateImageView(String fileName, int size){
        return generateImageView(loadImageFile(fileName), size);
    }
}
