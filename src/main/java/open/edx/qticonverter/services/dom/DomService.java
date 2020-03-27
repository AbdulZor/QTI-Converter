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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DomService {
    // Constants
    private final CourseService courseService;
    public static final String PACKAGE_FILES_PATH = "src/main/java/open/edx/qticonverter/files/";
    public static final String XML_EXTENSION = ".xml";
    private static final Logger logger = LoggerFactory.getLogger(DomService.class);
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    // Attributes
    private List<Course> courses;

    public DomService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void createQtiPackages() {
        try {
            this.courses = this.courseService.getCourses();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Course course : this.courses) {
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
    }

    private void createManifestXmlFile(Course course) {
        Manifest manifest = new Manifest();
        manifest.setMetadata("QTIv2.1 Package", "1.0.0");

        List<String> problemReferences = new ArrayList<>();
        List<Problem> problems = course.getProblems();
        for (Problem problem : problems) {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = documentBuilder.newDocument();
            Node assessmentItemNode = doc.createElement("assessmentItem");
            Node assessmentItemNod = doc.appendChild(assessmentItemNode);
            Element assessmentItem = doc.getDocumentElement();
            assessmentItem.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_v2p1");
            assessmentItem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            assessmentItem.setAttribute("xsi:schemaLocation", "http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd");
            assessmentItem.setAttribute("identifier", problem.getId());
            assessmentItem.setAttribute("title", problem.getName());
            assessmentItem.setAttribute("adaptive", "false");
            assessmentItem.setAttribute("timeDependent", "false");

            if (problem.getProblemType().equals(ProblemType.ChoiceInteraction) || problem.getProblemType().equals(ProblemType.MultipleChoiceInteraction)) {
                buildMultipleChoiceItem(doc, problem, assessmentItemNode);
            }


            try {
                build(doc, course, problem.getFileIdentifier());
            } catch (TransformerException e) {
                e.printStackTrace();
            }

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

    private Document buildMultipleChoiceItem(Document doc, Problem problem, Node assessmentItemNode) {
        // create responseDeclaration element with its child correctResponse
        Element responseDeclaration = doc.createElement("responseDeclaration");
        String responseIdentifier = "RESPONSE";
        responseDeclaration.setAttribute("identifier", responseIdentifier);
        responseDeclaration.setAttribute("baseType", "identifier");

        // Create outcomeDeclaration SCORE
        Element outcomeScoreDeclaration = doc.createElement("choiceDeclaration");
        outcomeScoreDeclaration.setAttribute("identifier", "SCORE");
        outcomeScoreDeclaration.setAttribute("cardinality", "single");
        outcomeScoreDeclaration.setAttribute("baseType", "float");

        Element scoreDefaultValue = doc.createElement("defaultValue");
        Element defaultValue = doc.createElement("value");
        defaultValue.setAttribute("baseType", "float");
        scoreDefaultValue.appendChild(defaultValue);
        scoreDefaultValue.setTextContent("0"); // initial score per item is 0
        outcomeScoreDeclaration.appendChild(scoreDefaultValue);
        assessmentItemNode.appendChild(outcomeScoreDeclaration); //append to assessmentItem

        // Create outcomeDeclaration MAX_SCORE
        Element outcomeMaxDeclaration = doc.createElement("choiceDeclaration");
        outcomeMaxDeclaration.setAttribute("identifier", "MAX_SCORE");
        outcomeMaxDeclaration.setAttribute("cardinality", "single");
        outcomeMaxDeclaration.setAttribute("baseType", "float");

        Element maxDefaultValue = doc.createElement("defaultValue");
        Element defaultValue2 = doc.createElement("value");
        defaultValue2.setAttribute("baseType", "float");
        maxDefaultValue.appendChild(defaultValue2);
        maxDefaultValue.setTextContent(String.valueOf(problem.getWeight()));
        outcomeMaxDeclaration.appendChild(maxDefaultValue);
        assessmentItemNode.appendChild(outcomeMaxDeclaration); //append to assessmentItem

        // setup itemBody element
        Element itemBody = doc.createElement("itemBody");
        Element responseProcessing = doc.createElement("responseProcessing");

        Element correctResponse = doc.createElement("correctResponse");
        responseDeclaration.appendChild(correctResponse);

        // create choiceInteraction element
        Element choiceInteraction = doc.createElement("choiceInteraction");
        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
        choiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
        choiceInteraction.setAttribute("shuffle", "true");
        itemBody.appendChild(choiceInteraction);


        // the children of choiceresponse
//        NodeList choiceResponseChildren = problemChild.getChildNodes();
//
//        Node prompt = doc.createElement("prompt");
//
//        for (int j = 0; j < choiceResponseChildren.getLength(); j++) {
//
//            // the children, such as: label, description ...
//            Node choiceResponseChild = choiceResponseChildren.item(j);
//
//
//            switch (choiceResponseChild.getNodeName()) {
//                case "label":
//                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase() +
//                            ", with text value \"" + choiceResponseChild.getTextContent() + "\"");
//                    System.out.println(prompt.getTextContent());
//                    Element paragraphElement = doc.createElement("p");
//                    paragraphElement.setTextContent(choiceResponseChild.getTextContent());
//                    prompt.appendChild(paragraphElement);
//                    break;
//                case "description":
//
//                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase() +
//                            ", with text value \"" + choiceResponseChild.getTextContent() + "\"");
//                    prompt.setTextContent(prompt.getTextContent() + choiceResponseChild.getTextContent());
//                    break;
//                case "checkboxgroup":
//                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase());
//
//                    // loop through each choice of the Node "checkboxgroup"
//                    NodeList checkboxresponseChildren = choiceResponseChild.getChildNodes();
//                    char character = 'A';
//                    for (int k = 0; k < checkboxresponseChildren.getLength(); k++) {
//                        Node choiceNode = checkboxresponseChildren.item(k);
//                        if (choiceNode.getNodeName().equals("choice")) {
//                            String correctness = choiceNode.getAttributes().item(0).getTextContent();
//                            if (correctness.equals("true")) {
//                                Element value = doc.createElement("value");
//                                value.setTextContent(String.valueOf(character));
//                                correctResponse.appendChild(value);
//                            }
//                            Element simpleChoice = doc.createElement("simpleChoice");
//                            simpleChoice.setAttribute("identifier", String.valueOf(character++));
//                            simpleChoice.setTextContent(choiceNode.getTextContent());
//                            choiceInteraction.appendChild(simpleChoice);
//                        }
//                    }
//
//                    // if more answers are right, set the "cardinality" attribute of responseDec to multiple
//                    // else to single
////                                if (correctResponse.getChildNodes().getLength() > 1) {
//                    responseDeclaration.setAttribute("cardinality", "multiple");
////                                } else {
////                                    responseDeclaration.setAttribute("cardinality", "single");
////                                }
//                    // if length of correct answers is more than 1 we get a multiple choice so we need to
//                    // set the maxChoices to the amount of correct answers that can be selected in an interaction
//                    choiceInteraction.setAttribute("maxChoices", "0");
//                    break;
//                default:
//                    if (choiceResponseChild.getNodeName().equals("p") || choiceResponseChild.getNodeName().equals("h3")) {
//                        System.out.println(choiceResponseChild.getNodeName());
//                        prompt.appendChild(choiceResponseChild);
//                    }
//            }
//        }
//        choiceInteraction.appendChild(prompt);
//
//        assessmentItemNode.appendChild(responseDeclaration);
//        assessmentItemNode.appendChild(itemBody);
//        //add the response processing of the choicebox question item
//        assessmentItemNode.appendChild(responseProcessing);
        return doc;
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
