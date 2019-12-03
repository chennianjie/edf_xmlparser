package service;


import java.sql.SQLException;
import java.text.ParseException;

public interface RdcFileStatusService {

    void insert(String fileName, String uuid) throws ParseException, SQLException;

    void updateStateByUUId(String state, String uuid);
}
