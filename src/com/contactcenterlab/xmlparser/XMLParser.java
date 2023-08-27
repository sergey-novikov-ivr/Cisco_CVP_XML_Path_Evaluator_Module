package com.contactcenterlab.xmlparser;

import java.io.IOException;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.audium.server.voiceElement.ElementInterface;
import com.audium.server.voiceElement.Setting;
import com.audium.server.voiceElement.ElementException;
import com.audium.server.voiceElement.ElementData;
import com.audium.server.xml.DecisionElementConfig;
import com.audium.server.voiceElement.ExitState;

import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class XMLParser extends DecisionElementBase implements ElementInterface {
	
	private final String VALUE_NAME = "value";
	private final String EXIT_STATE_DONE = "found";
	private final String EXIT_STATE_NOT_FOUND = "not_found";
	private final String EXIT_STATE_ERROR = "error";
	
	private final String XML_DOCUMENT_SETTING_NAME = "xml_document_query";
	private final String XPATH_QUERY_SETTING_NAME = "xpath_query";
	
	private final String XML_DOCUMENT_DEFAULT_VALUE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n<tag>TEST_EXAMPLE</tag>";
	private final String XPATH_QUERY_DEFAULT_VALUE = "/tag/text()";
	
	private final XPathQueryController xpathController = XPathQueryController.getInstance(); 
	
	// The name of the element
	public final String getElementName() {
		return "XMLParser";
	}
	
	// The name of the folder in Local Elements
	public final String getDisplayFolderName() {
		return "contactcenterlab";
	}

	// Description
	public final String getDescription() {
		return "This a sample of XML parser";
	}
	
	public ExitState[] getExitStates() throws ElementException {
		ExitState found = new ExitState(EXIT_STATE_DONE, EXIT_STATE_DONE, "Normal completion. XML pattern found.");
		ExitState not_found = new ExitState(EXIT_STATE_NOT_FOUND, EXIT_STATE_NOT_FOUND, "XML pattern not found in XML document");
		ExitState error = new ExitState(EXIT_STATE_ERROR, EXIT_STATE_ERROR, "Error");
		return new ExitState[] { found, not_found, error };
	}

	// List of settings
	public final Setting[] getSettings() throws ElementException {
		Setting xml_document = new Setting(XML_DOCUMENT_SETTING_NAME, "XML Document",
				"XML Document",
				true,	// It is required
				true,	// It appears only once
				true,	// It allows substitution
				Setting.TEXTFIELD);
		xml_document.setDefaultValue(XML_DOCUMENT_DEFAULT_VALUE);		
		Setting xpath_query = new Setting(XPATH_QUERY_SETTING_NAME, "XPath Query",
				"XPath Query",
				true,	// It is required
				true,	// It appears only once
				true,	// It allows substitution
				Setting.STRING);
		xpath_query.setDefaultValue(XPATH_QUERY_DEFAULT_VALUE);
		return new Setting[] { xml_document, xpath_query };
	}

	// Get element array
	public final ElementData[] getElementData() throws ElementException {		
		ElementData value = new ElementData(VALUE_NAME, "The result of XML Parsing");
		return new ElementData[] { value };
	}

	// This method performs the action/decision
	@Override
	public String doDecision(String name, DecisionElementData decisionData) throws Exception {
		// Get the configuration
		DecisionElementConfig config = decisionData.getDecisionElementConfig();
		String xmlDocument = config.getSettingValue(XML_DOCUMENT_SETTING_NAME, decisionData);
		String xpathQuery = config.getSettingValue(XPATH_QUERY_SETTING_NAME, decisionData);
		
		try {
			String result = xpathController.runQuery(xpathQuery, xmlDocument);
			if (result == null || result.isEmpty()) {
				decisionData.addToLog("not_found", result);
				return EXIT_STATE_NOT_FOUND;
			}
			decisionData.setElementData(VALUE_NAME, result);		
			decisionData.addToLog("result", result);
			return EXIT_STATE_DONE;
		} catch (IOException | XPathExpressionException | SAXException ex) {
			decisionData.addToLog("error", ex.getMessage());
			return EXIT_STATE_ERROR;
		}
	}	
	
}
