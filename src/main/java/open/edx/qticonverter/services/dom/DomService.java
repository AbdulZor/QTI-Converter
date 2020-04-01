package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.*;
import open.edx.qticonverter.models.qti.manifest.Manifest;
import open.edx.qticonverter.services.CourseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class DomService {
    // Constants
    private final CourseService courseService;
    public static final String PACKAGE_FILES_PATH = "src/main/java/open/edx/qticonverter/files/";
    public static final String XML_EXTENSION = ".xml";
    private static final Logger logger = LoggerFactory.getLogger(DomService.class);
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    // Attributes


    public DomService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void createQtiPackages() {
        List<Course> courses;
        try {
            courses = this.courseService.getCourses();

            for (Course course : courses) {
                File file = new File(PACKAGE_FILES_PATH + course.getId() + "/");
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean directoryIsMade = file.mkdir();

                if (directoryIsMade)
                    createManifestXmlFile(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createQtiPackageForId(String id) {
        Course course;
        try {
            course = this.courseService.getCourseById(id);

            File file = new File(PACKAGE_FILES_PATH + course.getId() + "/");
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean directoryIsMade = file.mkdir();

            if (directoryIsMade) {
                createManifestXmlFile(course);
                createAssessmentItemFiles(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createManifestXmlFile(Course course) {
        Manifest manifest = new Manifest();
        manifest.setMetadata("QTIv2.1 Package", "1.0.0");

        List<String> problemReferences = new ArrayList<>();
        List<Problem> problems = course.getProblems();
        for (Problem problem : problems) {
            problemReferences.add(problem.getFileIdentifier());

            // Add resource (item/problem) to manifest
            manifest.addResource(problem.getId().trim(),
                    "imsqti_item_xmlv2p1",
                    problem.getFileIdentifier() + XML_EXTENSION,
                    null
            );

        }
        // Add assessmentItem resources with optional dependencies
        manifest.addResource(course.getId(),
                "imsqti_test_xmlv2p1",
                course.getName() + "-" + course.getId() + XML_EXTENSION, // we use course because, this is what is used in the assessment part element
                problemReferences);

        try {
            build(manifest.getDocument(), course, "imsmanifest.xml");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void createAssessmentItemFiles(Course course) {
        List<Problem> problems = course.getProblems();
        for (Problem problem : problems) {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            /*
                assessmentItem
                    responseDeclaration
                    outcomeDeclaration
                    outcomeDeclaration

                    itemBody
                        prompt
                        simpleChoice

                    responseProcessing

             */

            Document qtiDocument = documentBuilder.newDocument();
            Node assessmentItemNode = qtiDocument.createElement("assessmentItem");
            Node assessmentItemNod = qtiDocument.appendChild(assessmentItemNode);
            Element assessmentItem = qtiDocument.getDocumentElement();
            assessmentItem.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_v2p1");
            assessmentItem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            assessmentItem.setAttribute("xsi:schemaLocation", "http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd");
            assessmentItem.setAttribute("identifier", problem.getId());
            assessmentItem.setAttribute("title", problem.getName());
            assessmentItem.setAttribute("adaptive", "false");
            assessmentItem.setAttribute("timeDependent", "false");

            // Add the <itemBody> Node to the DOM documentElement
            Element itemBody = qtiDocument.createElement("itemBody");


            //add the response processing of the choicebox question item
            StringBuilder responseProcessingString = new StringBuilder();
            responseProcessingString.append(
                    "<responseProcessing>" +
                            "<responseCondition>\n" +
                            "             <responseIf>\n" +
                            "                <and>\n"
            );

            String problemXMLString = problem.getDefinition().getData();
            logger.info("XML STRING: {}", problemXMLString);

            Document olxDocument = stringToXmlDocument(problemXMLString);

            NodeList childNodesProblem = olxDocument.getDocumentElement().getChildNodes();
            int responseId = 1;
            List<Node> promptList = new ArrayList<>();
            for (int i = 0; i < childNodesProblem.getLength(); i++) {
                Node problemChild = childNodesProblem.item(i);
                logger.info("ProblemChild: " + problemChild.getNodeName());
//                logger.info("ProblemChild Context: " + problemChild.getTextContent());



                switch (problemChild.getNodeName()) {
                    case "label":
                        Element paragraphElement = qtiDocument.createElement("p");
                        paragraphElement.setTextContent(problemChild.getTextContent());
                        promptList.add(paragraphElement);
                        break;
                    case "description":
                        promptList.add(problemChild);
                        break;
                    case "multiplechoiceresponse":
                        buildChoiceBoxItem(qtiDocument, itemBody, problemChild, assessmentItemNode);
                        promptList.clear();
                    case "choiceresponse":
                        buildMultipleChoiceItem(qtiDocument, problemChild, itemBody, promptList, assessmentItemNode, responseProcessingString, responseId++);
                        promptList.clear();
                    default:
                        if (problemChild.getNodeName().equals("p") || problemChild.getNodeName().equals("h3")) {
                            Node importedChoiceRespondChild = qtiDocument.importNode(problemChild, true);
                            promptList.add(importedChoiceRespondChild);
                        }
                }

                for (Node promptListNode : promptList) {
                    logger.info("list node: {}", promptListNode);
                }

                // Also adds the responseDeclaration element of document in the Item build
                boolean choiceresponse = problemChild.getNodeName().equalsIgnoreCase("choiceresponse");
                boolean multipleChoiceResponse = problemXMLString.equalsIgnoreCase("multiplechoiceresponse");
                if (choiceresponse) {
//                    buildMultipleChoiceItem(qtiDocument, problemChild, itemBody, promptList, assessmentItemNode, responseProcessingString, responseId++);
                } else if (multipleChoiceResponse) {
//                    buildChoiceBoxItem(qtiDocument, itemBody, problemChild, assessmentItemNode);
                }
            }

            // Add the <outcomeDeclaration> Nodes to the DOM tree
            createOutcomeDeclarations(problem, qtiDocument, assessmentItemNode);

            // finish the responseProcessingElement
            responseProcessingString.append(
                    "                </and>\n" +
                            "                <setOutcomeValue identifier=\"SCORE\">\n" +
                            "                    <baseValue baseType=\"float\">" + problem.getWeight() + "</baseValue>\n" +
                            "                </setOutcomeValue>\n" +
                            "            </responseIf>\n" +
                            "            <responseElse>\n" +
                            "                <setOutcomeValue identifier=\"SCORE\">\n" +
                            "                    <baseValue baseType=\"float\">0.0</baseValue>\n" +
                            "                </setOutcomeValue>\n" +
                            "            </responseElse>\n" +
                            "</responseCondition>\n" +
                            "</responseProcessing>"
            );

//            logger.info("responseProcessingElement String:\n {}", responseProcessingString);
            Document responseProcessingElement = stringToXmlDocument(responseProcessingString.toString());
            Node importedResponseNode = qtiDocument.importNode(responseProcessingElement.getDocumentElement(), true);

            assessmentItemNode.appendChild(itemBody);
            assessmentItemNode.appendChild(importedResponseNode);

            try {
                build(qtiDocument, course, problem.getFileIdentifier() + XML_EXTENSION);
            } catch (TransformerException e) {
                e.printStackTrace();
            }

        }
    }

    private void createOutcomeDeclarations(Problem problem, Document qtiDocument, Node assessmentItemNode) {
        // Create outcomeDeclaration SCORE
        Element outcomeScoreDeclaration = qtiDocument.createElement("outcomeDeclaration");
        outcomeScoreDeclaration.setAttribute("identifier", "SCORE");
        outcomeScoreDeclaration.setAttribute("cardinality", "single");
        outcomeScoreDeclaration.setAttribute("baseType", "float");

        Element scoreDefaultValue = qtiDocument.createElement("defaultValue");
        Element defaultValue = qtiDocument.createElement("value");
        defaultValue.setTextContent("0"); // initial score per item is 0
        scoreDefaultValue.appendChild(defaultValue);
        outcomeScoreDeclaration.appendChild(scoreDefaultValue);
        assessmentItemNode.appendChild(outcomeScoreDeclaration); //append to assessmentItem

        // Create outcomeDeclaration MAX_SCORE
        Element outcomeMaxDeclaration = qtiDocument.createElement("outcomeDeclaration");
        outcomeMaxDeclaration.setAttribute("identifier", "MAX_SCORE");
        outcomeMaxDeclaration.setAttribute("cardinality", "single");
        outcomeMaxDeclaration.setAttribute("baseType", "float");

        Element maxDefaultValue = qtiDocument.createElement("defaultValue");
        Element defaultValue2 = qtiDocument.createElement("value");
        defaultValue2.setTextContent(String.valueOf(problem.getWeight()));
        maxDefaultValue.appendChild(defaultValue2);
        outcomeMaxDeclaration.appendChild(maxDefaultValue);
        assessmentItemNode.appendChild(outcomeMaxDeclaration); //append to assessmentItem
    }

    private void buildMultipleChoiceItem(Document qtiDocument, Node problemChild, Element itemBody, List<Node> promptList,
                                         Node assessmentItemNode, StringBuilder responseProcessingString, int responseId) {
        // create responseDeclaration element with its child correctResponse
        Element responseDeclaration = qtiDocument.createElement("responseDeclaration");
        String responseIdentifier = "RESPONSE_" + responseId;
        responseDeclaration.setAttribute("identifier", responseIdentifier);
        responseDeclaration.setAttribute("baseType", "identifier");

        Element correctResponse = qtiDocument.createElement("correctResponse");
        responseDeclaration.appendChild(correctResponse);

        // create choiceInteraction element
        Element choiceInteraction = qtiDocument.createElement("choiceInteraction");
        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
        choiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
        choiceInteraction.setAttribute("shuffle", "true");
        itemBody.appendChild(choiceInteraction);

        // Add the <prompt> Node as the first child of choiceInteraction (required)
        Node prompt = qtiDocument.createElement("prompt");

        // If multiple items in problem component, problemList will be used
        for (Node promptNode : promptList) {
            logger.info("node: " + promptNode.getNodeName());
            prompt.appendChild(promptNode);
        }

        choiceInteraction.appendChild(prompt);

        responseProcessingString.append(
                "                    <match>\n" +
                        "                        <variable identifier=\"" + responseIdentifier + "\"/>\n" +
                        "                        <correct identifier=\"" + responseIdentifier + "\"/>\n" +
                        "                    </match>\n"
        );


        // the children of choiceresponse
        NodeList choiceResponseChildren = problemChild.getChildNodes();


        for (int j = 0; j < choiceResponseChildren.getLength(); j++) {

            // the children, such as: label, description ...
            Node choiceResponseChild = choiceResponseChildren.item(j);
//            logger.info("choiceResponseChild value: {} ", choiceResponseChild.getNodeName());
//            logger.info("problemChild.getNodeName(): {}", problemChild.getNodeName());
//            logger.info("problemChild.getNode(): {}", problemChild);

            switch (choiceResponseChild.getNodeName()) {
                case "label":
                    Element paragraphElement = qtiDocument.createElement("p");
                    paragraphElement.setTextContent(prompt.getTextContent().concat(choiceResponseChild.getTextContent()));
                    prompt.appendChild(paragraphElement);
                    break;
                case "description":
                    prompt.setTextContent(prompt.getTextContent() + choiceResponseChild.getTextContent());
                    break;
                case "checkboxgroup":
                    // loop through each choice of the Node "checkboxgroup"
                    NodeList checkboxresponseChildren = choiceResponseChild.getChildNodes();
                    char character = 'A';
                    for (int k = 0; k < checkboxresponseChildren.getLength(); k++) {
                        Node choiceNode = checkboxresponseChildren.item(k);
                        if (choiceNode.getNodeName().equals("choice")) {
                            String correctness = choiceNode.getAttributes().item(0).getTextContent();
                            if (correctness.equals("true")) {
                                Element value = qtiDocument.createElement("value");
                                value.setTextContent(responseId + String.valueOf(character));
                                correctResponse.appendChild(value);
                            }
                            Element simpleChoice = qtiDocument.createElement("simpleChoice");
                            simpleChoice.setAttribute("identifier", responseId + String.valueOf((character++)));
                            simpleChoice.setTextContent(choiceNode.getTextContent());
                            choiceInteraction.appendChild(simpleChoice);
                        }
                    }

                    // if more answers are right, set the "cardinality" attribute of responseDec to multiple
                    // else to single
//                                if (correctResponse.getChildNodes().getLength() > 1) {
                    responseDeclaration.setAttribute("cardinality", "multiple");
//                                } else {
//                                    responseDeclaration.setAttribute("cardinality", "single");
//                                }
                    // if length of correct answers is more than 1 we get a multiple choice so we need to
                    // set the maxChoices to the amount of correct answers that can be selected in an interaction
                    choiceInteraction.setAttribute("maxChoices", "0");
                    break;
                default:
                    if (choiceResponseChild.getNodeName().equals("p") || choiceResponseChild.getNodeName().equals("h3")) {
                        Node importedChoiceRespondChild = qtiDocument.importNode(choiceResponseChild, true);
                        prompt.appendChild(importedChoiceRespondChild);
                    }
            }
        }

        assessmentItemNode.appendChild(responseDeclaration);

    }

    private void buildChoiceBoxItem(Document qtiDocument, Element itemBody, Node problemChild, Node assessmentItemNode) {
        // create responseDeclaration element with its child correctResponse
        Element responseDeclaration = qtiDocument.createElement("responseDeclaration");
        String responseIdentifier = "RESPONSE";
        responseDeclaration.setAttribute("identifier", responseIdentifier);
        responseDeclaration.setAttribute("baseType", "identifier");


        Element correctResponse = qtiDocument.createElement("correctResponse");
        responseDeclaration.appendChild(correctResponse);

        // create choiceInteraction element
        Element choiceInteraction = qtiDocument.createElement("choiceInteraction");
        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
        choiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
        choiceInteraction.setAttribute("shuffle", "true");
        itemBody.appendChild(choiceInteraction);


        // the children of choiceresponse
        NodeList choiceResponseChildren = problemChild.getChildNodes();

        Node prompt = qtiDocument.createElement("prompt");

        for (int j = 0; j < choiceResponseChildren.getLength(); j++) {

            // the children, such as: label, description ...
            Node choiceResponseChild = choiceResponseChildren.item(j);


            switch (choiceResponseChild.getNodeName()) {
                case "label":
                    Element paragraphElement = qtiDocument.createElement("p");
                    paragraphElement.setTextContent(choiceResponseChild.getTextContent());
                    prompt.appendChild(paragraphElement);
                    break;
                case "description":
                    prompt.setTextContent(prompt.getTextContent() + choiceResponseChild.getTextContent());
                    break;
                case "choicegroup":
                    // loop through each choice of the Node "checkboxgroup"
                    NodeList choiceGroupChildren = choiceResponseChild.getChildNodes();
                    char character = 'A';
                    for (int k = 0; k < choiceGroupChildren.getLength(); k++) {
                        Node choiceNode = choiceGroupChildren.item(k);
                        if (choiceNode.getNodeName().equals("choice")) {
                            String correctness = choiceNode.getAttributes().item(0).getTextContent();
                            if (correctness.equals("true")) {
                                Element value = qtiDocument.createElement("value");
                                value.setTextContent(String.valueOf(character));
                                correctResponse.appendChild(value);
                            }
                            Element simpleChoice = qtiDocument.createElement("simpleChoice");
                            simpleChoice.setAttribute("identifier", String.valueOf(character++));
                            simpleChoice.setTextContent(choiceNode.getTextContent());
                            choiceInteraction.appendChild(simpleChoice);
                        }
                    }

                    // if more answers are right, set the "cardinality" attribute of responseDec to multiple
                    // else to single
//                                if (correctResponse.getChildNodes().getLength() > 1) {
                    responseDeclaration.setAttribute("cardinality", "multiple");
//                                } else {
//                                    responseDeclaration.setAttribute("cardinality", "single");
//                                }
                    // if length of correct answers is more than 1 we get a multiple choice so we need to
                    // set the maxChoices to the amount of correct answers that can be selected in an interaction
                    choiceInteraction.setAttribute("maxChoices", "1");
                    break;
                default:
                    if (choiceResponseChild.getNodeName().equals("p") || choiceResponseChild.getNodeName().equals("h3")) {
                        Node importedChoiceRespondChild = qtiDocument.importNode(choiceResponseChild, true);
                        prompt.appendChild(importedChoiceRespondChild);
                    }
            }
        }
        choiceInteraction.appendChild(prompt);
        assessmentItemNode.appendChild(responseDeclaration);

    }

    private static Document stringToXmlDocument(String str) {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document = null;
        try {
            document = docBuilder.parse(new InputSource(new StringReader(str)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return document;
    }

    private void build(Document document, Course course, String fileName) throws TransformerException {
        // write the content into manifest xml file
        // NOTE: if you want the thespart to be something else (sequential ...) you need to move this transform code
        // over to the add* method. This piece of code creates parses the DOM into an XML file
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource manifestSource = new DOMSource(document);
        String problemFilePath = PACKAGE_FILES_PATH + course.getId() + "/" + fileName;
        StreamResult result = new StreamResult(problemFilePath);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(manifestSource, result);
    }
}
