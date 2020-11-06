package my.learning.domain.enums;

public enum BoardType {
    NOTICE("noticeBoard"),
    FREE("freeBoard");

    private final String value;

    BoardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
