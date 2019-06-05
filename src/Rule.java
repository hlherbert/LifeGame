public interface Rule {
    public void updateCellState(int[][] cells, int[][] cellsNext, int x, int y,
                                int width, int height);

    /**
     * 随机生成一个活细胞颜色
     *
     * @return
     */
    public int genRandomLiveCell();
}
