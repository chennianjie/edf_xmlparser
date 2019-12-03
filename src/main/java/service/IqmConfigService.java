package service;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public interface IqmConfigService {
    Integer getSequenceNum();
    Integer getLogLevel();
    void updateSequenceNum(Integer fileSequence);
}
