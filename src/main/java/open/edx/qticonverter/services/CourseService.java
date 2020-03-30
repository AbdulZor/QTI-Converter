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

        Optional<Definition> definitionOptional = this.definitionsRepo.findById((ObjectId) problems.get("definition"));

        Map problemField = (Map) problems.get("fields");
        problem.setProblemType(null); //TODO:: Find a way to add the ProblemType (get it from XML)
        problem.setDefinition(definitionOptional.get());
        problem.setName(problemField.get("display_name") != null ? problemField.get("display_name").toString() : "");
        problem.setMax_attempts(Integer.parseInt(problemField.get("max_attempts") != null ? problemField.get("max_attempts").toString() : "1"));
        problem.setWeight(Float.parseFloat(problemField.get("weight") != null ? problemField.get("weight").toString() : "1.0"));
        problem.setFileIdentifier(problem.getName() + "-" + problem.getId());
        logger.info("The created problem and its properties: {}", problem);


        vertical.addChildBlock(problem);
        course.addChildBlock(problem);
        logger.info("Course object: {}", course);
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
