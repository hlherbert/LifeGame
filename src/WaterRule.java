import java.awt.Color;

/**
 * 墨水侵染效果
 *
 * @author hl
 *
 */
public class WaterRule extends AbstractRule {
    private int moshuiThreshold = 100; // 墨水颜色阈值，低于这个就是有墨水
    private int liveThreshold = 0;

    private boolean hasMoshui(int cell) {
        int gray1 = ImageUtil.getGray(cell);
        return gray1 < moshuiThreshold;
    }

    private int avgGray(int[][] cells, int x, int y, int w, int h) {
        int val = (y > 0 && x > 0 ? ImageUtil.getGray(cells[x - 1][y - 1])
                : 255)
                + (y > 0 ? ImageUtil.getGray(cells[x][y - 1]) : 255)
                + (y > 0 && x < w ? ImageUtil.getGray(cells[x + 1][y - 1])
                        : 255)
                + (x > 0 ? ImageUtil.getGray(cells[x - 1][y]) : 255)

                + (x < w ? ImageUtil.getGray(cells[x + 1][y]) : 255)
                + (x > 0 && y < h ? ImageUtil.getGray(cells[x - 1][y + 1])
                        : 255)
                + (y < h ? ImageUtil.getGray(cells[x][y + 1]) : 255)
                + (y < h && x < w ? ImageUtil.getGray(cells[x + 1][y + 1])
                        : 255);

        int gray = val / 8;
        // if (gray < 255) {
        // System.out.println(gray);
        // }
        return gray;
    }

    public WaterRule(int liveThreshold, int colorDiffThreshold) {
        this.liveThreshold = liveThreshold;
        this.moshuiThreshold = colorDiffThreshold;
    }

    @Override
    public void updateCellState(int[][] cells, int[][] cellsNext, int x, int y,
            int width, int height) {
        if (GeoUtil.outOfBound(x, y, width, height)) {
            return;
        }
        int cell = cells[x][y];
        // cells(x,y)周围8个细胞，计算有几个是活着的
        int nLiveCells = 0;
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
                int c = cells[i][j];

                if (hasMoshui(c)) {
                    nLiveCells++; // 只要旁边的细胞灰度和这个细胞差异较大，则认为是边界细胞
                }
            }
        }

        // 寻找边缘
        if (nLiveCells == liveThreshold) {
            // 如果细胞cell(x,y)周围刚好liveThreshold个细胞活着，则它就活了（如果细胞已死，可以重生）
            cell = Grid.LIVE;
        } else if (nLiveCells == liveThreshold - 1) {
            cell = Color.green.getRGB();// cells[x][y];
        } else {
            // 否则细胞死。
            cell = Grid.DIE;
        }

        cellsNext[x][y] = cell;
    }
}
