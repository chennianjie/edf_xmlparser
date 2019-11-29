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

    //这里的3个对象是具体的POJO
    IncrementalStg incrementalStg = null;


    //基于事件流的方式来做的，通过使用流的ＡＰＩ，像指针一样的来处理文档，每一个节点都可以返回一个事件。处理完以后由JVM来回收内存。
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
            int eventReader = reader.getEventType();//获取节点类型,结果是以整形的方式返回的。
            Long propertyId = null;
            while (true) {
                switch (eventReader) {
                    case XMLStreamConstants.START_DOCUMENT://表示的是文档的开通节点。
                        time = System.currentTimeMillis();
                        break;
                    case XMLStreamConstants.START_ELEMENT://开始解析开始节点
                        switch (reader.getLocalName()) {
                            case "entity": //判断节点的名字
                                //给节点赋值
                                type = reader.getAttributeValue(0);//getAttributeValue(index)获取属性的值，可能有多个属性。

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
                    //文档的结束元素
                    case XMLStreamConstants.END_ELEMENT:
                        if (reader.getLocalName().equals("property")) {
//                            if (propertyIds.contains(propertyId)) {
                                ProcessBatchQueues.IncrementalQueue.add(incrementalStg);
                                ProcessBatchQueues.parseNum.getAndIncrement();
//                            }
                        }
                        break;
                    //文档的结束。
                    case XMLStreamConstants.END_DOCUMENT:
                        logger.info("-----------end Document--------");
                        time = System.currentTimeMillis() - time;
                        logger.info("解析property数: "+ ProcessBatchQueues.parseNum +"耗时: " + time + "毫秒");
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
        logger.info("一共解析property：" + entityList.size());
    }

}
