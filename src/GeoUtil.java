public class GeoUtil {

    public static boolean outOfBound(int i, int j, int w, int h) {
        return (i < 0 || i >= w || j < 0 || j >= h);
    }
}
