package fileparse.staxparse;


import entity.ProcessBatchQueues;
import common.PropertyUtil;
import entity.IncrementalStg;
import fileparse.saxparse.ParseXMLBySaxThread;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Queue;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 10/11/2019
 */
public class ParseXmlByStaxThread implements Runnable{

    private static Logger logger = Logger.getLogger(ParseXmlByStaxThread.class);

    private String type;
    private String subtype;
    private String rcssubtype;
    private String event;
    private String pi;

    private File file;
    private String uuid;

    private List<Long> propertyIds;

    public ParseXmlByStaxThread() {
    }

    public ParseXmlByStaxThread(File file, String uuid) {
        this.file = file;
        this.uuid = uuid;
    }

    IncrementalStg incrementalStg = null;

    @Override
    public void run() {
        propertyIds = PropertyUtil.getPropertyIds();
        Long time = null;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Reader fileReader = null;
        XMLStreamReader reader = null;
        try {
            fileReader = new FileReader(file);
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            reader = factory.createXMLStreamReader(fileReader);
            int eventReader = reader.getEventType();
            Long propertyId = null;
            while (true) {
                switch (eventReader) {
                    case XMLStreamConstants.START_DOCUMENT:
                        time = System.currentTimeMillis();
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        switch (reader.getLocalName()) {
                            case "entity":
                                type = reader.getAttributeValue(0);

                                subtype = reader.getAttributeValue(1);
                                rcssubtype = reader.getAttributeValue(2);
//                                event = reader.getAttributeValue(3);
                                break;
                            case "PI":
                                pi = reader.getElementText();
                                break;
                            case "property":

                                propertyId = Long.valueOf(reader.getAttributeValue(0));
//                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg = new IncrementalStg();
                                    incrementalStg.setEntity_type(type);
                                    incrementalStg.setEntity_sub_type(subtype);
                                    incrementalStg.setEntity_rcs_sub_type(rcssubtype);
                                    incrementalStg.setEntity_event(event);
                                    incrementalStg.setNda_pi(Long.valueOf(pi));
                                    incrementalStg.setReference_flag("Y");
                                    incrementalStg.setProperty_id(propertyId);
//                                }
                                break;
                            case "currValue":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setCurrent_value(reader.getElementText());
                                }
                                break;
                            case "validFrom":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setValid_from(reader.getElementText());
                                }
                                break;
                            case "validTo":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setValid_to(reader.getElementText());
                                }
                                break;
                            case "validFromWithTime":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setValid_from_inc_time(reader.getElementText());
                                }
                                break;
                            case "validToWithTime":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setValid_to_inc_time(reader.getElementText());
                                }
                                break;
                            case "language":
                                if (propertyIds.contains(propertyId)) {
                                    incrementalStg.setLanguage(reader.getElementText());
                                }
                                break;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (reader.getLocalName().equals("property")) {
//                            if (propertyIds.contains(propertyId)) {
                                ProcessBatchQueues.IncrementalQueue.add(incrementalStg);
                                ProcessBatchQueues.parseNum.getAndIncrement();
//                            }
                        }
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        time = System.currentTimeMillis() - time;
                        logger.info("sum of properties: "+ ProcessBatchQueues.parseNum +"cost time: " + time + "ms");
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
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
                ProcessBatchQueues.IncrementalQueue.add(ParseXMLBySaxThread.getDUMMY());
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
        }
    }

    private void printList(Queue<IncrementalStg> entityList) {
        for (IncrementalStg incrementalStg : entityList) {
            logger.info(incrementalStg.toString());
        }
        logger.info("sum of properties：" + entityList.size());
    }

}
