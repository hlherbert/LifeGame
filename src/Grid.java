import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Grid {

    public static final int LIVE = Color.black.getRGB(); // LIVE COLOR
    public static final int DIE = Color.white.getRGB(); // DIE COLOR

    private int width = 0;
    private int height = 0;
    private int cellNum = 0; // 初始生的细胞个数
    private int liveThreshold = 0; // 一个细胞周围3x3网格内，有至少liveThreshold个细胞活着，则下一轮迭代，这个细胞就活着，否则它就孤独而终
    private int[][] cells; // 当前细胞状态
    private int[][] cellsNext;// 下一轮的细胞状态
    private Random rand;
    private Rule rule;

    BufferedImage image;
    int[] imgData;

    public static boolean isLive(int cell) {
        return (cell != DIE);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getCells() {
        return cells;
    }

    public boolean isEmpty() {
        return cells == null;
    }

    public void create(int w, int h, int cellNum, int liveThreshold, String ruleName) {
        this.width = w; // 区域宽度
        this.height = h; // 区域高度
        this.cellNum = cellNum; // 初始细胞个数
        this.liveThreshold = liveThreshold; // 存活阈值：一个细胞周围3x3网格内，有至少liveThreshold个细胞活着，则下一轮迭代，这个细胞就活着，否则它就孤独而终
        cells = new int[w][h];
        cellsNext = new int[w][h];
        rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = DIE;
                cellsNext[i][j] = DIE;
            }
        }

        //this.rule = new DefaultRule(liveThreshold);
        this.rule = RuleFactory.createRule(ruleName, liveThreshold);
        createImage();
    }

    public void createByImage(String filename, int liveThreshold,
                              int colorDiffThreshold, boolean transferToGray, String ruleName) {
        try {
            image = ImageIO.read(new File(filename));
            // 图片转为黑白照片
            if (transferToGray) {
                ImageUtil.makeImageGray(image);
            }
            int w = image.getWidth();
            int h = image.getHeight();
            this.width = w; // 区域宽度
            this.height = h; // 区域高度
            imgData = new int[w * h];
            image.getRGB(0, 0, w, h, imgData, 0, w);

            this.liveThreshold = liveThreshold;
            cells = ImageUtil.createCellsFromImage(image);
            cellsNext = ImageUtil.createCellsFromImage(image);
            rand = new Random(System.currentTimeMillis());

            //this.rule = new WaterRule(liveThreshold, colorDiffThreshold);
            this.rule = RuleFactory.createRule(ruleName, liveThreshold);

            this.rule.preprocess(cells, width, height);
            updateImage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createImage() {
        image = ImageUtil.createImageFromCells(cells, width, height);
        imgData = ImageUtil.cells2data(cells, width, height);
    }

    private void updateImage() {
        ImageUtil.setImageFromCells(image, cells, imgData, width, height);
    }

    // 所有细胞状态重置为DIE,然后选择cellNum个随机细胞状态设置为LIVE
    public void reset() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = DIE;
            }
        }
        for (int i = 0; i < cellNum; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            cells[x][y] = rule.genRandomLiveCell();
        }

        updateImage();
    }

    // 更新细胞(x,y)状态
    private void updateCellState(int x, int y) {
        rule.updateCellState(cells, cellsNext, x, y, width, height);
    }

    // 演化一轮
    public void nextIter() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                updateCellState(i, j);
            }
        }
        // 更新完一轮后，交换cells和cellsNext，使cells更新为cellsNext(最新状态)
        int[][] tmp = cellsNext;
        cellsNext = cells;
        cells = tmp;

        updateImage();
    }

    // 演化n轮
    public void nextIter(int n) {
        for (int i = 0; i < n; i++) {
            nextIter();
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}
