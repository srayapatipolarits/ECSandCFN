package com.sp.web.controller.systemadmin.smartling;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.service.translation.SmartlingJsonGoals;
import com.sp.web.service.translation.SmartlingJsonPersonalityPractice;
import com.sp.web.service.translation.SmartlingJsonTutorial;
import com.sp.web.service.translation.TranslationFactory;
import com.sp.web.service.tutorial.SPTutorialFactoryCache;
import com.sp.web.utils.OrderedProperties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * SPTranslationControllerHelper class will upload the xml files to smartling.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SPTranslationControllerHelper {
  
  private static final String TRANSLATE_PATHS = "translate_paths";

  private static final Logger log = Logger.getLogger(SPTranslationControllerHelper.class);
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private TranslationSystemConnector systemConnector;
  
  @Autowired
  private TranslationFactory translationFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private SPTutorialFactoryCache tutorialFactoryCache;
  
  /**
   * updloadTranslationMethod will upload the translation to the smartling. It will conver the
   * properties file to xml and then will use the smartling transaltion api to upload the xml files
   * to smartling system. params contains any input parameters.
   * 
   * @return the response json.
   */
  public SPResponse uploadTranslations(Object[] params) {
    
    /* get the list of english properties file to be upload for new translation. */
    
    String sharePath = environment.getProperty("sharedPath");
    String propertiesFilePath = sharePath.concat("/messages/properties");
    
    File fileDir = new File(propertiesFilePath);
    Collection<File> listFiles = FileUtils.listFiles(fileDir, TrueFileFilter.INSTANCE,
        TrueFileFilter.INSTANCE);
    
    uploadFiles(sharePath, listFiles);
    
    return new SPResponse().isSuccess();
    
  }
  
  private void uploadFiles(String sharePath, Collection<File> listFiles) {
    /* find the default english files only */
    List<String> skipFiles = new ArrayList<String>();
    skipFiles.add("sp-admin-messages.properties");
    skipFiles.add("sp-registration-messages.properties");
    
    List<File> filterdFiles = listFiles.stream()
        .filter(fl -> !fl.getName().contains("_") && !skipFiles.contains(fl.getName()))
        .collect(Collectors.toList());
    
    if (log.isDebugEnabled()) {
      log.debug("Total files to translated " + filterdFiles.size());
    }
    
    propertiesToXml(filterdFiles, sharePath);
    List<String> filesToBeUpload = listFiles.stream().map(fl -> fl.getName())
        .collect(Collectors.toList());
    /** read all the xml files to be uploaded to smartling. */
    String xmlFilesPath = sharePath + "/messages/tmp/finaluploadxml/";
    File xmlFileDir = new File(xmlFilesPath);
    Collection<File> xmlFiles = FileUtils.listFiles(xmlFileDir, TrueFileFilter.INSTANCE,
        TrueFileFilter.INSTANCE);
    List<File> fileToBeUploaded = xmlFiles.stream()
        .filter(fl -> !filesToBeUpload.contains(fl.getName())).collect(Collectors.toList());
    systemConnector.uploadFiles(fileToBeUploaded);
  }
  
  /**
   * updloadTranslationMethod will upload the translation to the smartling. It will conver the
   * properties file to xml and then will use the smartling transaltion api to upload the xml files
   * to smartling system. params contains any input parameters.
   * 
   * @return the response json.
   */
  public SPResponse uploadTranslation(Object[] params) {
    
    /* get the list of english properties file to be upload for new translation. */
    String fileName = (String) params[1];
    
    String sharePath = environment.getProperty("sharedPath");
    String propertiesFilePath = sharePath
        .concat("/messages/properties/" + fileName + ".properties");
    
    File fileDir = new File(propertiesFilePath);
    
    List<File> fileList = new ArrayList<File>();
    fileList.add(fileDir);
    uploadFiles(sharePath, fileList);
    return new SPResponse().isSuccess();
    
  }
  
  /**
   * propertiesToXml method will upload the
   * 
   * @param propsFile
   *          properties file.
   * @param filesToBeUploaded
   *          files.
   * @param sharePath
   */
  private void propertiesToXml(List<File> propsFile, String sharePath) {
    
    /* iterate throught the properties files */
    for (File propFile : propsFile) {
      FileInputStream inputStream = null;
      try {
        inputStream = FileUtils.openInputStream(propFile);
        Properties props = new Properties();
        props.load(inputStream);
        String fileName = FilenameUtils.removeExtension(propFile.getName());
        File file = new File(sharePath + "/messages/tmp/xml/" + fileName + ".xml");
        if (!file.exists()) {
          file.createNewFile();
        }
        OutputStream os = new FileOutputStream(file);
        
        // store the properties detail into a pre-defined XML file
        props.storeToXML(os, "Comment", "UTF-8");
        inputStream.close();
        os.close();
        File xml = new File(sharePath + "/messages/tmp/xml/" + fileName + ".xml");
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(sharePath + "/messages/tmp/xml/" + fileName + ".xml");
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
          
        } else if (xml.getName().contains("relationship")) {
          for (int i = 0; i < entryTag.getLength(); i++) {
            Node entryNode = entryTag.item(i);
            Element newEntryNode = newDocument.createElement("entry");
            createElement.appendChild(newEntryNode);
            newEntryNode.setAttribute("key", ((Element) entryNode).getAttribute("key"));
            
            String textContent = entryNode.getTextContent();
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode clean = cleaner.clean(textContent);
            TagNode[] pNodes = clean.getElementsByName("p", true);
            if(pNodes.length >0){
              for (TagNode pNode : pNodes) {
                Element entry = newDocument.createElement("entry");
                entry.setAttribute("key", "p");
                entry.appendChild(newDocument.createTextNode(pNode.getText().toString()));
                newEntryNode.appendChild(entry);
              }  
            }else{
              Element entry = newDocument.createElement("entry");
              entry.setAttribute("key", "title");
              entry.appendChild(newDocument.createTextNode(textContent));
              newEntryNode.appendChild(entry);
            }
            
          }
        } else {
          for (int i = 0; i < entryTag.getLength(); i++) {
            Node entryNode = entryTag.item(i);
            Element newEntryNode = newDocument.createElement("entry");
            createElement.appendChild(newEntryNode);
            newEntryNode.setAttribute("key", ((Element) entryNode).getAttribute("key"));
            newEntryNode.setTextContent(entryNode.getTextContent());
          }
        }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(newDocument);
        StreamResult result = new StreamResult(new File(sharePath + "/messages/tmp/finaluploadxml/"
            + fileName + ".xml"));
        transformer.transform(source, result);
        
      } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
        log.error("Error occurred while loading the props file " + propFile.getName(), e);
        continue;
      }
      
    }
    
  }
  
  public SPResponse downloadFiles(Object[] param) {
    
    String fileToBeDownloaded = (String) param[0];
    
    String locale = (String) param[1];
    String sharePath = environment.getProperty("sharedPath");
    
    String downloadFiles;
    
    String baseName = FilenameUtils.getBaseName(fileToBeDownloaded);
    
    String localeName = locale.replace("-", "_");
    baseName = baseName.concat("_" + localeName);
    
    try {
      if (fileToBeDownloaded.contains("PracticeArea") || fileToBeDownloaded.contains("sp-tutorial")
          || fileToBeDownloaded.contains("tutorial-steps")
          || fileToBeDownloaded.contains("sp-tutorial.json")) {
        downloadFiles = systemConnector.downloadFiles(fileToBeDownloaded, locale, "json");
        File file = new File(sharePath + "/messages/" + baseName + ".json");
        file.createNewFile();
        FileUtils.writeByteArrayToFile(file, downloadFiles.getBytes());
        
      } else {
        downloadFiles = systemConnector.downloadFiles(fileToBeDownloaded, locale, "xml");
        InputSource inputSource = null;
        inputSource = new InputSource(new ByteArrayInputStream(downloadFiles.getBytes("UTF-8")));
        if (!fileToBeDownloaded.startsWith("relationship")) {
          
          OrderedProperties props = new OrderedProperties();
          
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
          Document doc = dbuilder.parse(inputSource);
          doc.getDocumentElement().normalize();
          
          XPath xpath = XPathFactory.newInstance().newXPath();
          
          String expression = "/properties/entry";
          NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(doc,
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
          
          OutputStream os = new FileOutputStream(sharePath + "/messages/properties/" + baseName
              + ".properties");
          
          props.store(new OutputStreamWriter(os, "UTF-8"), "props file");
          os.close();
        }
        
        if (fileToBeDownloaded.startsWith("relationship")) {
          OrderedProperties props = new OrderedProperties();
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
          Document doc = dbuilder.parse(inputSource);
          doc.getDocumentElement().normalize();
          
          XPath xpath = XPathFactory.newInstance().newXPath();
          
          String expression = "/properties/entry";
          NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(doc,
              XPathConstants.NODESET);
          for (int i = 0; i < nodeList.getLength(); i++) {
            Node entryNode = nodeList.item(i);
            
            String key = entryNode.getAttributes().getNamedItem("key").getTextContent();
            
            StringBuilder builder = new StringBuilder();
            NodeList childNodes = entryNode.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
              String textContent = childNodes.item(j).getTextContent();
              if (StringUtils.hasText(textContent)) {
                if (key.contains("title")) {
                  builder.append(textContent);
                } else {
                  builder.append("<p>").append(textContent).append("</p>");
                }
                
              }
            }
            props.setProperty(key, builder.toString());
          }
          
          OutputStream os = new FileOutputStream(sharePath + "/messages/properties/" + baseName
              + ".properties");
          props.store(new OutputStreamWriter(os, "UTF-8"), "props file");
          os.close();
        }
        
        /* not trigger the jenkins job to copy the files back to git */
        
      }
    } catch (Exception e) {
      log.error("error occurred while converting file to properties files" + fileToBeDownloaded, e);
      throw new SPException("error occurred while donwloading file from smartling", e);
    }
    /* Trigger the hudeson job */
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to create the translation json.
   * 
   * @return
   *    - the translation json for tutorials
   */
  public SmartlingJsonTutorial createTutorialJson() {
    SmartlingJsonTutorial tutorial = new SmartlingJsonTutorial();
    Map<Object, Object> smartling = tutorial.getSmartling();
    List<Map<Object, Object>> object = new ArrayList<Map<Object, Object>>();
    smartling.put(TRANSLATE_PATHS, object);
    
    object.add(getPathKeyMap("/{*}/id", "*/name"));
    object.add(getPathKeyMap("/{*}/id,description", "*/description"));
    
    tutorial.setData(tutorialFactoryCache.getAll());
    return tutorial;
  }

  private Map<Object, Object> getPathKeyMap(final String key, final String path) {
    Map<Object, Object> paths = new HashMap<Object, Object>();
    paths.put("path", path);
    paths.put("key", key);
    return paths;
  }
  
  /**
   * createJsonDataTranslableSource method wil create the json for the practice area.
   */
  public SmartlingJsonGoals createJsonDataTranslableSource() {
    
    SmartlingJsonGoals response = new SmartlingJsonGoals();
    @SuppressWarnings("unchecked")
    List<Map<Object, Object>> object = (List<Map<Object, Object>>) response.getSmartling().get(
        TRANSLATE_PATHS);
    if (object == null) {
      object = new ArrayList<Map<Object, Object>>();
      response.getSmartling().put(TRANSLATE_PATHS, object);
    }
    
    Map<Object, Object> paths = new HashMap<Object, Object>();
    
    paths.put("path", "*/name");
    paths.put("key", "/{*}/id");
    
    Map<Object, Object> paths1 = new HashMap<Object, Object>();
    
    paths1.put("path", "/*/description");
    paths1.put("key", "/{*}/id,*/name");
    
    Map<Object, Object> paths2 = new HashMap<Object, Object>();
    
    paths2.put("path", "/*/dsDescription");
    paths2.put("key", "/data/{*}/developmentStrategyList/{*}/id");
    
    Map<Object, Object> paths3 = new HashMap<Object, Object>();
    
    paths3.put("path", "/*/name");
    paths3.put("key", "/{*}/id");
    
    object.add(paths);
    object.add(paths1);
    object.add(paths2);
    object.add(paths3);
    
    object.add(getPathKeyMap("/data/{*}/devStrategyActionCategoryList/actionList/{*}/id",
        "/*/actionList/title"));
    object.add(getPathKeyMap("/data/{*}/devStrategyActionCategoryList/actionList/{*}/id,description",
        "/*/actionList/description"));
    object.add(getPathKeyMap("/data/{*}/devStrategyActionCategoryList/actionList/actionData/{*}/id",
        "/*/actionList/actionData/linkText"));
    
    List<SPGoal> goals = goalsRepository.findAllGoalsByCategory(GoalCategory.Business,
        GoalCategory.Individual, GoalCategory.GrowthAreas, GoalCategory.Tutorial);
    
    response.setData(goals);
    return response;
  }
  
  /**
   * createJsonDataTranslableSource method wil create the json for the practice area.
   */
  public SmartlingJsonPersonalityPractice createJsonDataTranslableSourcePersonality() {
    
    SmartlingJsonPersonalityPractice response = new SmartlingJsonPersonalityPractice();
    @SuppressWarnings("unchecked")
    List<Map<Object, Object>> object = (List<Map<Object, Object>>) response.getSmartling().get(
        TRANSLATE_PATHS);
    if (object == null) {
      object = new ArrayList<Map<Object, Object>>();
      response.getSmartling().put(TRANSLATE_PATHS, object);
    }
    
    Map<Object, Object> paths1 = new HashMap<Object, Object>();
    
    paths1.put("path", "/*/Opportunities");
    paths1.put("key", "/{*}/id,*/personalityType,/*/Opportunities");
    
    Map<Object, Object> paths2 = new HashMap<Object, Object>();
    
    paths2.put("path", "/*/Threats");
    paths2.put("key", "/{*}/id,*/personalityType,/*/Threats");
    
    Map<Object, Object> paths3 = new HashMap<Object, Object>();
    
    paths3.put("path", "/*/Weakness");
    paths3.put("key", "/{*}/id,*/personalityType, /*/Weakness");
    
    Map<Object, Object> paths4 = new HashMap<Object, Object>();
    
    paths4.put("path", "/*/Strengths");
    paths4.put("key", "/{*}/id,*/personalityType,/*/Strengths");
    
    object.add(paths4);
    object.add(paths1);
    object.add(paths2);
    object.add(paths3);
    List<PersonalityPracticeArea> goals = goalsRepository.findAllPersonalityPracticeAreas();
    
    response.setData(goals);
    return response;
  }
  
  /**
   * updatePracticeAreaJson method will update the the translation data back into the mongo
   * database.
   * 
   * @param user
   *          is the system admin user who will upload the json file.
   * @param params
   *          contains the file
   * @return the
   * @throws IOException
   */
  public SPResponse uploadPracticeAreaJson(User user, Object[] params) {
    MultipartFile practiceAreaJson = (MultipartFile) params[0];
    MultipartFile spanishPracticeAreaJson = (MultipartFile) params[1];
    String locale = (String) params[2];
    InputStream inputStream;
    InputStream spanishInputStream;
    try {
      spanishInputStream = spanishPracticeAreaJson.getInputStream();
      inputStream = practiceAreaJson.getInputStream();
      
      updatePracticeAreaJson(locale, inputStream, spanishInputStream);
      
    } catch (IOException e) {
      log.error("Eerror occurred while updated the translated content ", e);
    }
    
    return new SPResponse().isSuccess();
    
  }
  
  private void updatePracticeAreaJson(String locale, InputStream inputStream,
      InputStream spanishInputStream) throws IOException, JsonParseException, JsonMappingException {
    ObjectMapper englishMapper = new ObjectMapper();
    englishMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SmartlingJsonGoals english = englishMapper.readValue(inputStream, SmartlingJsonGoals.class);
    List<SPGoal> data2 = (List<SPGoal>) english.getData();
    Map<String, SPGoal> englishGoals = data2.stream().collect(
        Collectors.toMap(SPGoal::getName, Function.identity()));
    
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SmartlingJsonGoals readValue = objectMapper.readValue(spanishInputStream,
        SmartlingJsonGoals.class);
    
    List<SPGoal> data = (List<SPGoal>) readValue.getData();
    Map<String, SPGoal> spanishGoal = data.stream().collect(
        Collectors.toMap(SPGoal::getId, Function.identity()));
    
    /* udpate the id for the data */
    Set<String> keySet = englishGoals.keySet();
    List<SPGoal> goals = goalsRepository.findAllGoalsByCategory(GoalCategory.Business,
        GoalCategory.Individual, GoalCategory.GrowthAreas, GoalCategory.Tutorial);
    for (SPGoal goalOrg : goals) {
      if(keySet.contains(goalOrg.getName())){
        log.info("matches");
      }else{
        for(String key : keySet){
          
          log.info("key:" + key + ", goalOrg.getn" + goalOrg.getName() + ", " + key.equalsIgnoreCase(goalOrg.getName()));
          
        }
      }
      SPGoal spGoal = englishGoals.get(goalOrg.getName());
      
      if (spGoal == null) {
        log.info("Goal not present in environemnt for translation " + goalOrg.getName() + ", id:"
            + goalOrg.getId());
        continue;
      }
      
      SPGoal spaishGoal = spanishGoal.get(spGoal.getId());
      /* update the id */
      spaishGoal.setId(goalOrg.getId());
    }
    Collection<SPGoal> spanishMappedData = spanishGoal.values();
    
    translationFactory.updateTranslationData(spanishMappedData, locale, "goals");
  }
  
  public SPResponse updatePracticeAreaJsonMongo(Object[] param) {
    
    String sharePath = environment.getProperty("sharedPath");
    
    File englishPracticeArea = new File(sharePath + "/messages/PracticeArea_en_US.json");
    File spanish = new File(sharePath + "/messages/PracticeArea_es_LA.json");
    
    try {
      InputStream inputStream = new FileInputStream(englishPracticeArea);
      InputStream spanishInputStream = new FileInputStream(spanish);
      updatePracticeAreaJson("es_LA", inputStream, spanishInputStream);
    } catch (Exception e) {
      log.error("error occurred", e);
    }
    
    return new SPResponse().isSuccess();
  }
  
 public SPResponse updateTutoriaToMongo(Object[] param) {
    
    String sharePath = environment.getProperty("sharedPath");
    
    File englishPracticeArea = new File(sharePath + "/messages/tutorial-steps_en_US.json");
    File spanish = new File(sharePath + "/messages/tutorial-steps_es_LA.json");
    
    try {
      InputStream inputStream = new FileInputStream(englishPracticeArea);
      InputStream spanishInputStream = new FileInputStream(spanish);
      updatePracticeAreaJson("es_LA", inputStream, spanishInputStream);
    } catch (Exception e) {
      log.error("error occurred", e);
    }
    
    return new SPResponse().isSuccess();
  }
  
  public SPResponse updatePersonalityPracticeAreaJsonMongo(Object[] param) {
    
    String sharePath = environment.getProperty("sharedPath");
    
    File englishPracticeArea = new File(sharePath + "/messages/personalityPracticeArea_en_US.json");
    File spanish = new File(sharePath + "/messages/personalityPracticeArea_es_LA.json");
    
    try {
      InputStream inputStream = new FileInputStream(englishPracticeArea);
      InputStream spanishInputStream = new FileInputStream(spanish);
      updatePersonalityPracticeAreaJson("es_LA", inputStream, spanishInputStream);
    } catch (Exception e) {
      log.error("error occurred", e);
    }
    
    return new SPResponse().isSuccess();
  }
  
  private void updatePersonalityPracticeAreaJson(String locale, InputStream inputStream,
      InputStream spanishInputStream) throws IOException, JsonParseException, JsonMappingException {
    ObjectMapper englishMapper = new ObjectMapper();
    englishMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    SmartlingJsonPersonalityPractice english = englishMapper.readValue(inputStream,
        SmartlingJsonPersonalityPractice.class);
    List<PersonalityPracticeArea> data2 = (List<PersonalityPracticeArea>) english.getData();
    Map<PersonalityType, PersonalityPracticeArea> englishGoals = data2.stream().collect(
        Collectors.toMap(PersonalityPracticeArea::getPersonalityType, Function.identity()));
    
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SmartlingJsonPersonalityPractice readValue = objectMapper.readValue(spanishInputStream,
        SmartlingJsonPersonalityPractice.class);
    
    List<PersonalityPracticeArea> data = (List<PersonalityPracticeArea>) readValue.getData();
    Map<PersonalityType, PersonalityPracticeArea> spanishGoal = data.stream().collect(
        Collectors.toMap(PersonalityPracticeArea::getPersonalityType, Function.identity()));
    
    /* udpate the id for the data */
    
    List<PersonalityPracticeArea> goals = goalsRepository.findAllPersonalityPracticeAreas();
    for (PersonalityPracticeArea goalOrg : goals) {
      
      PersonalityPracticeArea spGoal = englishGoals.get(goalOrg.getPersonalityType());
      
      if (spGoal == null) {
        log.info("Personality not present in environemnt for translation " + goalOrg.getId()
            + ", id:" + goalOrg.getId());
        continue;
      }
      
      PersonalityPracticeArea spaishGoal = spanishGoal.get(spGoal.getPersonalityType());
      /* update the id */
      spaishGoal.setId(goalOrg.getId());
    }
    Collection<PersonalityPracticeArea> spanishMappedData = spanishGoal.values();
    
    translationFactory.updateTranslationData(spanishMappedData, locale, "PersonalityPracticeArea");
  }
  
  /**
   * Helper method to create the tutorial translations.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the generate request
   */
  public SPResponse updateTutorial(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String locale = (String) params[0];
    
    String sharePath = environment.getProperty("sharedPath");
    
    File engTutorialFile = new File(sharePath + "/messages/sp-tutorial_en_US.json");
    File translatedTutorialFile = new File(sharePath + "/messages/sp-tutorial_" + locale + ".json");
    
    try {
      InputStream inputStream = new FileInputStream(engTutorialFile);
      InputStream spanishInputStream = new FileInputStream(translatedTutorialFile);
      updateSPTutorial(locale, inputStream, spanishInputStream);
      resp.isSuccess();
    } catch (Exception e) {
      log.error("error occurred", e);
      resp.addError(new SPException(e));
    }
    
    return resp;
  }

  /**
   * Update the translated tutorial data into the db.
   * 
   * @param locale
   *          - locale
   * @param inputStream
   *          - master input stream
   * @param translatedInputStream
   *          - translated input stream
   * @throws Exception
   *          - error performing the operation
   */
  private void updateSPTutorial(String locale, InputStream inputStream,
      InputStream translatedInputStream) throws Exception {
    ObjectMapper om = new ObjectMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SmartlingJsonTutorial english = om.readValue(inputStream, SmartlingJsonTutorial.class);
    List<SPTutorial> data2 = (List<SPTutorial>) english.getData();
    Map<String, SPTutorial> englishTutorialMap = data2.stream().collect(
        Collectors.toMap(SPTutorial::getName, Function.identity()));
    
    SmartlingJsonTutorial readValue = om.readValue(translatedInputStream, SmartlingJsonTutorial.class);
    
    List<SPTutorial> data = (List<SPTutorial>) readValue.getData();
    Map<String, SPTutorial> spanishTutorialMap = data.stream().collect(
        Collectors.toMap(SPTutorial::getId, Function.identity()));
    
    /* udpate the id for the data */
    
    List<SPTutorial> tutorialList = tutorialFactoryCache.getAll();
    for (SPTutorial tutorialInDB : tutorialList) {
      
      SPTutorial engTutorial = englishTutorialMap.get(tutorialInDB.getName());
      
      if (engTutorial == null) {
        log.info("Tutorial not present in environemnt for translation " + tutorialInDB.getName() + ", id:"
            + tutorialInDB.getId());
        continue;
      }
      
      SPTutorial spaishTutorial = spanishTutorialMap.get(engTutorial.getId());
      /* update the id */
      spaishTutorial.setId(tutorialInDB.getId());
    }
    Collection<SPTutorial> spanishMappedData = spanishTutorialMap.values();
    translationFactory.updateTranslationData(spanishMappedData, locale, "tutorial");
  }
  
}
