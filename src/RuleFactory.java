public class RuleFactory {
    public static String[] getRuleNames() {
        return new String[]{"default", "wuxin", "water", "contour"};
    }

    public static Rule createRule(String ruleName, int liveThreshold) {
        switch (ruleName) {
            case "default":
                return new DefaultRule(liveThreshold);
            case "wuxin":
                return new WuxinRule(liveThreshold);
            case "water":
                return new WaterRule(liveThreshold, 100);
            case "contour":
                return new ContourRule(liveThreshold, 100);
            default:
                return new DefaultRule(liveThreshold);
        }
    }
}
