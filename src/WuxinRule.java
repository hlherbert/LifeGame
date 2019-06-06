import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 五行规则
 */
public class WuxinRule implements Rule {

    private int liveThreshold = 0;

    private Random random = new Random(System.currentTimeMillis());

    // 金木水火土的颜色和对应值
    private final static int DIE_COLOR = Grid.DIE;
    private final static int JIN_COLOR = new Color(255, 255, 0).getRGB();
    private final static int MU_COLOR = new Color(34, 177, 76).getRGB();
    private final static int SHUI_COLOR = new Color(30, 144, 255).getRGB();
    private final static int HUO_COLOR = new Color(255, 0, 0).getRGB();
    private final static int TU_COLOR = new Color(185, 122, 87).getRGB();


    private final static int DIE = 0;
    private final static int JIN = 1;
    private final static int MU = 2;
    private final static int SHUI = 3;
    private final static int HUO = 4;
    private final static int TU = 5;
    private final static int WUXIN_SIZE = 6;

    // 克制表
    private final static int[] KEZHI_TB;

    // 生表
    private final static int[] SHEN_TB;

    // 五行颜色编码 WUXIN_CODEC[color] = wuxinCode; // 例如 WUXIN_CODEC[JIN_COLOR] = JIN;
    private final static Map<Integer, Integer> WUXIN_CODEC;


    // 五行颜色 WUXIN_COLORS[code] = wuxinColor; // 例如 WUXIN_CODEC[JIN] = JIN_COLOR;
    private final static int[] WUXIN_COLORS;


    static {
//		相生：
//		木 ->  火 -> 土 -> 金 -> 水
//
//		相克：
//		金 -> 木 -> 土 -> 水 -> 火
        KEZHI_TB = new int[WUXIN_SIZE];
        SHEN_TB = new int[WUXIN_SIZE];
        WUXIN_CODEC = new HashMap<Integer, Integer>();
        WUXIN_COLORS = new int[WUXIN_SIZE];

        // kezhi[a] = b 表示 a克制b
        KEZHI_TB[DIE] = DIE;
        KEZHI_TB[JIN] = MU;
        KEZHI_TB[MU] = TU;
        KEZHI_TB[TU] = SHUI;
        KEZHI_TB[SHUI] = HUO;
        KEZHI_TB[HUO] = JIN;

        // shen[a] = b 表示 a生b
        SHEN_TB[DIE] = DIE;
        SHEN_TB[MU] = HUO;
        SHEN_TB[HUO] = TU;
        SHEN_TB[TU] = JIN;
        SHEN_TB[JIN] = SHUI;
        SHEN_TB[SHUI] = MU;

        WUXIN_CODEC.put(DIE_COLOR, DIE);
        WUXIN_CODEC.put(JIN_COLOR, JIN);
        WUXIN_CODEC.put(MU_COLOR, MU);
        WUXIN_CODEC.put(SHUI_COLOR, SHUI);
        WUXIN_CODEC.put(HUO_COLOR, HUO);
        WUXIN_CODEC.put(TU_COLOR, TU);

        WUXIN_COLORS[DIE] = DIE_COLOR;
        WUXIN_COLORS[JIN] = JIN_COLOR;
        WUXIN_COLORS[MU] = MU_COLOR;
        WUXIN_COLORS[SHUI] = SHUI_COLOR;
        WUXIN_COLORS[HUO] = HUO_COLOR;
        WUXIN_COLORS[TU] = TU_COLOR;
    }

    /**
     * a是否克制b
     *
     * @param a 一种五行属性
     * @param b 第二种五行属性
     * @return a是否克制b
     */
    private static boolean isKezhi(int a, int b) {
        return KEZHI_TB[a] == b;
    }


    private static boolean isLive(int cell) {
        return (cell != DIE_COLOR);
    }

    public WuxinRule(int liveThreshold) {
        this.liveThreshold = liveThreshold;
    }


    /**
     * a是否生b
     *
     * @param a 一种五行属性
     * @param b 第二种五行属性
     * @return a是否生b
     */
    private static boolean isShen(int a, int b) {
        return SHEN_TB[a] == b;
    }

    /**
     * 获取某个五行编码a对应的生值
     *
     * @param a 五行编码
     * @return b, 当a生b
     */
    private static int getShen(int a) {
        return SHEN_TB[a];
    }

