public class Point {
    private float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public double distanceTo(Point p) {
        double dX = p.getX() - getX();
        double dY = p.getY() - getY();

        return Math.sqrt(dX * dX + dY * dY);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
