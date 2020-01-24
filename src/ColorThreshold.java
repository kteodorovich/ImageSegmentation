import processing.core.PApplet;

import java.util.ArrayList;

public class ColorThreshold implements PixelFilter, Clickable {
    private double threshold;

    private ArrayList<RGBColor> targetColors;

    public ColorThreshold() {
        targetColors = new ArrayList<>();

        threshold = 50;
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (int row = 0; row < red.length; row++) {
            for (int col = 0; col < red[row].length; col++) {
                RGBColor pixel = new RGBColor(red[row][col], green[row][col], blue[row][col]);

                if (targetColors.size() > 0) {

                    for (RGBColor targetColor : targetColors) {
                        if (pixel.distanceTo(targetColor) < threshold){
//                        if (ratiosCloseEnough(pixel, targetColor)) {
                            red[row][col] = 255;
                            green[row][col] = 255;
                            blue[row][col] = 255;
                        }
                    }

                    if (red[row][col] != 255 || green[row][col] != 255 || blue[row][col] != 255) {
                        red[row][col] = 0;
                        green[row][col] = 0;
                        blue[row][col] = 0;

                    }
                }
            }
        }

        img.setColorChannels(red, green, blue);
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        RGBColor color = new RGBColor(red[mouseY][mouseX], green[mouseY][mouseX], blue[mouseY][mouseX]);
        targetColors.add(color);
        System.out.println("ADDED COLOR " + color.toString());
    }

    @Override
    public void keyPressed(char key) {
        if (key == '+') {
            threshold += 10;
            System.out.println("threshold = " + threshold);

        } else if (key == '-') {
            threshold -= 10;
            System.out.println("threshold = " + threshold);

        } else if (key == 'c') {
            resetTargetColors();
        }

    }

    public int numTargetColors() {
        return targetColors.size();
    }

    public void resetTargetColors() {
        targetColors.clear();
    }
}

