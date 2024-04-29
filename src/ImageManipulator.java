import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

/**
 * Static utility class that is responsible for transforming the images.
 * Each function (or at least most functions) take in an Image and return
 * a transformed image.
 */
public class ImageManipulator {
    /**
     * Loads the image at the given path
     * @param path path to image to load
     * @return an Img object that has the given image loaded
     * @throws IOException
     */
    public static Img LoadImage(String path) throws IOException {
        return new Img(path);
    }

    /**
     * Saves the image to the given file location
     * @param image image to save
     * @param path location in file system to save the image
     * @throws IOException
     */
    public static void SaveImage(Img image, String path) throws IOException {
        image.Save("image", path);
    }

    /**
     * Converts the given image to grayscale (black, white, and gray). This is done
     * by finding the average of the RGB channel values of each pixel and setting
     * each channel to the average value.
     * @param image image to transform
     * @return the image transformed to grayscale
     */
    public static Img ConvertToGrayScale(Img image) {
        for(int i = 0; i < image.GetWidth(); i++){
            for(int j = 0; j < image.GetHeight(); j++){
                RGB rgb = image.GetRGB(i, j);
                int red = rgb.GetRed();
                int blue = rgb.GetBlue();
                int green = rgb.GetGreen();
                int average = (red+blue+green)/3;
                RGB gray = new RGB(average, average, average);
                image.SetRGB(i, j, gray);
            }
        }
        return image;
    }

    /**
     * Inverts the image. To invert the image, for each channel of each pixel, we get
     * its new value by subtracting its current value from 255. (r = 255 - r)
     * @param image image to transform
     * @return image transformed to inverted image
     */
    public static Img InvertImage(Img image) {
        for(int i = 0; i < image.GetWidth(); i++){
            for(int j = 0; j < image.GetHeight(); j++){
                RGB og = image.GetRGB(i, j);
                int red = 255 - og.GetRed();
                int green = 255 - og.GetGreen();
                int blue = 255 - og.GetBlue();
                RGB in = new RGB(red, green, blue);
                image.SetRGB(i, j, in);
            }
        }
        return image;
    }

    /**
     * Converts the image to sepia. To do so, for each pixel, we use the following equations
     * to get the new channel values:
     * r = .393r + .769g + .189b
     * g = .349r + .686g + .168b
     * b = .272r + .534g + .131b
     * @param image image to transform
     * @return image transformed to sepia
     */
    public static Img ConvertToSepia(Img image) {
        for(int i = 0; i < image.GetWidth(); i++){
            for(int j = 0; j < image.GetHeight(); j++){
                RGB og = image.GetRGB(i, j);
                int red = (int) ((.393 * og.GetRed()) + (.769 * og.GetGreen()) + (.189 * og.GetBlue()));
                int green = (int) ((.349 * og.GetRed()) + (.686 * og.GetGreen()) + (.168 * og.GetBlue()));
                int blue = (int) ((.272 * og.GetRed()) + (.534 * og.GetGreen()) + (.131 * og.GetBlue()));
                RGB in = new RGB(red, green, blue);
                image.SetRGB(i, j, in);
            }
        }
        return image;
    }

    /**
     * Creates a stylized Black/White image (no gray) from the given image. To do so:
     * 1) calculate the luminance for each pixel. Luminance = (.299 r^2 + .587 g^2 + .114 b^2)^(1/2)
     * 2) find the median luminance
     * 3) each pixel that has luminance >= median_luminance will be white changed to white and each pixel
     *      that has luminance < median_luminance will be changed to black
     * @param image image to transform
     * @return black/white stylized form of image
     */
    public static Img ConvertToBW(Img image) {
        ArrayList<Double> lums = new ArrayList<Double>();
        double median = 0;
        for(int i = 0; i < image.GetWidth(); i++){
            for(int j = 0; j < image.GetHeight(); j++){
                RGB og = image.GetRGB(i, j);
                double lum = Math.sqrt((.299 * (og.GetRed() * og.GetRed())) + (.587 * (og.GetGreen() * og.GetGreen())) + (.114 * (og.GetBlue() * og.GetBlue())));
                lums.add(lum);
                //RGB in = new RGB(red, green, blue);
                //image.SetRGB(i, j, in);
            }
        }
        Collections.sort(lums);
        if(lums.size() % 2 == 1){
            median = lums.get((lums.size() +1)/ 2 - 1);
        }
        else{
            double lower = lums.get(lums.size()/2 - 1);
            double upper = lums.get(lums.size()/2);
            median = (lower + upper)/2.0;
        }
        for(int k = 0; k < image.GetWidth(); k++){
            for(int m = 0; m < image.GetHeight(); m++){
                RGB pix = image.GetRGB(k, m);
                double pixLum = Math.sqrt((.299 * (pix.GetRed() * pix.GetRed())) + (.587 * (pix.GetGreen() * pix.GetGreen())) + (.114 * pix.GetBlue() * pix.GetBlue()));
                if(pixLum >= median){
                    RGB white = new RGB(255, 255, 255);
                    image.SetRGB(k, m, white);
                }
                else{
                    RGB black = new RGB(0, 0, 0);
                    image.SetRGB(k, m, black);
                }
            }
        }
        return image;

    }

    /**
     * Rotates the image 90 degrees clockwise.
     * @param image image to transform
     * @return image rotated 90 degrees clockwise
     */
    public static Img RotateImage(Img image) {
        // Implement this method and remove the line below
        throw new UnsupportedOperationException();
    }

    /**
     * Applies an Instagram-like filter to the image. To do so, we apply the following transformations:
     * 1) We apply a "warm" filter. We can produce warm colors by reducing the amount of blue in the image
     *      and increasing the amount of red. For each pixel, apply the following transformation:
     *          r = r * 1.2
     *          g = g
     *          b = b / 1.5
     * 2) We add a vignette (a black gradient around the border) by combining our image with an
     *      an image of a halo (you can see the image at resources/halo.png). We take 65% of our
     *      image and 35% of the halo image. For example:
     *          r = .65 * r_image + .35 * r_halo
     * 3) We add decorative grain by combining our image with a decorative grain image
     *      (resources/decorative_grain.png). We will do this at a .95 / .5 ratio.
     * @param image image to transform
     * @return image with a filter
     * @throws IOException
     */
    public static Img InstagramFilter(Img image) throws IOException {
        // Implement this method and remove the line below
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the given hue to each pixel image. Hue can range from 0 to 360. We do this
     * by converting each RGB pixel to an HSL pixel, Setting the new hue, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param hue amount of hue to add
     * @return image with added hue
     */
    public static Img SetHue(Img image, int hue) {
        // Implement this method and remove the line below
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the given saturation to the image. Saturation can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new saturation, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param saturation amount of saturation to add
     * @return image with added hue
     */
    public static Img SetSaturation(Img image, double saturation) {
        // Implement this method and remove the line below
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the lightness to the image. Lightness can range from 0 to 1. We do this
     * by converting each RGB pixel to an HSL pixel, setting the new lightness, and then
     * converting each pixel back to an RGB pixel.
     * @param image image to transform
     * @param lightness amount of hue to add
     * @return image with added hue
     */
    public static Img SetLightness(Img image, double lightness) {
        // Implement this method and remove the line below
        throw new UnsupportedOperationException();
    }
}
