package edf.xmlparser.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class RdcFileBatch implements Serializable {

    private static final long serialVersionUID = 7830927033742536383L;
    private long id;
    private String bpm_batch_guid;
    private String bp,_batch_index;
    private String ingestion_state;
    private Date create_timestamp;
    private Date modify_tinestam;

    public RdcFileBatch() {
    }

    public RdcFileBatch(long id, String bpm_batch_guid, String bp, String _batch_index, String ingestion_state, Date create_timestamp, Date modify_tinestam) {
        this.id = id;
        this.bpm_batch_guid = bpm_batch_guid;
        this.bp = bp;
        this._batch_index = _batch_index;
        this.ingestion_state = ingestion_state;
        this.create_timestamp = create_timestamp;
        this.modify_tinestam = modify_tinestam;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBpm_batch_guid() {
        return bpm_batch_guid;
    }

    public void setBpm_batch_guid(String bpm_batch_guid) {
        this.bpm_batch_guid = bpm_batch_guid;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String get_batch_index() {
        return _batch_index;
    }

    public void set_batch_index(String _batch_index) {
        this._batch_index = _batch_index;
    }

    public String getIngestion_state() {
        return ingestion_state;
    }

    public void setIngestion_state(String ingestion_state) {
        this.ingestion_state = ingestion_state;
    }

    public Date getCreate_timestamp() {
        return create_timestamp;
    }

    public void setCreate_timestamp(Date create_timestamp) {
        this.create_timestamp = create_timestamp;
    }

    public Date getModify_tinestam() {
        return modify_tinestam;
    }

    public void setModify_tinestam(Date modify_tinestam) {
        this.modify_tinestam = modify_tinestam;
    }

    @Override
    public String toString() {
        return "RdcFileBatch{" +
                "id=" + id +
                ", bpm_batch_guid='" + bpm_batch_guid + '\'' +
                ", bp='" + bp + '\'' +
                ", _batch_index='" + _batch_index + '\'' +
                ", ingestion_state='" + ingestion_state + '\'' +
                ", create_timestamp=" + create_timestamp +
                ", modify_tinestam=" + modify_tinestam +
                '}';
    }
}
