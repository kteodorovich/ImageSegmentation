public class RGBColor {
    private short r, g, b;

    public RGBColor(short r, short g, short b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public short getR() {
        return r;
    }

    public short getG() {
        return g;
    }

    public short getB() {
        return b;
    }

    public double distanceTo(RGBColor targetColor) {
        int dR = targetColor.getR() - getR();
        int dG = targetColor.getG() - getG();
        int dB = targetColor.getB() - getB();

        return Math.sqrt(dR * dR + dG * dG + dB * dB);
    }
}
