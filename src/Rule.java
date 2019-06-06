public interface Rule {
    void updateCellState(int[][] cells, int[][] cellsNext, int x, int y,
                         int width, int height);

    /**
     * 随机生成一个活细胞颜色
     *
     * @return
     */
    int genRandomLiveCell();

    /**
     * 预处理
     *
     * @param cells  元胞
     * @param width  宽度
     * @param height 高度
     */
    void preprocess(int[][] cells, int width, int height);
}
