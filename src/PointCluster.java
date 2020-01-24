import processing.core.PApplet;

import java.util.ArrayList;

public class PointCluster {
    private static final float CIRCLE_DIAM = 20;

    private ArrayList<Point> points, oldCenters;
    private Point center;

    public PointCluster(float x, float y) {
        this.center = new Point(x, y);
        this.points = new ArrayList<>();
        this.oldCenters = new ArrayList<>();
    }

    public Point getCenter() {
        return center;
    }

    public void recalculateCenter() {
        if (points.size() == 0) return;

        float x = 0;
        float y = 0;

        for (Point p : points) {
            x += p.getX();
            y += p.getY();
        }

        x /= points.size();
        y /= points.size();

        this.center.setX(x);
        this.center.setY(y);

    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public double averagePointDistance() {
        if (points.size() == 0) return -1;
        double output = 0;

        for (Point p : points) {
            output += center.distanceTo(p);
        }

        output /= points.size();

        return output;
    }

    public RGBColor getCenterPixel(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        int r = (int) center.getY();
        int c = (int) center.getX();

        return new RGBColor(red[r][c], green[r][c], blue[r][c]);
    }

    public void drawLine(PApplet window) {
        window.stroke(200,200,200);

        for (int i = 0; i < oldCenters.size() - 1; i++) {
            Point c1 =  oldCenters.get(i);
            Point c2 =  oldCenters.get(i + 1);

            window.line(c1.getX(), c1.getY(), c2.getX(), c2.getY());
        }

        Point prevCenter = oldCenters.get(oldCenters.size() - 1);

        window.line(prevCenter.getX(), prevCenter.getY(), center.getX(), center.getY());
    }

    public void drawCenter(PApplet window) {
        window.fill(0, 255,0);
        window.stroke(0,255,0);

        window.ellipse(center.getX(), center.getY(), CIRCLE_DIAM, CIRCLE_DIAM);
    }

    public void resetLine() {
        oldCenters.clear();
    }

    public void clearList() {
        points.clear();
    }
}
