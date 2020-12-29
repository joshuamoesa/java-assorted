package com.joshuamoesa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GetXMLNodePaths {

	public static void main(String[] args) throws Exception {
		
		generateXMLNodeList(removeNamespacePrefixes("./input/SBSVoormeldingNotificatie.xml"));

	}

	private static String getXPath(Node node) {
	    Node parent = node.getParentNode();
	    if (parent == null) {
	        return node.getNodeName();
	    }
	    return getXPath(parent) + "/" + node.getNodeName();
	}
	
	private static String removeNamespacePrefixes(String filePath) {

		File file = new File(filePath);
		String fileName = file.getName().replaceAll("\\..*","").toString();
		
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("./resources/xslt/RemoveNamespacePrefixes.xsl"));
        Transformer transformer = null;
        
		try {
			transformer = factory.newTransformer(xslt);
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Source text = new StreamSource(file);
        try {
			transformer.transform(text, new StreamResult(new File("./output/intermediate/"+ fileName + "_intermediate.xml")));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				

        System.out.println("test: " + transformer.toString());
              
	    return "./output/intermediate/"+ fileName + "_intermediate.xml";
	}

	private static String generateXMLNodeList(String filePath) {

	    File file = new File(filePath);
	    String fileName = file.getName().replaceAll("\\_.*","").toString();
	    
	    XPath xPath =  XPathFactory.newInstance().newXPath();
	    String expression = "//*[not(*)]";

	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = null;
	    
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document document = null;
		try {
			document = (Document) builder.parse(file);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    document.getDocumentElement().normalize();

	    NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    PrintWriter writer = null;
		try {
			writer = new PrintWriter("./output/" + fileName + "_output.xml", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    for(int i = 0 ; i < nodeList.getLength(); i++) {
	        System.out.println(getXPath(nodeList.item(i)));
	        writer.println(getXPath(nodeList.item(i)));
	    }
	    writer.close();		
	    
	    return writer.toString();	    
		
	}
	
}
