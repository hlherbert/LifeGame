public abstract class AbstractRule implements Rule {

    @Override
    public int genRandomLiveCell() {
        return Grid.LIVE;
    }
}
