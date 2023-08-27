package com.contactcenterlab.xmlpathevaluator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathQueryController {

	private static volatile XPathQueryController instance = null;		
	
	private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
	private DocumentBuilder builder = null;
	private final XPath xPath = XPathFactory.newInstance().newXPath();	
	
	public static XPathQueryController getInstance() {
		if (instance == null) {
			synchronized (XPathQueryController.class) {				
				if (instance == null) {
					instance = new XPathQueryController();
				}
			}
		}
		return instance;
	}
	
	public XPathQueryController() {
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {			
		}
	}
	
	// Run XPath query and get the result in String
	public synchronized String runQuery(String xpathQuery, String xmlDocument) throws XPathExpressionException, SAXException, IOException {
		InputStream inputStream = new ByteArrayInputStream(xmlDocument.getBytes());
		String result = "";		
		try {
			Document document = builder.parse(inputStream);
			result = xPath.evaluate(xpathQuery, document);			
		} finally {
			if (inputStream != null) inputStream.close();			
		}		
		return result;
	}
	
}
