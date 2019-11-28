package entity;

import java.io.Serializable;

public class EntityXml implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2446115081116715310L;
	
	public EntityXml(String file_Name, String uuid, String xml){
		this.fileName = file_Name;
		this.uuid = uuid;
		this.xmlContent = xml;
	}
	
	private String fileName;
	private String xmlContent;
    private String uuid;

	public String getXmlContent() {
		return xmlContent;
	}


	public String getFileName() {
		return fileName;
	}


	public String getUuid() {
		return uuid;
	}


	


	
	

}
