package cn.lancedai.weye.common.emuration;

public enum TimePattern {
    HH_MM_SS("HH:MM:SS");


    private final String pattern;

    TimePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
