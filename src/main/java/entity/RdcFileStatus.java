package entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public class RdcFileStatus implements Serializable {

    private static final long serialVersionUID = -578076059053523364L;
    private long id;
    private String file_name;
    private String bpm_batch_guid;
    private String file_type;
    private Integer sequence_number;
    private String ingestion_state;
    private Date start_datetime;
    private Date end_datetime;
    private Integer bpm_batch_total;


    public RdcFileStatus() {
    }

    public RdcFileStatus(long id, String file_name, String bpm_batch_guid, String file_type, Integer sequence_number, String ingestion_state, Date start_datetime, Date end_datetime, Integer bpm_batch_total) {
        this.id = id;
        this.file_name = file_name;
        this.bpm_batch_guid = bpm_batch_guid;
        this.file_type = file_type;
        this.sequence_number = sequence_number;
        this.ingestion_state = ingestion_state;
        this.start_datetime = start_datetime;
        this.end_datetime = end_datetime;
        this.bpm_batch_total = bpm_batch_total;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getBpm_batch_guid() {
        return bpm_batch_guid;
    }

    public void setBpm_batch_guid(String bpm_batch_guid) {
        this.bpm_batch_guid = bpm_batch_guid;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public Integer getSequence_number() {
        return sequence_number;
    }

    public void setSequence_number(Integer sequence_number) {
        this.sequence_number = sequence_number;
    }

    public String getIngestion_state() {
        return ingestion_state;
    }

    public void setIngestion_state(String ingestion_state) {
        this.ingestion_state = ingestion_state;
    }

    public Date getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(Date start_datetime) {
        this.start_datetime = start_datetime;
    }

    public Date getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(Date end_datetime) {
        this.end_datetime = end_datetime;
    }

    public Integer getBpm_batch_total() {
        return bpm_batch_total;
    }

    public void setBpm_batch_total(Integer bpm_batch_total) {
        this.bpm_batch_total = bpm_batch_total;
    }

    @Override
    public String toString() {
        return "RdcFileStatus{" +
                "id=" + id +
                ", file_name='" + file_name + '\'' +
                ", bpm_batch_guid='" + bpm_batch_guid + '\'' +
                ", file_type='" + file_type + '\'' +
                ", sequence_number=" + sequence_number +
                ", ingestion_state='" + ingestion_state + '\'' +
                ", start_datetime=" + start_datetime +
                ", end_datetime=" + end_datetime +
                ", bpm_batch_total=" + bpm_batch_total +
                '}';
    }
}
