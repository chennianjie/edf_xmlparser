package edf.xmlparser.entity;

import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 12/13/2019
 */
public class Entity {

    private String type;

    private String subtype;

    private String rcssubtype;

    private String event;

    private Long pi;

    /**
     * 1--invalid   0--valid  default(0)
     */
    private Integer isInvalid = 0;

    public Integer getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Integer isInvalid) {
        this.isInvalid = isInvalid;
    }

    private List<IncrementalStg> propertyList;

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

    public Long getPi() {
        return pi;
    }

    public void setPi(Long pi) {
        this.pi = pi;
    }

    public List<IncrementalStg> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<IncrementalStg> propertyList) {
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
