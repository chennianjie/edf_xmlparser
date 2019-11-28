package fileparse.saxparse;

import entity.ProcessBatchQueues;
import common.PropertyUtil;
import entity.IncrementalStg;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/30/2019
 */
public class ParseXMLBySaxThread implements Runnable{

    private final static IncrementalStg DUMMY = new IncrementalStg();
    public static IncrementalStg getDUMMY() {
        return DUMMY;
    }
    private File file;
    private String uuid;

    public ParseXMLBySaxThread(File file, String uuid) {
        this.file = file;
        this.uuid = uuid;
    }

    @Override
    public void run() {

        try {
            Long start = System.currentTimeMillis();
            // 创建解析器工厂、获取解析器
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            // 创建xml读取器，绑定事件处理器
            XMLReader reader = parser.getXMLReader();
            GetStuInfoHandler stuHandler = new GetStuInfoHandler(file.getName(), uuid);
            reader.setContentHandler(stuHandler);
            reader.parse(file.getAbsolutePath());

            System.out.println("文件解析完成{"+file.getName()+"}" + "====== uuid{"+uuid+"}" + "===== 解析的property数量{" + ProcessBatchQueues.parseNum + "}");
            Long end = System.currentTimeMillis();
            System.out.println("文件解析的时间是(ms)："+ (end - start));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            ProcessBatchQueues.IncrementalQueue.add(DUMMY);
        }

    }

}


/**
 * GetStuInfoHandler2
 * XML analysis
 */
class GetStuInfoHandler extends DefaultHandler {
    public static ArrayList<Long> propertyIds = PropertyUtil.getPropertyIds();
    private String fileName;
    private String uuid;

    private String type;
    private String subtype;
    private String rcssubtype;
    private String event;
    private String pi;
    private final String ENTITY = "entity", PI = "PI",  PROPERTY = "property", CURRVALUE = "currValue",
            VALIDFROM = "validFrom", VALIDFROMWITHTIME = "validFromWithTime", VALIDTO = "validTo", LANGUAGE = "language",
            VALIDTOWITHTIME = "validToWithTime";
    IncrementalStg inc;
    private static String flag;


    public GetStuInfoHandler() {
    }

    public GetStuInfoHandler(String fileName, String uuid) {
        this.fileName = fileName;
        this.uuid = uuid;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 每次解析到一个标签都会触发该方法
        switch (qName) {
            case ENTITY:
                // 获取entity标签内的属性值
                if (attributes != null) {
                    type = attributes.getValue("type");
                    subtype = attributes.getValue("subtype");
                    rcssubtype = attributes.getValue("rcssubtype");
                    event = attributes.getValue("event");
                }
                break;
            case PI:
                flag = PI;
                break;
            case PROPERTY:
                inc = new IncrementalStg();
                inc.setProperty_id(Long.valueOf(attributes.getValue("id")));
                inc.setEntity_type(type);
                inc.setEntity_sub_type(subtype);
                inc.setEntity_rcs_sub_type(rcssubtype);
                inc.setEntity_event(event);
                inc.setNda_pi(Long.valueOf(pi));
                break;
            case CURRVALUE:
                flag = CURRVALUE;
                break;
            case VALIDFROM:
                flag = VALIDFROM;
                break;
            case VALIDFROMWITHTIME:
                flag = VALIDFROMWITHTIME;
                break;
            case VALIDTOWITHTIME:
                flag = VALIDTOWITHTIME;
                break;
            case VALIDTO:
                flag = VALIDTO;
                break;
            case LANGUAGE:
                flag = LANGUAGE;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(flag==null) return;
        switch (flag) {
            case VALIDFROMWITHTIME:
                inc.setValid_from_inc_time(new String(ch, start, length));
                break;
            case VALIDFROM:
                inc.setValid_from(new String(ch, start, length));
                break;
            case CURRVALUE:
                inc.setCurrent_value(new String(ch, start, length));
                break;
            case PI:
                pi = new String(ch, start, length);
                break;
            case VALIDTOWITHTIME:
                inc.setValid_to_inc_time(new String(ch, start, length));
                break;
            case VALIDTO:
                inc.setValid_to(new String(ch, start, length));
                break;
            case LANGUAGE:
                inc.setLanguage(new String(ch, start, length));
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals(PROPERTY)){
            //添加进队列之前，对其property id进行校验
            if (propertyIds.contains(inc.getProperty_id())) {
                inc.setReference_flag("Y");
            }else {
                inc.setReference_flag("N");
            }
            //ParseXMLBySaxThread.pw.println(inc.toString());
            ProcessBatchQueues.IncrementalQueue.add(inc);//使用阻塞队列
            ProcessBatchQueues.parseNum.getAndIncrement();
        }
        flag = null;
    }
}
