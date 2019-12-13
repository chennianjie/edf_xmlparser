package edf.xmlparser.service;

import edf.xmlparser.entity.Entity;
import edf.xmlparser.entity.IncrementalStg;

import java.util.List;

public interface IncrementalStgService {

    void insert(IncrementalStg incrementalStg);

    void insertByBatch(List<Entity> list, Integer batchNum, String uuid, String fileName);
}
