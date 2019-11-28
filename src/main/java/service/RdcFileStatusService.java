package service;

import entity.RdcFileStatus;

public interface RdcFileStatusService {

    void insert(String fileName, String uuid);

    void updateStateByUUId(String state, String uuid);
}
