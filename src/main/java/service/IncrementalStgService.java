package service;

import entity.IncrementalStg;

import java.util.List;

public interface IncrementalStgService {

    void insert(IncrementalStg incrementalStg);

    void insertByBatch(List<IncrementalStg> list, Integer batchNum, String uuid, String fileName);
}
