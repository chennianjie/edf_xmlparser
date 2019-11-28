package entity;

import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 10/11/2019
 */
public class Entity {


    private String type;

    private String subtype;

    private String rcssubtype;

    private String event;

    private String pi;

    private List<Property> propertyList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getRcssubtype() {
        return rcssubtype;
    }

    public void setRcssubtype(String rcssubtype) {
        this.rcssubtype = rcssubtype;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", rcssubtype='" + rcssubtype + '\'' +
                ", event='" + event + '\'' +
                ", pi='" + pi + '\'' +
                ", propertyList=" + propertyList +
                '}';
    }
}
