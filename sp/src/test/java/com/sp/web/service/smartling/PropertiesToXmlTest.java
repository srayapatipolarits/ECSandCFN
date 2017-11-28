package com.sp.web.service.smartling;

import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class PropertiesToXmlTest extends SPTestBase {
  
  @Test
  public void propertiesToXml() throws Exception {
    File file = new File(
        "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/properties/messages.properties");
    InputStream inputStream = new FileInputStream(file);
    Properties props = new Properties();
    props.load(inputStream);
    // where to store?
    OutputStream os = new FileOutputStream(
        "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/xml/messages.xml");
    
    // store the properties detail into a pre-defined XML file
    props.storeToXML(os, "Comment", "UTF-8");
    inputStream.close();
    os.close();
    File xml = new File(
        "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/xml/messages.xml");
    
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder
        .parse("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/xml/messages.xml");
    Node rootNode = doc.getFirstChild();
    
    Document newDocument = docBuilder.newDocument();
    Element createElement = newDocument.createElement("properties");
    newDocument.appendChild(createElement);
    
    Comment commentTranslatePath = doc
        .createComment(" smartling.translate_paths = properties/entry ");
    Comment commentTranslatePathSecond = doc
        .createComment(" smartling.placeholder_format_custom = (mm/dd/yyyy)|(\\[.+?\\])|(\\{.+?\\})|(\\d)|(%)|(-) ");
    doc.insertBefore(commentTranslatePath, rootNode);
    doc.insertBefore(commentTranslatePathSecond, rootNode);
    
    Comment commentTranslatePathNew = newDocument
        .createComment(" smartling.translate_paths = properties/entry ");
    Comment commentTranslatePathSecondNew = newDocument
        .createComment(" smartling.placeholder_format_custom = (mm/dd/yyyy)|(\\[.+?\\])|(\\{.+?\\})|(\\d)|(%)|(-) ");
    newDocument.insertBefore(commentTranslatePathNew, createElement);
    newDocument.insertBefore(commentTranslatePathSecondNew, createElement);
    NodeList entryTag = doc.getElementsByTagName("entry");
    
    if (xml.getName().contains("ogMessages")) {
      for (int i = 0; i < entryTag.getLength(); i++) {
        Node entryNode = entryTag.item(i);
        Element newEntryNode = newDocument.createElement("entry");
        newEntryNode.appendChild(newDocument.createTextNode(entryNode.getTextContent()));
        String attribute = ((Element) entryNode).getAttribute("key");
        if (attribute.contains("url") || attribute.contains("Url")) {
          Node lastChild = createElement.getLastChild();
          if (lastChild != null) {
            String key = ((Element) lastChild).getAttribute("key");
            if (!key.contains("url") || !key.contains("Url")) {
              createElement.insertBefore(newEntryNode, null);
            } else {
              createElement.insertBefore(newEntryNode, lastChild);
            }
          } else {
            createElement.insertBefore(newEntryNode, lastChild);
          }
        } else {
          Node firstChild = createElement.getFirstChild();
          createElement.insertBefore(newEntryNode, firstChild);
        }
        newEntryNode.setAttribute("key", ((Element) entryNode).getAttribute("key"));
      }
      /* add not tranlatable node */
      boolean commentAdded = false;
      Element propertiesNode = newDocument.getDocumentElement();
      NodeList newEntryTag = newDocument.getElementsByTagName("entry");
      for (int i = 0; i < newEntryTag.getLength(); i++) {
        Node entryNode = newEntryTag.item(i);
        String attribute = ((Element) entryNode).getAttribute("key");
        if (attribute.contains("url") || attribute.contains("Url")) {
          
          if (!commentAdded) {
            Comment comentNode = newDocument.createComment(" smartling.sltrans = notranslate ");
            propertiesNode.insertBefore(comentNode, entryNode);
            commentAdded = true;
          }
          
        }
        
      }
      Comment comentNode = newDocument.createComment(" smartling.sltrans = translate ");
      propertiesNode.insertBefore(comentNode, null);
      
    } else if (xml.getName().contains("messages")) {
      for (int i = 0; i < entryTag.getLength(); i++) {
        Node entryNode = entryTag.item(i);
        Element newEntryNode = newDocument.createElement("entry");
        newEntryNode.appendChild(newDocument.createTextNode(entryNode.getTextContent()));
        String attribute = ((Element) entryNode).getAttribute("key");
        if (attribute.contains("video") || attribute.contains("Video")) {
          Node lastChild = createElement.getLastChild();
          if (lastChild != null) {
            String key = ((Element) lastChild).getAttribute("key");
            if (!key.contains("video") || !key.contains("Video")) {
              createElement.insertBefore(newEntryNode, null);
            } else {
              createElement.insertBefore(newEntryNode, lastChild);
            }
          } else {
            createElement.insertBefore(newEntryNode, lastChild);
          }
        } else {
          Node firstChild = createElement.getFirstChild();
          createElement.insertBefore(newEntryNode, firstChild);
        }
        newEntryNode.setAttribute("key", ((Element) entryNode).getAttribute("key"));
      }
      /* add not tranlatable node */
      boolean commentAdded = false;
      Element propertiesNode = newDocument.getDocumentElement();
      NodeList newEntryTag = newDocument.getElementsByTagName("entry");
      for (int i = 0; i < newEntryTag.getLength(); i++) {
        Node entryNode = newEntryTag.item(i);
        if (commentAdded) {
          Comment comentNode = newDocument.createComment(" smartling.sltrans = translate ");
          propertiesNode.insertBefore(comentNode, entryNode);
          commentAdded = false;
        }
        
        String attribute = ((Element) entryNode).getAttribute("key");
        if (attribute.contains("video") || attribute.contains("Video")
            || attribute.contains("dashboardAdmin.defaultImg")) {
          
          if (!commentAdded) {
            Comment comentNode = newDocument.createComment(" smartling.sltrans = notranslate ");
            propertiesNode.insertBefore(comentNode, entryNode);
            commentAdded = true;
          }
          
        }
        
      }
      
    } else {
      for (int i = 0; i < entryTag.getLength(); i++) {
        Node entryNode = entryTag.item(i);
        Element newEntryNode = newDocument.createElement("entry");
        createElement.appendChild(newEntryNode);
        newEntryNode.setAttribute("key", ((Element) entryNode).getAttribute("key"));
        
        String textContent = entryNode.getTextContent();
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode clean = cleaner.clean(textContent);
        TagNode[] pNodes = clean.getElementsByName("p", true);
        for (TagNode pNode : pNodes) {
          Element entry = newDocument.createElement("entry");
          entry.setAttribute("key", "p");
          entry.appendChild(newDocument.createTextNode(pNode.getText().toString()));
          newEntryNode.appendChild(entry);
        }
        
      }
    }
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(newDocument);
    StreamResult result = new StreamResult(new File("messagessNew.xml"));
    transformer.transform(source, result);
    
    System.out.println("Done");
  }
  
  @Test
  public void xmlToProperties() throws Exception {
    
    /* Read all the files */
    File file = new File("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/xml");
    Collection<File> listFiles = FileUtils.listFiles(file, TrueFileFilter.INSTANCE,
        TrueFileFilter.INSTANCE);
    
    /* Iterate through the files */
    listFiles.stream().forEach(
        fl -> {
          
          log.info("File :" + fl.getName());
          if (!fl.getName().startsWith("ogMessage") && !fl.getName().startsWith("relationShip")) {
            
            OrderedProperties props = new OrderedProperties();
            InputStream fs;
            try {
              fs = new FileInputStream(fl);
              DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
              DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
              Document doc = dBuilder.parse(fl);
              doc.getDocumentElement().normalize();
              
              XPath xPath = XPathFactory.newInstance().newXPath();
              
              String expression = "/properties/entry";
              NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc,
                  XPathConstants.NODESET);
              for (int i = 0; i < nodeList.getLength(); i++) {
                Node entryNode = nodeList.item(i);
                
                String key = entryNode.getAttributes().getNamedItem("key").getTextContent();
                
                String value = entryNode.getTextContent();
                if (key.equalsIgnoreCase("notification.subject.EmailNotes")) {
                  log.info("Value is " + value);
                }
                props.setProperty(key, value);
              }
              
              String baseName = FilenameUtils.getBaseName(fl.getName());
              baseName = baseName.replace("-", "_");
              OutputStream os = new FileOutputStream(
                  "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/properties/"
                      + baseName + ".properties");
              
              props.store(new OutputStreamWriter(os, "UTF-8"), "props file");
              fs.close();
              os.close();
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          
          if (fl.getName().startsWith("relationship")) {
            OrderedProperties props = new OrderedProperties();
            try {
              DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
              DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
              Document doc = dBuilder.parse(fl);
              doc.getDocumentElement().normalize();
              
              XPath xPath = XPathFactory.newInstance().newXPath();
              
              String expression = "/properties/entry";
              NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc,
                  XPathConstants.NODESET);
              for (int i = 0; i < nodeList.getLength(); i++) {
                Node entryNode = nodeList.item(i);
                
                String key = entryNode.getAttributes().getNamedItem("key").getTextContent();
                
                StringBuilder builder = new StringBuilder();
                NodeList childNodes = entryNode.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                  String textContent = childNodes.item(j).getTextContent();
                  if (StringUtils.hasText(textContent)) {
                    builder.append("<p>").append(textContent).append("</p>");
                  }
                  
                }
                props.setProperty(key, builder.toString());
              }
              String baseName = FilenameUtils.getBaseName(fl.getName());
              baseName = baseName.replace("-", "_");
              
              OutputStream os = new FileOutputStream(
                  "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/properties/"
                      + baseName + ".properties");
              props.store(new OutputStreamWriter(os, "UTF-8"), "props file");
              os.close();
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
          }
          
        });
    
  }
  
  @Test
  public void testMissingPropertiesFiles() throws Exception{
    
    String string = "/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/properties/messages.properties";
    
    InputStream in = new FileInputStream(new File(string));
    InputStream spanishIn = new FileInputStream(new File("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/shared/messages/tmp/xml/messages.xml"));
    Properties spanishPr = new Properties();
    
    spanishPr.loadFromXML(spanishIn);
    Properties engLis = new Properties();
    engLis.load(in);;
    
    Set<Object> engLish = engLis.keySet();
    
    Set<Object> keySet = spanishPr.keySet();
    
    Collection disjunction = CollectionUtils.disjunction(engLish, keySet);
    
    for(Object disObject : disjunction)
    System.out.println(disObject);
    
    
    
  }
}
