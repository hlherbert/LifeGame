public abstract class AbstractRule implements Rule {

    @Override
    public int genRandomLiveCell() {
        return Grid.LIVE;
    }

    @Override
    public void preprocess(int[][] cells, int width, int height) {
        // do nothing
    }
}
