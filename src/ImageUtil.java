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
}
