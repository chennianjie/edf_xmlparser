package edf.xmlparser.enums;

public enum LogTypeEnum {

    DEBUG("DEBUG", 1),
    INFO("INFO", 2),
    WARNING("WARNING", 3),
    ERROR("ERROR", 4);

    LogTypeEnum(String name, Integer rank){
        this.name = name;
        this.rank = rank;
    }
    private String name;
    private Integer rank;

    public String getName() {
        return name;
    }

    public Integer getRank() {
        return rank;
    }

    public static LogTypeEnum getByValue(String name) {
        for(LogTypeEnum typeEnum : LogTypeEnum.values()) {
            if(typeEnum.name == name) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }
}
