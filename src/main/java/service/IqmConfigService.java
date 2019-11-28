package service;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/28/2019
 */
public interface IqmConfigService {
    Integer getSequenceNum();

    void update(String categoryCode, Integer fileSequence);
}
