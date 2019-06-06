import java.awt.image.BufferedImage;

public class ImageUtil {

    public static int[] cells2data(int[][] cells, int w, int h) {
        int[] data = new int[w * h];
        int p = 0;
        for (int i = 0; i < cells.length; i++) {
            int[] line = cells[i];
            for (int j = 0; j < line.length; j++) {
                data[p++] = cells[j][i];
            }
        }
        return data;
    }

    public static void cells2data(int[][] cells, int[] data, int w, int h) {
        int p = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                data[j * w + i] = cells[i][j];
            }
        }
    }

    public static BufferedImage createImageFromCells(int[][] cells, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // 0xFFFFFF
        int[] data = cells2data(cells, w, h);
        img.setRGB(0, 0, w, h, data, 0, 0);
        return img;
    }

    public static void setImageFromCells(BufferedImage img, int[][] cells,
                                         int[] data, int w, int h) {
        cells2data(cells, data, w, h);
        img.setRGB(0, 0, w, h, data, 0, w);
    }

    public static int[][] createCellsFromImage(BufferedImage img) {
        if (img == null) {
            return null;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        int[] data = new int[w * h];
        img.getRGB(0, 0, w, h, data, 0, w);
        int[][] cells = new int[w][h];
        int p = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                cells[i][j] = data[p++];
            }
        }
        return cells;
    }

    public static void makeImageGray(BufferedImage img) {
        if (img == null) {
            return;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        int[] data = new int[w * h];
        img.getRGB(0, 0, w, h, data, 0, w);
        for (int i = 0; i < data.length; i++) {
            int gray = getGray(data[i]);
            data[i] = makeRgb(gray, gray, gray);
        }
        img.setRGB(0, 0, w, h, data, 0, w);
    }

    public static int getGray(int rgb) {
        // Gray = R*0.299 + G*0.587 + B*0.114
        int r = (rgb & 0xFF0000) >> 16;
        int g = (rgb & 0x00FF00) >> 8;
        int b = (rgb & 0xFF);
        int gray = (r * 38 + g * 75 + b * 15) >> 7;
        return gray;
    }

    public static int makeRgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    /**
     * 寻找颜色距离，以RGB空间
     *
     * @param color1
     * @param color2
     * @return
     */
    public static double findColorDistance(int color1, int color2) {
        int r1 = (color1 & 0xFF0000) >> 16;
        int g1 = (color1 & 0x00FF00) >> 8;
        int b1 = (color1 & 0xFF);

        int r2 = (color2 & 0xFF0000) >> 16;
        int g2 = (color2 & 0x00FF00) >> 8;
        int b2 = (color2 & 0xFF);

        double distance = Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
        return distance;
    }

    /**
     * 寻找颜色的色相距离，以HSV空间，比较H的差异
     *
     * @param color1
     * @param color2
     * @return 两个颜色之间Hue(色相的差异)
     */
    public static double findColorDistanceByHue(int color1, int color2) {
        int r1 = (color1 & 0xFF0000) >> 16;
        int g1 = (color1 & 0x00FF00) >> 8;
        int b1 = (color1 & 0xFF);

        int r2 = (color2 & 0xFF0000) >> 16;
        int g2 = (color2 & 0x00FF00) >> 8;
        int b2 = (color2 & 0xFF);

        float[] hsv1 = rgb2hsvF(r1, g1, b1);
        float h1 = hsv1[0];

        float[] hsv2 = rgb2hsvF(r2, g2, b2);
        float h2 = hsv2[0];

        return Math.abs(h1 - h2);
    }

    /*
     * RGB转HSV
     * 输入范围R,G,B, 0~255
     * 输出范围[0]:0~1,[1]:0~1,[2]:0~255
     */
    public static float[] rgb2hsvF(int R, int G, int B) {

        int tMax, tMin;
        float H = 0, S = 0, V = 0;
        float delta;
        float[] tRet = new float[3];
        tMax = Math.max(R, Math.max(G, B));
        tMin = Math.min(R, Math.min(G, B));
        if (0 == tMax) {
            tRet[0] = 0;
            tRet[1] = 0;
            tRet[2] = 0;
            return tRet;
        }

        V = tMax;
        delta = tMax - tMin;

        S = delta / tMax;

        if (0 == delta) {
            H = 0;
        } else if (G == tMax) {
            H = 2 + (B - R) / delta; // between cyan & yellow
        } else if (B == tMax) {
            H = 4 + (R - G) / delta; // between magenta & cyan
        } else if (R == tMax) {
            H = (G - B) / delta;     // between yellow & magenta
        }

        H *= 60;
        if (H < 0) {
            H += 360;
        }

        tRet[0] = H / 360.0f;
        tRet[1] = S;
        tRet[2] = V;
        return tRet;
    }
}
