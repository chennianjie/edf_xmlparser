package service;

public interface RdcFileBatchService {

    void insert(String batchGuid, Integer batchIndex);

    void updateState(String batchGuid, String batchIndex, String state);
}
