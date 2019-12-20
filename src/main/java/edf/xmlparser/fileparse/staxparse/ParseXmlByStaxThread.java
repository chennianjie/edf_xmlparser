package edf.xmlparser.fileparse.staxparse;


import edf.xmlparser.common.IQMLogUtil;
import edf.xmlparser.common.OracleConnection;
import edf.xmlparser.entity.Entity;
import edf.xmlparser.entity.ProcessBatchQueues;
import edf.xmlparser.common.PropertyUtil;
import edf.xmlparser.entity.IncrementalStg;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 10/11/2019
 */
public class ParseXmlByStaxThread implements Runnable{

    private static Logger logger = Logger.getLogger(ParseXmlByStaxThread.class);
    private IQMLogUtil iqmLogUtil = IQMLogUtil.getSingleton();

    private String type;
    private String subtype;
    private String rcssubtype;
    private String event;
    private String pi;
    private File file;
    private String uuid;
    private List<Long> propertyIds;
    private final static Entity DUMMY = new Entity();
    public static Entity getDUMMY() {
        return DUMMY;
    }


    public ParseXmlByStaxThread() {
    }

    public ParseXmlByStaxThread(File file, String uuid) {
        this.file = file;
        this.uuid = uuid;
    }

    private IncrementalStg incrementalStg = null;
    private Entity entity = null;
    private List<IncrementalStg> propertyList = null;

    @Override
    public void run() {
        propertyIds = PropertyUtil.getPropertyIds();
        Long time = null;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Reader fileReader;
        XMLStreamReader reader = null;
        Long propertyId = null;
        try {
            fileReader = new FileReader(file);
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            reader = factory.createXMLStreamReader(fileReader);
            int eventReader = reader.getEventType();
            while (true) {
                switch (eventReader) {
                    case XMLStreamConstants.START_DOCUMENT:
                        time = System.currentTimeMillis();
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        switch (reader.getLocalName()) {
                            case "entity":
                                entity = new Entity();
                                propertyList = new ArrayList<>();
                                type = reader.getAttributeValue(0);
                                subtype = reader.getAttributeValue(1);
                                rcssubtype = reader.getAttributeValue(2);
                                event = reader.getAttributeValue(3);
                                entity.setType(type);
                                entity.setSubtype(subtype);
                                entity.setRcssubtype(rcssubtype);
                                entity.setEvent(event);
                                break;
                            case "PI":
                                pi = reader.getElementText();
                                try {
                                    entity.setPi(Long.valueOf(pi));
                                } catch (NumberFormatException e){
                                    iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "NumberFormatException",
                                            "FileName:" + file.getName() +"  || UUID:" + uuid + " || PI:" + pi + " || Property_id:" + propertyId + " || exception:" + e.getMessage(),
                                            new Date(System.currentTimeMillis()));
                                    e.printStackTrace();
                                    entity.setIsInvalid(1);
                                }
                                break;
                            case "property":
                                incrementalStg = new IncrementalStg();

                                try {
                                    propertyId = Long.valueOf(reader.getAttributeValue(0));
//                                   if (propertyIds.contains(propertyId)) {
                                        incrementalStg.setProperty_id(propertyId);
//                                  }
                                } catch (NumberFormatException e){
                                    iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "NumberFormatException",
                                            "FileName:" + file.getName() +"  || UUID:" + uuid + " || PI:" + pi + " || Property_id:" + propertyId + " || exception:" + e.getMessage(),
                                            new Date(System.currentTimeMillis()));
                                    e.printStackTrace();
                                    incrementalStg.setIsInvalid(1);
                                }
                                break;
                            case "currValue":
                                    incrementalStg.setCurrent_value(reader.getElementText());
                                break;
                            case "validFrom":
                                    incrementalStg.setValid_from(reader.getElementText());
                                break;
                            case "validTo":
                                    incrementalStg.setValid_to(reader.getElementText());
                                break;
                            case "validFromWithTime":
                                    incrementalStg.setValid_from_inc_time(reader.getElementText());
                                break;
                            case "validToWithTime":
                                    incrementalStg.setValid_to_inc_time(reader.getElementText());
                                break;
                            case "language":
                                    incrementalStg.setLanguage(reader.getElementText());
                                break;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (reader.getLocalName().equals("property") && incrementalStg.getIsInvalid() != 1) {
//                            if (propertyIds.contains(propertyId)) {
                                propertyList.add(incrementalStg);

//                            }
                            ProcessBatchQueues.parsePropertyNum.getAndIncrement();
                        }else if(reader.getLocalName().equals("entity") && entity.getIsInvalid() != 1) {
                            entity.setPropertyList(propertyList);
                            ProcessBatchQueues.EntityQueue.add(entity);
                            ProcessBatchQueues.parseEntityNum.getAndIncrement();
                        }
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        time = System.currentTimeMillis() - time;
                        logger.info("=========parse file end{}"+ file.getName() + " || cost time:" + time + "ms=========");
                        logger.info("parse entity nums：" + ProcessBatchQueues.parseEntityNum);
                        logger.info("parse property nums：" + ProcessBatchQueues.parsePropertyNum);
                        break;
                }

                try {
                    if (!reader.hasNext()) {
                        break;
                    }
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
                try {
                    eventReader = reader.next();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "FileNotFoundException",
                    "FilePath:" + file.getPath() +"  || UUID:" + uuid,
                    new Date(System.currentTimeMillis()));
            e.printStackTrace();
        //eventType!=1 throw XMLStreamException
        } catch (XMLStreamException e) {
            iqmLogUtil.logging("ERROR", OracleConnection.getUser(), "XMLStreamException",
                    "FileName:" + file.getName() +"  || UUID:" + uuid,
                    new Date(System.currentTimeMillis()));
            e.printStackTrace();
        } finally {
                ProcessBatchQueues.EntityQueue.add(DUMMY);
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
        }
    }
}
