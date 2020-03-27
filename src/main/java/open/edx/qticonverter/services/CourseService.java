package open.edx.qticonverter.services;

import open.edx.qticonverter.models.*;
import open.edx.qticonverter.models.Questions.CheckboxGroup;
import open.edx.qticonverter.models.Questions.Choice;
import open.edx.qticonverter.models.Questions.SingleChoice;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import open.edx.qticonverter.mongomodel.Definition;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.DefinitionsRepo;
import open.edx.qticonverter.repositories.StructuresRepo;
import open.edx.qticonverter.repositories.VersionsRepo;
import open.edx.qticonverter.services.dom.DomService;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CourseService {
    /*
     * Algemeen: heb de repos even hernoemd zodat duidelijk is of Versions een repo is of een model object.
     * Voor de duidelijkheid zou ik het opbouwen van het course model losgemaakt hebben van het wegschrijven naar xml files
     * en zip. Doordat het nu samen is kloppen de controllers op zich niet meer. Als je een lijstje courses wil hebben lukt
     * dat niet zonder alles weg te schrijven.
     *
     * Qti versie 2.1 wordt nu gebruikt. Uiteindelijk moet dat 2.2 of zelfs 3.0 worden, maar dat zijn meer details dan iets
     * anders. Als je echter de xml output op basis van een volledig opgebouwd course object maakt kan je ook meerdere
     * versies output naast elkaar maken.
     */


    private final VersionsRepo versionsRepo;
    private final StructuresRepo structuresRepo;
    private final DefinitionsRepo definitionsRepo;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseService.class);

    public CourseService(VersionsRepo versionsRepo, StructuresRepo structuresRepo, DefinitionsRepo definitionsRepo) {
        this.versionsRepo = versionsRepo;
        this.structuresRepo = structuresRepo;
        this.definitionsRepo = definitionsRepo;
    }

    public List<Course> getCourses() throws Exception {
        ArrayList<Course> courses = new ArrayList<>();
        //Get Active versions from repo
        List<Version> all = versionsRepo.findAll();

        for (Version version : all) {
            // Map each value of active_versions to create the Course object
            if (version.getVersions().getPublished_branch() != null) {
                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();

                Course course = new Course();
                course.setId(version.getId());
                course.setName(version.getCourse());
                course.setStructure(structuresRepo.findById(publishedBranchId).get());


                addChapters(course);
                courses.add(course);

                String fileName = course.getName().trim().concat(course.getId().trim());
//                ZipUtil.pack(new File("src/main/java/open/edx/qticonverter/"), new File("src/main/java/open/edx/qticonverter/zipfiles/" + fileName + ".zip"));
            }
        }
        return courses;
    }

    public Course getCourseById(String courseId) throws Exception {
        //Get Active versions from repo
        List<Version> all = versionsRepo.findAll();
        Course course = new Course();
        for (Version version : all) {
            if (version.getId().equals(courseId)) {
                course.setId(version.getId());
                course.setName(version.getCourse());

                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();
                course.setStructure(structuresRepo.findById(publishedBranchId).get());

                addChapters(course);
//                ZipUtil.pack(new File("src/main/java/open/edx/qticonverter/files/"), new File("src/main/java/open/edx/qticonverter/zipfiles/" + course.getName() + course.getId() + ".zip"));
            }
        }
        return course;
    }

    // TODO:: Refractor 'id' to 'courseId' after uncommenting code
    // We use the id because they version the structures in the structures collection
    // We can use the courses published id to get the structure with the latest version.
    // If we look at the structures collection we may have 5 versions of the same course but we
    // want to get the latest version from the active_versions collections

    private void addChapters(Course course) throws Exception {
        // Find the course in blocks and add the children as Chapter
        // Whereby the chapters can be found in the fields property->children->[chapter, "ObjectId"]
        // getBlocks are the
        List<Map> courses = course.getStructure().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_type") &&
                blockmap.get("block_type").equals("course")).collect(Collectors.toList());

        // for each course
        for (Map courseMap : courses) {
            if (courseMap.containsKey("fields")) {
                Map fieldValue = (Map) courseMap.get("fields");
                List<List> courseChildrenValue = (List) fieldValue.get("children");

                for (List chapterPair : courseChildrenValue) {
//                    System.out.println(chapterPair.get(1)); // block_id needs to match this value
                    Chapter chapter = new Chapter();
                    chapter.setId(chapterPair.get(1).toString());

                    List<Map> chapters = course.getStructure().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                            blockmap.get("block_id").toString().equals(chapter.getId())).collect(Collectors.toList());

                    for (Map chapterMap : chapters) {
                        Map chapterFieldValue = (Map) chapterMap.get("fields");
                        String chapterNameValue = chapterFieldValue.get("display_name").toString();
                        chapter.setName(chapterNameValue);

                        // Add the XML attributes if available
                        addXmlAttributes(chapter, chapterFieldValue);

                        List<List> chapterChildrenValue = (List) chapterFieldValue.get("children");

                        if (chapterChildrenValue != null) {
                            for (List chapterChild : chapterChildrenValue) {
                                // after each property of Chapter obj (name, xml_attr ..) is set we add the sequentials property
                                addSequentials(course, chapter, chapterChild.get(1).toString());
                            }
                        }
                    }
                    course.addChildBlock(chapter);
                }
            }
        }
        Logger.getAnonymousLogger().info(courses.toString());
    }
    // Course --> Chapter -> Sequential
    // Get courses();
    // contains all chapters which again contains sequentials ...

    private void addSequentials(Course course, Chapter chapter, String sequentialId) throws Exception {
        Map sequentials = course.getStructure().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(sequentialId)).findFirst().get();

        // Get
        Sequential sequential = new Sequential();
        sequential.setId(sequentialId);

        Map sequentialField = (Map) sequentials.get("fields");
        sequential.setName(sequentialField.get("display_name").toString());

        addXmlAttributes(sequential, sequentialField);

        List<List> sequentialChildrenValue = (List) sequentialField.get("children");

        // after each property of Sequential obj (name, xml_attr ..) is set we add the vertical property
        if (sequentialChildrenValue != null) {
            for (List sequentialChild : sequentialChildrenValue) {
                addVerticals(course, sequential, sequentialChild.get(1).toString());
            }
        }
        chapter.addChildBlock(sequential);
    }

    private void addVerticals(Course course, Sequential sequential, String verticalId) throws Exception {
        Map verticals = course.getStructure().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(verticalId)).findFirst().get();

        // Get
        Vertical vertical = new Vertical();
        vertical.setId(verticalId);

        Map verticalField = (Map) verticals.get("fields");
        vertical.setName(verticalField.get("display_name").toString());

        addXmlAttributes(vertical, verticalField);

        List<List> verticalChildrenValue = (List) verticalField.get("children");

        // after each property of Sequential obj (name, xml_attr ..) is set we add the vertical property
        if (verticalChildrenValue != null) {
            for (List verticalChild : verticalChildrenValue) {
                if (verticalChild.get(0).toString().equalsIgnoreCase("problem")) {
                    addProblems(course, vertical, verticalChild.get(1).toString());
                }
            }
        }
        sequential.addChildBlock(vertical);
    }

    private void addProblems(Course course, Vertical vertical, String problemId) throws IOException {
        Map problems = course.getStructure().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(problemId)).findFirst().get();

        // Get
        Problem problem = new Problem();
        problem.setId(problemId);

        Map problemField = (Map) problems.get("fields");
        problem.setProblemType(problemField); //TODO:: Find a way to add the ProblemType (get it from XML)
        problem.setName(problemField.get("display_name") != null ? problemField.get("display_name").toString() : "");
        problem.setMax_attempts(Integer.parseInt(problemField.get("max_attempts") != null ? problemField.get("max_attempts").toString() : "1"));
        problem.setWeight(Float.parseFloat(problemField.get("weight") != null ? problemField.get("weight").toString() : "1.0"));
        problem.setFileIdentifier(problem.getName() + "-" + problem.getId());
        logger.info("The created problem and its properties: {}", problem);


        Optional<Definition> definitionOptional = this.definitionsRepo.findById((ObjectId) problems.get("definition"));


        int PRETTY_PRINT_INDENT_FACTOR = 5;
        String TEST_XML_STRING = definitionOptional.get().getData();

        try {
//            System.out.println("Sample.xml contents = " + TEST_XML_STRING);

            Document doc = stringToXmlDocument(TEST_XML_STRING);
            logger.info("XML STRING: {}", TEST_XML_STRING);


            // DOM PARSER -> DOM TRANSFORM -> XML FILE CREATED
            Element problemNode = doc.getDocumentElement();


//            Node node = problemNode.getChildNodes().item(0).getChildNodes().
//            itemBody.appendChild()

            NodeList problemChildren = problemNode.getChildNodes();

            Node assessmentItemNode = doc.createElement("assessmentItem");

            for (int i = 0; i < problemChildren.getLength(); i++) {
                Node problemChild = problemChildren.item(i);

                // if there is a choice box question in the problem context
                //TODO:: CREATE A FUNCTION FOR EACH PROBLEM TYPE AND USE A SWITCH STATEMENT

            }


            doc.removeChild(problemNode);

            // DOM PARSER -> JAVA OBJECT CREATED -> XML DOCUMENT CREATED -> XML FILE CREATED

            // WITH JACKSON LIBRARY

        } catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new IOException();
        }

        vertical.addChildBlock(problem);
        course.addChildBlock(problem);
        logger.info("Course object: {}", course);
    }


    private void buildChoiceBoxItem(Document doc, Map problemField, Node problemChild, Node assessmentItemNode) {
        // create responseDeclaration element with its child correctResponse
        Element responseDeclaration = doc.createElement("responseDeclaration");
        String responseIdentifier = "RESPONSE";
        responseDeclaration.setAttribute("identifier", responseIdentifier);
        responseDeclaration.setAttribute("baseType", "identifier");

        Element itemBody = doc.createElement("itemBody");
        Element responseProcessing = doc.createElement("responseProcessing");
        if (problemField.get("weight") != null) {
            responseDeclaration.setAttribute("MAX_SCORE", problemField.get("weight").toString());
        }

        Element correctResponse = doc.createElement("correctResponse");
        responseDeclaration.appendChild(correctResponse);

        // create choiceInteraction element
        Element choiceInteraction = doc.createElement("choiceInteraction");
        // also add the responseIdentifier to the choiceInteraction that matches the responseDeclaration
        choiceInteraction.setAttribute("responseIdentifier", responseIdentifier);
        choiceInteraction.setAttribute("shuffle", "true");
        itemBody.appendChild(choiceInteraction);

        Node prompt = doc.createElement("prompt");

        // the children of choiceresponse
        NodeList choiceResponseChildren = problemChild.getChildNodes();
        for (int j = 0; j < choiceResponseChildren.getLength(); j++) {

            // the children, such as: label, description ...
            Node choiceResponseChild = choiceResponseChildren.item(j);

            switch (choiceResponseChild.getNodeName()) {
//                case "h3":
//                    System.out.println("H3: " + choiceInteraction.getTextContent());
//                    Element headerElement = doc.createElement("h3");
//                    headerElement.setTextContent(choiceResponseChild.getTextContent());
//                    prompt.appendChild(headerElement);
//                    break;
//                case "p":
                case "label":
                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase() +
                            ", with text value \"" + choiceResponseChild.getTextContent() + "\"");
                    System.out.println(prompt.getTextContent());
                    Element paragraphElement = doc.createElement("p");
                    paragraphElement.setTextContent(choiceResponseChild.getTextContent());
                    prompt.appendChild(paragraphElement);
                    break;
                case "description":
                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase() +
                            ", with text value \"" + choiceResponseChild.getTextContent() + "\"");
                    break;
                case "choicegroup":
                    System.out.println("I've got the " + choiceResponseChild.getNodeName().toUpperCase());

                    // loop through each choice of the Node "checkboxgroup"
                    NodeList checkboxresponseChildren = choiceResponseChild.getChildNodes();
                    char character = 'A';
                    for (int k = 0; k < checkboxresponseChildren.getLength(); k++) {
                        Node choiceNode = checkboxresponseChildren.item(k);
                        if (choiceNode.getNodeName().equals("choice")) {
                            String correctness = choiceNode.getAttributes().item(0).getTextContent();
                            System.out.println("Correctness: " + correctness);
                            if (correctness.equals("true")) {
                                Element value = doc.createElement("value");
                                value.setTextContent(String.valueOf(character));
                                correctResponse.appendChild(value);
                            }
                            Element simpleChoice = doc.createElement("simpleChoice");
                            simpleChoice.setAttribute("identifier", String.valueOf(character++));
                            simpleChoice.setTextContent(choiceNode.getTextContent());
                            choiceInteraction.appendChild(simpleChoice);
                        }
                    }

                    // if more answers are right, set the "cardinality" attribute of responseDec to multiple
                    // else to single
//                                if (correctResponse.getChildNodes().getLength() > 1) {
                    responseDeclaration.setAttribute("cardinality", "single");
//                                } else {
//                                    responseDeclaration.setAttribute("cardinality", "single");
//                                }
                    // if length of correct answers is more than 1 we get a multiple choice so we need to
                    // set the maxChoices to the amount of correct answers that can be selected in an interaction
                    choiceInteraction.setAttribute("maxChoices", "1");
                    break;
                default:
                    if (choiceResponseChild.getNodeName().equals("p") || choiceResponseChild.getNodeName().equals("h3")) {
                        prompt.appendChild(choiceResponseChild);
                    }
            }
        }
        choiceInteraction.appendChild(prompt);
        assessmentItemNode.appendChild(responseDeclaration);
        assessmentItemNode.appendChild(itemBody);
        //add the response processing of the choicebox question item
        assessmentItemNode.appendChild(responseProcessing);
    }


    private void visitChildNodes(NodeList nList) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
