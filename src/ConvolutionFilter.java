import processing.core.PApplet;

public class ConvolutionFilter implements PixelFilter {
    private static final short[][] GAUSSIAN_BLUR = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    private static short[][] BIG_GAUSSIAN = {
            {1, 4, 6, 4, 1},
            {4, 16, 24, 16, 4},
            {6, 24, 36, 24, 6},
            {4, 16, 24, 16, 4},
            {1, 4, 6, 4, 1}
    };
    private static final short[][] SHARPEN = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private static final short[][] HORIZONTAL_LINES = {{-1, -1, -1}, {2, 2, 2}, {-1, -1, -1}};
    private static final short[][] VERTICAL_LINES = {{-1, 2, -1}, {-1, 2, -1}, {-1, 2, -1}};
    private static final short[][] PREWITT_EDGE = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};

    private short[][] kernel;
    private int kernelWeight;
    private int center;

    public ConvolutionFilter() {
        this.kernel = BIG_GAUSSIAN;

        this.kernelWeight = calculateKernelWeight();
        center = kernel.length / 2;
    }



    private int calculateKernelWeight() {
        int sum = 0;

        for (short[] row : kernel) {
            for (short val : row) {
                sum += val;
            }
        }

        if (sum <= 0) sum = 1;
        return sum;
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        short[][] newPixels = new short[pixels.length][pixels[0].length];

        for (int row = 0; row < pixels.length - (kernel.length - 1); row++) {
            for (int col = 0; col < pixels[row].length - (kernel.length - 1); col++) {
                applyKernel(row, col, pixels, newPixels);
            }
        }

        img.setPixels(newPixels);
        return img;
    }

    private void applyKernel(int row, int col, short[][] oldPixels, short[][] newPixels) {
        int output = 0;
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel.length; c++) {
                short kernelVal = kernel[r][c];
                short pixelVal = oldPixels[row + r][col + c];

                output += kernelVal * pixelVal;
            }

        }

        output /= kernelWeight;
        if (output < 0) output = 0;
        if (output > 255) output = 255;
        newPixels[row + center][col + center] = (short) output;
    }

    private static short[][] generateBoxBlur(int n) {
        short[][] output = new short[n][n];

        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < output[i].length; j++) {
                output[i][j] = 1;
            }
        }

        return output;
    }


    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }
}

