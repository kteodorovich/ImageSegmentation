import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;

public class ObjectTracking implements PixelFilter, Clickable {
    private static final double AVG_DISTANCE_THRESHOLD = 100;

    private ArrayList<PointCluster> clusters;
    private int k;
    private boolean showingLines, recoloringClusters;

    private ArrayList<PixelFilter> filters;
    private ColorThreshold thresholdFilter;

    public ObjectTracking() {
        this.k = 10;

        this.clusters = new ArrayList<>();
        this.filters = new ArrayList<>();

        /*
                3 filters
         */
        filters.add(new ColorThreshold()); // get target colors
        filters.add(new ConvolutionFilter()); // blur away noise
        filters.add(new Monochrome((short) 254)); // reset to b&w

        thresholdFilter = (ColorThreshold) filters.get(0);

        showingLines = false;
        recoloringClusters = false;
    }

    @Override
    public DImage processImage(DImage img) {
        if (clusters.size() == 0) {
            initRandomClusters(img);
        }

        DImage originalImg = new DImage(img);

        if (thresholdFilter.numTargetColors() > 0) {

            for (PixelFilter f : filters) {
                img = f.processImage(img);
            }

            short[][] pixels = img.getBWPixelGrid();
            doKMeansClustering(pixels);

            checkIfKIsCorrect(img);

            if (recoloringClusters) img = setClusterPointsToColor(img, originalImg);
        }
        return img;
    }

    /*
            K MEANS CLUSTERING STUFF
     */
    private void doKMeansClustering(short[][] pixels) {
        if (clusters.size() == 0) return;
        boolean done = false;

        while (!done) {
            clearClusters();
            assignPixelsToClusters(pixels);

            int changed = recalculateClusterCenters();
            if (changed == 0) done = true;

        }
    }

    private void clearClusters() {
        for (PointCluster cluster : clusters) {
            cluster.clearList();
        }
    }

    private void assignPixelsToClusters(short[][] pixels) {
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[r].length; c++) {

                if (pixels[r][c] != 0) {
                    Point p = new Point(c, r);
                    assignPointToClosestCluster(p);
                }
            }
        }
    }

    private int recalculateClusterCenters() {
        int changed = 0;

        for (PointCluster cluster : clusters) {
            Point oldCenter = cluster.getCenter();
            cluster.recalculateCenter();

            if (!cluster.getCenter().equals(oldCenter)) {
                changed++;
            }
        }

        return changed;
    }

    private void assignPointToClosestCluster(Point p) {
        double minDistance = Double.MAX_VALUE;
        int closestCluster = 0;

        for (int i = 0; i < clusters.size(); i++) {
            double distance = p.distanceTo(clusters.get(i).getCenter());

            if (distance < minDistance) {
                minDistance = distance;
                closestCluster = i;
            }
        }

        clusters.get(closestCluster).addPoint(p);
    }

    private void initRandomClusters(DImage img) {
        for (int i = 0; i < k; i++) {
            addRandomCluster(img);
        }
    }

    private void checkIfKIsCorrect(DImage img) {
        for (int i = 0; i < clusters.size(); i++) {
            PointCluster cluster = clusters.get(i);

            // cluster too small/empty
            if (cluster.getPoints().size() <= 5) {
                clusters.remove(cluster);
                k--;
                System.out.println("K = " + clusters.size());
                break;
            }

            // cluster too big, should be split
            if (cluster.averagePointDistance() > AVG_DISTANCE_THRESHOLD) {
                k++;
                addRandomCluster(img);
                System.out.println("K = " + clusters.size());
                break;
            }
        }

        // clusters too close
        for (int i = 0; i < clusters.size() - 1; i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                PointCluster c1 = clusters.get(i);
                PointCluster c2 = clusters.get(j);

                double distance = c1.getCenter().distanceTo(c2.getCenter());

                if (distance < c1.averagePointDistance()) {
                    k--;
                    clusters.remove(c2);
                    System.out.println("K = " + clusters.size());
                    break;
                }
            }
        }

    }

    private void addRandomCluster(DImage img) {
        float randX = (float) (Math.random() * img.getWidth());
        float randY = (float) (Math.random() * img.getHeight());

        clusters.add(new PointCluster(randX, randY));
    }

    private void resetClusters() {
        resetLines();
        clusters.clear();
    }

    private DImage setClusterPointsToColor(DImage img, DImage originalImg) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (PointCluster cluster : clusters) {
            RGBColor color = cluster.getCenterPixel(originalImg);

            for (Point p : cluster.getPoints()) {
                setPixelTo(color, (int) p.getY(), (int) p.getX(), red, green, blue);
            }
        }

        img.setColorChannels(red, green, blue);
        return img;
    }

    private void setPixelTo(RGBColor color, int row, int col, short[][] red, short[][] green, short[][] blue) {
        red[row][col] = color.getR();
        green[row][col] = color.getG();
        blue[row][col] = color.getB();
    }


    private void resetLines() {
        for (PointCluster c : clusters) {
            c.resetLine();
        }
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        for (PointCluster cluster : clusters) {
            if (thresholdFilter.numTargetColors() > 0) {

                if (showingLines) cluster.drawLine(window);
                cluster.drawCenter(window);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        thresholdFilter.mouseClicked(mouseX, mouseY, img);
    }

    @Override
    public void keyPressed(char key) {
        if (key == '#') {
            k = Integer.parseInt(JOptionPane.showInputDialog("new number of cluster:"));
            resetClusters();
        }

        if (key == '/') {
            resetLines();
        }
        thresholdFilter.keyPressed(key);
    }

}

