package open.edx.qticonverter.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import open.edx.qticonverter.models.*;
import open.edx.qticonverter.models.Questions.SingleChoice;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import open.edx.qticonverter.mongomodel.Definition;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.Definitions;
import open.edx.qticonverter.repositories.Structures;
import open.edx.qticonverter.repositories.Versions;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CourseService {
    private final Versions versions;
    private final Structures structures;
    private final Definitions definition;

    public CourseService(Versions versions, Structures structures, Definitions definitions) {
        this.versions = versions;
        this.structures = structures;
        this.definition = definitions;
    }

    public List<Course> getCourses() throws IOException {
        ArrayList<Course> courses = new ArrayList<>();
        //Get Active versions from repo
        List<Version> all = versions.findAll();

        for (Version version : all) {
            // Map each value of active_versions to create the Course object
            if (version.getVersions().getPublished_branch() != null) {
                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();

                Course course = new Course();
                course.setId(version.getId());
                course.setName(version.getCourse());
                course.setStructure(structures.findById(publishedBranchId).get());

                addChapters(course);
                courses.add(course);
            }
        }
        return courses;
    }

    public Course getCourseById(String courseId) throws IOException {
        //Get Active versions from repo
        List<Version> all = versions.findAll();
        Course course = new Course();
        for (Version version : all) {
            if (version.getId().equals(courseId)) {
                course.setId(version.getId());
                course.setName("Hallo id");

                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();
                course.setStructure(structures.findById(publishedBranchId).get());
                addChapters(course);
            }
        }
        return course;
    }

    // TODO:: Refractor 'id' to 'courseId' after uncommenting code
    // We use the id because they version the structures in the structures collection
    // We can use the courses published id to get the structure with the latest version.
    // If we look at the structures collection we may have 5 versions of the same course but we
    // want to get the latest version from the active_versions collections

    private void addChapters(Course course) throws IOException {
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

    private void addSequentials(Course course, Chapter chapter, String sequentialId) throws IOException {
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

    private void addVerticals(Course course, Sequential sequential, String verticalId) throws IOException {
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
        // Set the definition MAP

//        problem.setDefinition((Map) problems.get("definition"));

        Map problemField = (Map) problems.get("fields");


        problem.setName(problemField.get("display_name").toString());

        addXmlAttributes(problem, problemField);

//        System.out.println("---------------------------------------------");
//        System.out.println(problems.get("definition"));
//        System.out.println(problems.get("definition").getClass());
//        System.out.println("---------------------------------------------");

//        System.out.println("Definition attributes");
        Optional<Definition> definitionOptional = this.definition.findById((ObjectId) problems.get("definition"));

//        System.out.println("Block Type: " + definitionOptional.get().getBlockType() + "\n");
//        System.out.println("Fields: " + definitionOptional.get().getFields() + "\n");
//        System.out.println("Data: " + definitionOptional.get().getData() + "\n");


        int PRETTY_PRINT_INDENT_FACTOR = 5;
        String TEST_XML_STRING = definitionOptional.get().getData();

        try {
            if (TEST_XML_STRING.contains("checkboxgroup")) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

                // read file and put contents into the string
//            String readContent = new String(Files.readAllBytes(Paths.get("../olx-files/to_deserialize.xml")));

                // deserialize from the XML into a Phone object
                open.edx.qticonverter.models.Questions.Problem deserializedData = xmlMapper.readValue(TEST_XML_STRING, open.edx.qticonverter.models.Questions.Problem.class);

                // Print object details
                System.out.println("Deserialized data: ");
                System.out.println("XML String: " + TEST_XML_STRING);
                System.out.println("\tP: " + deserializedData.getP());
                System.out.println("\tSolution: " + deserializedData.getSolution());
            }
        } catch (IOException e) {
            throw new IOException();
            // handle the exception
        }

//        try {
//            System.out.println("Sample.xml contents = " + TEST_XML_STRING);
//
//            Document doc = toXmlDocument(TEST_XML_STRING);
//
////            System.out.println("XML document formed");
////
//            if (doc.hasChildNodes()) {
//
//                printNote(doc.getChildNodes());
//
//            }
//
//            if (TEST_XML_STRING.contains("checkboxgroup")) {
//                ObjectMapper objectMapper = new XmlMapper();
//                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//                objectMapper.registerModule(new JaxbAnnotationModule());
//                System.out.println("IK BEGIN MET READVALUE()");
//                open.edx.qticonverter.models.Questions.Problem singleChoiceProblem = objectMapper.readValue(doc, open.edx.qticonverter.models.Questions.Problem.class);
//                System.out.println("IK EINDIG NU!");
//                Logger.getAnonymousLogger().info(singleChoiceProblem.toString());
//                System.out.println(((Map) singleChoiceProblem.getChoiceresponse().getChoice().get(0)).get("choice"));
//                System.out.println(((Map) singleChoiceProblem.getChoiceresponse().getChoice().get(0)).get(""));
//            }
//
//
//        } catch (IOException | SAXException | ParserConfigurationException ex) {
//            throw new IOException();
//        }

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
//            System.out.println("---------------------------");
//            System.out.println(xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR));
//            System.out.println("---------------------------");
//            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
//            System.out.println(jsonPrettyPrintString);
        } catch (JSONException je) {
            System.out.println(je.toString());
        }


        vertical.addChildBlock(problem);
    }

    private static void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());

                if (tempNode.hasAttributes()) {

                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();

                    for (int i = 0; i < nodeMap.getLength(); i++) {

                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());

                    }

                }

                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());
                }
                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
            }
        }

    }

    private static Document toXmlDocument(String str) throws ParserConfigurationException, SAXException, IOException {

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

//    //TODO:: Zorg ervoor dat je
//    private void addChildrenToBlockObject(BlockTypeable blockTypeable, Map fieldValue) {
//        List<List> childrenValue = (List) fieldValue.get("children");
//
//        // after each property of Chapter obj (name, xml_attr ..) is set we add the sequentials property
//        if (childrenValue != null) {
//            for (List child : childrenValue) {
//                blockTypeable.addChildBlock(child.get(1).toString());
//            }
//        }
//    }
}

