/**
 * 求轮廓
 *
 * @author hl
 *
 */
public class ContourRule extends AbstractRule {
	private int colorDiffThreshold = 10;
	private int liveThreshold = 0;

	private boolean isDiff(int cell, int cell2) {
		int gray1 = ImageUtil.getGray(cell);
		int gray2 = ImageUtil.getGray(cell2);
		return Math.abs(gray1 - gray2) > colorDiffThreshold;
	}

	public ContourRule(int liveThreshold, int colorDiffThreshold) {
		this.liveThreshold = liveThreshold;
		this.colorDiffThreshold = colorDiffThreshold;
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
				if (isDiff(c, cell)) {
					nLiveCells++; // 只要旁边的细胞灰度和这个细胞差异较大，则认为是边界细胞
				}
			}
		}

		// 寻找边缘
		if (nLiveCells > liveThreshold) {
			// 如果细胞cell(x,y)周围刚好liveThreshold个细胞活着，则它就活了（如果细胞已死，可以重生）
			cell = Grid.LIVE;
		} else if (nLiveCells == liveThreshold) {
			// 如果细胞cell(x,y)周围liveThreshold-1个细胞活着，则它状态不变
			cell = cells[x][y];
		} else {
			// 否则细胞死。
			cell = Grid.DIE;
		}

		// use laplacian
		// 0 1 0
		// 1 -4 1
		// 0 1 0

		// //
		// int val = (y > 0 ? ImageUtil.getGray(cells[x][y - 1]) : 0)
		// + (x > 0 ? ImageUtil.getGray(cells[x - 1][y]) : 0)
		// + (x < width - 1 ? ImageUtil.getGray(cells[x + 1][y]) : 0)
		// + (y < height - 1 ? ImageUtil.getGray(cells[x][y + 1]) : 0) - 4
		// * ImageUtil.getGray(cells[x][y]);
		// if (val < 0) {
		// val = 0;
		// }
		// if (val > 255) {
		// val = 255;
		// }
		// val = 255 - val;
		// cell = ImageUtil.makeRgb(val, val, val);

		cellsNext[x][y] = cell;
	}
}
