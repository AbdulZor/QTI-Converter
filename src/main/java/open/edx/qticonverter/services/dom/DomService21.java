package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.olx.Course;
import open.edx.qticonverter.models.olx.Problem;

import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import open.edx.qticonverter.models.qti.item.ItemBody21;
import open.edx.qticonverter.models.qti.item.choices.SimpleChoice;
import open.edx.qticonverter.models.qti.item.enums.BaseType;
import open.edx.qticonverter.models.qti.item.enums.Cardinality;
import open.edx.qticonverter.models.qti.item.enums.QtiBodyElement21;
import open.edx.qticonverter.models.qti.item.interactions.blockinteractions.ChoiceInteraction21;
import open.edx.qticonverter.models.qti.outcomeDeclarations.MaxScoreOutcomeDeclaration;
import open.edx.qticonverter.models.qti.outcomeDeclarations.OutcomeDeclarationStrategy;
import open.edx.qticonverter.models.qti.outcomeDeclarations.ScoreOutcomeDeclaration;
import open.edx.qticonverter.models.qti.item.responseDeclarations.ResponseDeclaration21;
import open.edx.qticonverter.models.qti.item.responseDeclarations.ResponseDeclarationStrategy;
import open.edx.qticonverter.models.qti.manifest.Manifest;
import open.edx.qticonverter.models.qti.manifest.Manifest21Builder;
import open.edx.qticonverter.models.qti.manifest.ManifestBuilder;
import open.edx.qticonverter.models.qti.manifest.enums.SchemaVersion;
import open.edx.qticonverter.services.CourseService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomService21 implements DomServiceStrategy {
    // Constants
    private final CourseService courseService;
    public static final String PACKAGE_FILES_PATH = "src/main/java/open/edx/qticonverter/files/";
    public static final String XML_EXTENSION = ".xml";
    private static final Logger logger = LoggerFactory.getLogger(DomService21.class);
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    // Attributes


    public DomService21(CourseService courseService) {
        this.courseService = courseService;
    }

    public void createQtiPackages() throws IOException {
        List<Course> courses;

        courses = this.courseService.getCourses();

        for (Course course : courses) {
            boolean directoryIsMade = recreateDirectory(course);

            if (directoryIsMade) {
                createManifestXmlFile(course);
                createAssessmentItemFiles(course);
            }
        }
    }

    public void createQtiPackageForId(String id) throws IOException {
        Course course;

        course = this.courseService.getCourseById(id);

        boolean directoryIsMade = recreateDirectory(course);

        if (directoryIsMade) {
            createManifestXmlFile(course);
            createAssessmentItemFiles(course);
        }
    }

    private void createManifestXmlFile(Course course) throws IOException {
        ManifestBuilder manifestBuilder = new Manifest21Builder();
        manifestBuilder.initializeDocument(); // We initialize a Document object to work on

        manifestBuilder.setMetadata("QTIv2.1 Package", SchemaVersion.V10);

        List<String> problemReferences = new ArrayList<>();
        List<Problem> problems = course.getProblems();
        for (Problem problem : problems) {
            problemReferences.add(problem.getFileIdentifier());

            // Add resource (item/problem) to manifest
            manifestBuilder.addResource(problem.getId().trim(),
                    "imsqti_item_xmlv2p1",
                    problem.getFileIdentifier() + XML_EXTENSION,
                    null
            );

        }
        // Add assessmentItem resources with optional dependencies
        manifestBuilder.addResource(course.getId(),
                "imsqti_test_xmlv2p1",
                course.getName() + "-" + course.getId() + XML_EXTENSION, // we use course name and id, because this is the filename of the assessmentTest part
                problemReferences);

        Manifest manifest = manifestBuilder.getResult();

        build(manifest.getDocument(), course, "imsmanifest.xml");

    }

    private void createAssessmentItemFiles(Course course) throws IOException {
        List<Problem> problems = course.getProblems();
        for (Problem problem : problems) {
            AssessmentItem21 assessmentItemObj = new AssessmentItem21(problem.getId(), problem.getName(), problem.getTimeDependent(), false);

            ItemBody21 itemBody = new ItemBody21();
            assessmentItemObj.setItemBody(itemBody);

            OutcomeDeclarationStrategy scoreOutcomeDeclaration = new ScoreOutcomeDeclaration();
            scoreOutcomeDeclaration.createOutcomeDeclaration(0.0f);
            assessmentItemObj.addOutcomeDeclaration(scoreOutcomeDeclaration);

            OutcomeDeclarationStrategy maxOutcomeDeclaration = new MaxScoreOutcomeDeclaration();
            maxOutcomeDeclaration.createOutcomeDeclaration(problem.getWeight());
            assessmentItemObj.addOutcomeDeclaration(maxOutcomeDeclaration);

            String problemXMLString = problem.getDefinition().getData();
            logger.info("XML STRING: {}", problemXMLString);

            Document olxDocument = stringToXmlDocument(problemXMLString);

            List<Element> childNodesProblem = olxDocument.getRootElement().getChildren();

            for (int i = 0; i < childNodesProblem.size(); i++) {
                Element problemChild = childNodesProblem.get(i);
                ResponseDeclarationStrategy responseDeclaration = new ResponseDeclaration21();

                switch (problemChild.getName().toLowerCase()) {
                    case "label":
                    case "description":
                        logger.info("p1 or description1 textVal1: " + problemChild.getText());
                        itemBody.append(problemChild.getText(), QtiBodyElement21.p);
                        break;
                    case "multiplechoiceresponse":
                        buildChoiceBoxItemDom(assessmentItemObj, (ResponseDeclaration21) responseDeclaration, problemChild);
                        break;
                    case "choiceresponse":
//                        buildMultipleChoiceItem(qtiDocument, problemChild, itemBody, promptStringBuilder, assessmentItemNode, responseId++);
                        break;
                    case "optionresponse":
//                        buildInlineChoiceItem(qtiDocument, problemChild, itemBody, promptStringBuilder, assessmentItemNode, responseId++);
                        break;
                    default:
                        if (problemChild.getName().equals("p") || problemChild.getName().equals("h3")) {
                            Element element = new Element(problemChild.getName(), AssessmentItem21.XMLNS_V21).setText(problemChild.getText());
                            itemBody.append(element);
                        }
                }
            }

            assessmentItemObj.buildDom();
            build(assessmentItemObj.getDocument(), course, problem.getFileIdentifier() + XML_EXTENSION);
        }
    }

    private void createMatchedResponseProcessing(Document qtiDocument, Element assessmentItemElement, int responseId, float problemWeight) {
        // overriding the match_template of QTI v2.1
        Element responseProcessing = new Element("responseProcessing");
        assessmentItemElement.addContent(responseProcessing);

        Element responseCondition = new Element("responseCondition");
        responseProcessing.addContent(responseCondition);

        Element responseIf = new Element("responseIf");
        responseCondition.addContent(responseIf);

        Element and = new Element("and");
        responseIf.addContent(and);

        // For each interaction in itemBody check if the candidate answer matches the correct answer
        for (int i = 1; i < responseId; i++) {
            Element match = new Element("match");
            and.addContent(match);

            Element variable = new Element("variable");
            variable.setAttribute("identifier", ("RESPONSE_" + i));
            match.addContent(variable);

            Element correct = new Element("correct");
            correct.setAttribute("identifier", ("RESPONSE_" + i));
            match.addContent(correct);
        }

        // Set outcomeValue if answers are correct
        Element setOutcomeValueIf = new Element("setOutcomeValue");
        setOutcomeValueIf.setAttribute("identifier", "SCORE");
        responseIf.addContent(setOutcomeValueIf);

        Element baseValueIf = new Element("baseValue");
        baseValueIf.setAttribute("baseType", "float");
        baseValueIf.setText(String.valueOf(problemWeight));
        setOutcomeValueIf.addContent(baseValueIf);

        // If the answers were not correct give the candidate 0 points
        Element responseElse = new Element("responseElse");
        responseCondition.addContent(responseElse);

        Element setOutcomeValueElse = new Element("setOutcomeValue");
        setOutcomeValueElse.setAttribute("identifier", "SCORE");
        responseElse.addContent(setOutcomeValueElse);

        Element baseValueElse = new Element("baseValue");
        baseValueElse.setAttribute("baseType", "float");
        baseValueElse.setText("0.0");
        setOutcomeValueElse.addContent(baseValueElse);
    }

    private void buildChoiceBoxItemDom(AssessmentItem21 assessmentItemObj, ResponseDeclaration21 responseDeclaration, Element problemChild) {
        String responseIdentifier = "RESPONSE_" + assessmentItemObj.getInteractionsList().size();

        responseDeclaration.setIdentifier(responseIdentifier);
        responseDeclaration.setBaseType(BaseType.IDENTIFIER);
        responseDeclaration.setCardinality(Cardinality.single);
        assessmentItemObj.addResponseDeclaration(responseDeclaration);

        ChoiceInteraction21 choiceInteraction = new ChoiceInteraction21(responseIdentifier);
        choiceInteraction.setShuffle(false);
        choiceInteraction.setMaxChoices(1);

        assessmentItemObj.getItemBody().append(choiceInteraction);
        assessmentItemObj.addInteraction(choiceInteraction);

        // the children of choiceresponse
        List<Element> choiceResponseChildren = problemChild.getChildren();

        for (Element choiceResponseChild : choiceResponseChildren) {

            // the children, such as: label, description ...
            switch (choiceResponseChild.getName()) {
                case "label":
                case "description":
                    choiceInteraction.appendToPrompt(new Text(choiceResponseChild.getText()));
                    break;
                case "choicegroup":
                    // loop through each choice of the Node "choicegroup"
                    List<Element> choicegroupResponseChildren = choiceResponseChild.getChildren();
                    char character = 'A';
                    for (int k = 0; k < choicegroupResponseChildren.size(); k++, character++) {
                        Element choiceNode = choicegroupResponseChildren.get(k);
                        if (choiceNode.getName().equalsIgnoreCase("choice")) {

                            SimpleChoice simpleChoice = new SimpleChoice(responseIdentifier + character);
                            Element simpleChoiceEl = simpleChoice.getElement();
                            simpleChoiceEl.setText(choiceNode.getText());

                            if (choiceNode.getAttributeValue("correct").equalsIgnoreCase("true")) {
                                responseDeclaration.setCorrectValue(responseIdentifier + character);
                            }

                            choiceInteraction.addSimpleChoice(simpleChoice);
                        }
                    }
                    break;
                default:
                    if (choiceResponseChild.getName().equals("p") || choiceResponseChild.getName().equals("h3")) {
                        choiceInteraction.appendToPrompt(new Text(choiceResponseChild.getText()));
                    }
            }
        }
    }

//    private void buildChoiceBoxItem(Document qtiDocument, Node problemChild, Element itemBody, StringBuilder promptStringBuilder,
//                                    Node assessmentItemNode, int responseId) {
//        // create responseDeclaration element with its child correctResponse
//        Element responseDeclaration = qtiDocument.createElement("responseDeclaration");
//        String responseIdentifier = "RESPONSE_" + responseId;
//        responseDeclaration.setAttribute("identifier", responseIdentifier);
//        responseDeclaration.setAttribute("baseType", "identifier");
//
//        Element correctResponse = qtiDocument.createElement("correctResponse");
//        responseDeclaration.appendChild(correctResponse);
//
//        // create choiceInteraction element
//        Element choiceInteraction = qtiDocument.createElement("choiceInteraction");
//        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
//        choiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
//        choiceInteraction.setAttribute("shuffle", "true");
//
//
//        // Add the <prompt> Node as the first child of choiceInteraction (required)
//        Node prompt = qtiDocument.createElement("prompt");
//
//        // If multiple items in problem component, problemList will be used
//        prompt.setTextContent(promptStringBuilder.toString());
//
//
//        choiceInteraction.appendChild(prompt);
//
//        // the children of choiceresponse
//        NodeList choiceResponseChildren = problemChild.getChildNodes();
//
//
//        for (int j = 0; j < choiceResponseChildren.getLength(); j++) {
//
//            // the children, such as: label, description ...
//            Node choiceResponseChild = choiceResponseChildren.item(j);
//
//            switch (choiceResponseChild.getNodeName()) {
//                case "label":
//                    prompt.setTextContent(choiceResponseChild.getTextContent());
//                    break;
//                case "description":
//                    Element paragraphElement2 = qtiDocument.createElement("p");
//                    paragraphElement2.setTextContent(problemChild.getTextContent());
//                    itemBody.appendChild(paragraphElement2);
//                    break;
//                case "choicegroup":
//                    // loop through each choice of the Node "choicegroup"
//                    NodeList choicegroupResponseChildren = choiceResponseChild.getChildNodes();
//                    char character = 'A';
//                    for (int k = 0; k < choicegroupResponseChildren.getLength(); k++) {
//                        Node choiceNode = choicegroupResponseChildren.item(k);
//                        if (choiceNode.getNodeName().equalsIgnoreCase("choice")) {
//                            String correctness = choiceNode.getAttributes().item(0).getTextContent();
//                            if (correctness.equalsIgnoreCase("true")) {
//                                Element value = qtiDocument.createElement("value");
//                                value.setTextContent(String.valueOf(character) + responseId);
//                                correctResponse.appendChild(value);
//                            }
//                            Element simpleChoice = qtiDocument.createElement("simpleChoice");
//                            simpleChoice.setAttribute("identifier", String.valueOf(character++) + responseId);
//                            simpleChoice.setTextContent(choiceNode.getTextContent());
//                            choiceInteraction.appendChild(simpleChoice);
//                        }
//                    }
//
//                    responseDeclaration.setAttribute("cardinality", "single");
//
//                    choiceInteraction.setAttribute("maxChoices", "1");
//                    break;
//                default:
//                    if (choiceResponseChild.getNodeName().equals("p") || choiceResponseChild.getNodeName().equals("h3")) {
//                        Node importedChoiceRespondChild = qtiDocument.importNode(choiceResponseChild, true);
//                        itemBody.appendChild(importedChoiceRespondChild);
//                    }
//            }
//        }
//        itemBody.appendChild(choiceInteraction);
//        assessmentItemNode.appendChild(responseDeclaration);
//    }
//
//    private void buildInlineChoiceItem(Document qtiDocument, Node problemChild, Element itemBody, StringBuilder promptStringBuilder,
//                                       Node assessmentItemNode, int responseId) {
//        // create responseDeclaration element with its child correctResponse
//        Element responseDeclaration = qtiDocument.createElement("responseDeclaration");
//        String responseIdentifier = "RESPONSE_" + responseId;
//        responseDeclaration.setAttribute("identifier", responseIdentifier);
//        responseDeclaration.setAttribute("baseType", "identifier");
//
//        Element correctResponse = qtiDocument.createElement("correctResponse");
//        responseDeclaration.appendChild(correctResponse);
//
//        // create choiceInteraction element
//        Element inlineChoiceInteraction = qtiDocument.createElement("inlineChoiceInteraction");
//        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
//        inlineChoiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
//        inlineChoiceInteraction.setAttribute("shuffle", "true");
//
//
//        // Add the <blockQuote> Node as the first child of choiceInteraction (required)
//        Node blockQuote = qtiDocument.createElement("blockquote");
//
//        Element paragraphElement = qtiDocument.createElement("p");
//
//        // If multiple items in problem component, problemList will be used
//        paragraphElement.setTextContent(promptStringBuilder.toString());
//
//
//        itemBody.appendChild(blockQuote);
//
//        // the children of optionresponse
//        NodeList optionResponseChildren = problemChild.getChildNodes();
//
//
//        for (int j = 0; j < optionResponseChildren.getLength(); j++) {
//
//            // the children, such as: label, description ...
//            Node optionResponseChild = optionResponseChildren.item(j);
//
//            Element breakingElement = qtiDocument.createElement("br");
//
//            switch (optionResponseChild.getNodeName()) {
//                case "label":
//                    paragraphElement.setTextContent(paragraphElement.getTextContent() + optionResponseChild.getTextContent());
//                    paragraphElement.appendChild(breakingElement);
//                    break;
//                case "optioninput":
//                    // loop through each choice of the Node "optioninput"
//                    char character = 'A';
//
//                    if (optionResponseChild.hasAttributes()) {
//                        if (optionResponseChild.getAttributes().getNamedItem("options") != null) {
//
//                            Pattern p = Pattern.compile("'([^']*)'");
//                            Matcher m = p.matcher(optionResponseChild.getAttributes().getNamedItem("options").getTextContent());
//                            while (m.find()) {
//                                if (optionResponseChild.getAttributes().getNamedItem("correct").getNodeValue().equalsIgnoreCase(m.group(1))) {
//                                    Element value = qtiDocument.createElement("value");
//                                    value.setTextContent(String.valueOf(character) + responseId);
//                                    correctResponse.appendChild(value);
//                                }
//                                Element inlineChoice = qtiDocument.createElement("inlineChoice");
//                                inlineChoice.setAttribute("identifier", String.valueOf(character++) + responseId);
//                                inlineChoice.setTextContent(m.group(1));
//                                inlineChoiceInteraction.appendChild(inlineChoice);
//                            }
//                        }
//                    } else {
//                        NodeList optioninputResponseChildren = optionResponseChild.getChildNodes();
//                        for (int k = 0; k < optioninputResponseChildren.getLength(); k++) {
//                            Node choiceNode = optioninputResponseChildren.item(k);
//                            if (choiceNode.getNodeName().equalsIgnoreCase("option")) {
//                                String correctness = choiceNode.getAttributes().item(0).getTextContent();
//                                if (correctness.equalsIgnoreCase("true")) {
//                                    Element value = qtiDocument.createElement("value");
//                                    value.setTextContent(String.valueOf(character) + responseId);
//                                    correctResponse.appendChild(value);
//                                }
//                                Element inlineChoice = qtiDocument.createElement("inlineChoice");
//                                inlineChoice.setAttribute("identifier", String.valueOf(character++) + responseId);
//                                inlineChoice.setTextContent(choiceNode.getTextContent());
//                                inlineChoiceInteraction.appendChild(inlineChoice);
//                            }
//                        }
//                    }
//
//                    responseDeclaration.setAttribute("cardinality", "single");
//
//                    break;
//                default:
//                    if (optionResponseChild.getNodeName().equals("p") || optionResponseChild.getNodeName().equals("h3")) {
//                        Node importedChoiceRespondChild = qtiDocument.importNode(optionResponseChild, true);
//                        blockQuote.appendChild(importedChoiceRespondChild);
//                    }
//            }
//        }
//        blockQuote.appendChild(paragraphElement);
//        paragraphElement.appendChild(inlineChoiceInteraction);
//        assessmentItemNode.appendChild(responseDeclaration);
//
//    }

    private boolean recreateDirectory(Course course) throws IOException {
        File file = new File(PACKAGE_FILES_PATH + course.getId() + "/");
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            throw new IOException("Course directory could not be deleted");
        }
        return file.mkdir();
    }

    private static Document stringToXmlDocument(String str) throws IOException {
        Document document;

        try {
            InputStream input = new ByteArrayInputStream(str.getBytes());
            SAXBuilder saxBuilder = new SAXBuilder();
            document = saxBuilder.build(input);
        } catch (JDOMException | IOException e) {
            throw new IOException("Could not build DOM tree from ByteArrayInputStream");
        }
        return document;
    }

    private void build(Document document, Course course, String fileName) throws IOException {
        // write the content into manifest xml file
        // NOTE: if you want the thespart to be something else (sequential ...) you need to move this transform code
        // over to the add* method. This piece of code creates parses the DOM into an XML file
        String problemFilePath = PACKAGE_FILES_PATH + course.getId() + "/" + fileName;

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());

        try {
            xmlOutput.output(document, new FileWriter(problemFilePath));
        } catch (IOException e) {
            throw new IOException("Could not create File");
        }
    }
}
