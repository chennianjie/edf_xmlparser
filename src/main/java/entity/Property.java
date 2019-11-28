package entity;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 10/11/2019
 */
public class Property { ;

    private String id;

    private String currValue;

    private String validFrom;

    private String validTo;

    private String validFromWithTime;

    private String validToWithTime;

    private String language;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrValue() {
        return currValue;
    }

    public void setCurrValue(String currValue) {
        this.currValue = currValue;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getValidFromWithTime() {
        return validFromWithTime;
    }

    public void setValidFromWithTime(String validFromWithTime) {
        this.validFromWithTime = validFromWithTime;
    }

    public String getValidToWithTime() {
        return validToWithTime;
    }

    public void setValidToWithTime(String validToWithTime) {
        this.validToWithTime = validToWithTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id='" + id + '\'' +
                ", currValue='" + currValue + '\'' +
                ", validFrom='" + validFrom + '\'' +
                ", validTo='" + validTo + '\'' +
                ", validFromWithTime='" + validFromWithTime + '\'' +
                ", validToWithTime='" + validToWithTime + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