    /**
     * 颜色转五行编码
     *
     * @param color 颜色
     * @return 五行编码
     */
    private static int encodeWuxinColor(int color) {
        // 颜色预处理，将不是五行颜色找到最近的五行颜色
//        int wuxinColor = toMostLikeWuxinColor(color);
//        return WUXIN_CODEC.get(wuxinColor);

        return WUXIN_CODEC.get(color);
    }

    /**
     * 将颜色转换为最接近的五行颜色
     *
     * @param color 颜色
     * @return 最像的五行颜色
     */
    private static int toMostLikeWuxinColor(int color) {
        int mostLikeColor = WUXIN_COLORS[DIE];
        double distMin = ImageUtil.findColorDistanceByHue(color, mostLikeColor);

        for (int i = 1; i < WUXIN_SIZE; i++) {
            int wuxinColor = WUXIN_COLORS[i];
            double dist = ImageUtil.findColorDistanceByHue(color, wuxinColor);
            if (dist < distMin) {
                distMin = dist;
                mostLikeColor = wuxinColor;
            }
        }
        return mostLikeColor;
    }



    /**
     * 五行编码转五行颜色
     *
     * @param wuxinCode 五行便阿门
     * @return 五行颜色
     */
    private static int decodeWuxinColor(int wuxinCode) {
        return WUXIN_COLORS[wuxinCode];
    }

    private static int findMostWuxin(int[] nWuxin) {
        int mostWuxin = DIE;
        int mostWuxinNum = 0;
        for (int i = 1; i < 5; i++) {
            if (nWuxin[i] > mostWuxinNum) {
                mostWuxin = i;
                mostWuxinNum = nWuxin[i];
            }
        }
        return mostWuxin;
    }


    @Override
    public void preprocess(int[][] cells, int width, int height) {
        // 将所有cell颜色转为最接近的wuxin颜色
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int wuxinColor = toMostLikeWuxinColor(cells[x][y]);
                cells[x][y] = wuxinColor;
            }
        }
    }

    @Override
    public void updateCellState(int[][] cells, int[][] cellsNext, int x, int y,
                                int width, int height) {
        if (GeoUtil.outOfBound(x, y, width, height)) {
            return;
        }

        // cells(x,y) = 有5种可能状态值[DIE, JIN, MU, SHUI, HUO, TU], 分别代表 死亡、金木水火土
        // 如果它是活的，如果上下左右 周围4个细胞，有2个克它的细胞，则它死亡；否则保持不变。

        int nKezhiNeighbours = 0;
        int nLiveNeighbours = 0;
        int cell = cells[x][y]; // 当前细胞c(x,y)
        int[] nWuxin = new int[WUXIN_SIZE];

        int cellCode = encodeWuxinColor(cell);

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // 不计入(x,y)本身
                if (i == x && j == y) {
                    continue;
                }
                // 超出边界，不计入
                if (GeoUtil.outOfBound(i, j, width, height)) {
                    continue;
                }
                // 四个角或者中心， 不计入
                if (Math.abs(i - x) + Math.abs(j - y) != 1) {
                    continue;
                }

                int neighbour = cells[i][j];
                int neighbourCode = encodeWuxinColor(neighbour);
                if (isLive(cell) && isKezhi(neighbourCode, cellCode)) {
                    nKezhiNeighbours++;
                }

                if (isLive(neighbour)) {
                    nLiveNeighbours++;
                }

                nWuxin[neighbourCode]++;
            }
        }

        // 每个细胞死或活的状态由它上下左右周围的4个细胞所决定。
        // “人口过少”：任何活细胞如果活邻居少于2个，则死掉。  --- 该规则会导致灭绝，去掉
        // “克制”：任何活细胞，如果克制它的邻居大于等于2个，则死掉。
        // “正常”：任何活细胞，如果没有被克制或者人口过少，则继续活。
        // “繁殖”：任何死细胞，如果活邻居 >=2个，则活过来，它的属性为邻居中数量最多的种类对应的生成属性。
        if (isLive(cell)) {
//			if (nLiveNeighbours < 2) {
//				cell = Grid.DIE;
//			} else
            if (nKezhiNeighbours >= 2) {
                cell = DIE_COLOR;
            }
        } else {
            if (nLiveNeighbours >= liveThreshold) {
                int mostWuxin = findMostWuxin(nWuxin);
                cellCode = getShen(mostWuxin);
                cell = decodeWuxinColor(cellCode);
            }
        }

        cellsNext[x][y] = cell;
    }

    @Override
    public int genRandomLiveCell() {
        return decodeWuxinColor(random.nextInt(5) + 1);
    }
}