//            System.out.println("Node:");
//            System.out.println(node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                if (node.getNodeName().contains("choiceresponse")) {
                    checkBoxProblemConverter(node);
                    if (node.getNextSibling() != null) {
                        node = node.getNextSibling();
                    }
                }

                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                }
                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    visitChildNodes(node.getChildNodes());
                }
            }
        }
    }

    private void checkBoxProblemConverter(Node node) {
        SingleChoice singleChoice = new SingleChoice();
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                System.out.println("CHild node name: " + nodeList.item(i).getNodeName());
                System.out.println("CHild node value: " + nodeList.item(i).getTextContent());
                switch (nodeList.item(i).getNodeName()) {
                    case "label":
                        singleChoice.setLabel(nodeList.item(i).getTextContent());
                        break;
                    case "description":
                        singleChoice.setDescription(nodeList.item(i).getTextContent());
                        break;
                    case "checkboxgroup":
                        Node checkBoxNode = nodeList.item(i);
                        CheckboxGroup checkboxGroup = new CheckboxGroup();
                        List<Choice> choiceList = new ArrayList<>();
                        if (checkBoxNode.hasChildNodes()) {
                            NodeList nList = checkBoxNode.getChildNodes();
                            for (int j = 0; j < nList.getLength(); j++) {
                                if (nList.item(j).getNodeName().equals("choice")) {
                                    Choice choice = new Choice();
                                    choice.setCorrect(nList.item(j).getAttributes().item(0).getTextContent());
                                    choice.setText(nList.item(j).getTextContent());
                                    choiceList.add(choice);
                                }
                            }
                        }
                        break;
                    default:
                }
            }
        }
    }

    private static Document stringToXmlDocument(String str) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(str)));

        return document;
    }

    private static String toXmlString(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StringWriter strWriter = new StringWriter();
        StreamResult result = new StreamResult(strWriter);

        transformer.transform(source, result);

        return strWriter.getBuffer().toString();

    }

    //TODO:: MAAK ALLE CLASSES DIE GEBRUIK KUNNEN MAKEN VAN XML ATTRIBUTES, IMPLEMENTS XmlAttributes
    private void addXmlAttributes(Object objectType, Map fieldValue) {
        if (fieldValue.get("xml_attributes") != null) {
            Map xmlAttributes = (Map) fieldValue.get("xml_attributes");
            if (xmlAttributes != null) {
                List xmlFiles = (List) xmlAttributes.get("filename");
                ((XmlAttributes) objectType).setXml_attributes(xmlFiles);
            }
        }
    }
}

