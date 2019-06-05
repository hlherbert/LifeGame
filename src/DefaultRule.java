public class DefaultRule extends AbstractRule {
    private int liveThreshold = 0;

    private static boolean isLive(int cell) {
        return (cell != Grid.DIE);
    }

    public DefaultRule(int liveThreshold) {
        this.liveThreshold = liveThreshold;
    }

    @Override
    public void updateCellState(int[][] cells, int[][] cellsNext, int x, int y,
                                int width, int height) {
        if (GeoUtil.outOfBound(x, y, width, height)) {
            return;
        }
        int cell = cellsNext[x][y];
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
                if (isLive(c)) {
                    nLiveCells++;
                }
            }
        }

        // 每个细胞死或活的状态由它周围的八个细胞所决定。
        // “人口过少”：任何活细胞如果活邻居少于2个，则死掉。
        // “正常”：任何活细胞如果活邻居为2个或3个，则继续活。
        // “人口过多”：任何活细胞如果活邻居大于3个，则死掉。
        // “繁殖”：任何死细胞如果活邻居正好是3个，则活过来。
        if (nLiveCells == liveThreshold) {
            // 如果细胞cell(x,y)周围刚好liveThreshold个细胞活着，则它就活了（如果细胞已死，可以重生）
            cell = Grid.LIVE;
        } else if (nLiveCells == liveThreshold - 1) {
            // 如果细胞cell(x,y)周围liveThreshold-1个细胞活着，则它状态不变
            cell = cells[x][y];
        } else {
            // 否则细胞死。
            cell = Grid.DIE;
        }

        cellsNext[x][y] = cell;
    }

}
