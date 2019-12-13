package edf.xmlparser.service;

public interface RdcFileBatchService {

    void insert(String batchGuid, Integer batchIndex);

    void updateState(String batchGuid, Integer batchIndex, String state);
}
