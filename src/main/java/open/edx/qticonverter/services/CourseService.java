package open.edx.qticonverter.services;

import open.edx.qticonverter.models.*;
import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import open.edx.qticonverter.mongomodel.Structure;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.Structures;
import open.edx.qticonverter.repositories.Versions;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CourseService {
    private final Versions versions;
    private final Structures structures;
    private Optional<Structure> currentCourseStructure;

    public CourseService(Versions versions, Structures structures) {
        this.versions = versions;
        this.structures = structures;
        this.currentCourseStructure = Optional.empty();
    }

    public List<Course> getCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        //Get Active versions from repo
        List<Version> all = versions.findAll();

        for (Version version : all) {
            // Map each value of active_versions to create the Course object
            if (version.getVersions().getPublished_branch() != null) {
                Course course = new Course();
                course.setId(version.getId());
                course.setName(version.getCourse());

                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();
                this.currentCourseStructure = structures.findById(publishedBranchId);
                addChapters(course);
                courses.add(course);
            }
        }
        return courses;
    }

    public Course getCourseById(String courseId) {
        //Get Active versions from repo
        List<Version> all = versions.findAll();
        Course course = new Course();
        for (Version version : all) {
            if (version.getVersions().toString().equals(courseId)) {
                course.setId(version.getId());
                course.setName(version.getCourse());


                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();
                this.currentCourseStructure = structures.findById(publishedBranchId);
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

    private void addChapters(Course course) {
        // Find the course in blocks and add the children as Chapter
        // Whereby the chapters can be found in the fields property->children->[chapter, "ObjectId"]
        // getBlocks are the
        List<Map> courses = this.currentCourseStructure.get().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_type") &&
                blockmap.get("block_type").equals("course")).collect(Collectors.toList());

        // for each course
        for (Map courseMap : courses) {
            if (courseMap.containsKey("fields")) {
                Map fieldValue = (Map) courseMap.get("fields");
                List<List> courseChildrenValue = (List) fieldValue.get("children");

                for (List chapterPair : courseChildrenValue) {
                    System.out.println(chapterPair.get(1)); // block_id needs to match this value
                    Chapter chapter = new Chapter();
                    chapter.setId(chapterPair.get(1).toString());

                    List<Map> chapters = this.currentCourseStructure.get().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                            blockmap.get("block_id").toString().equals(chapter.getId())).collect(Collectors.toList());

                    for (Map chapterMap : chapters) {
                        Map chapterFieldValue = (Map) chapterMap.get("fields");
                        String chapterNameValue = chapterFieldValue.get("display_name").toString();
                        chapter.setName(chapterNameValue);

                        // Add the XML attributes if available
                        addXMLAttributes(chapter, chapterFieldValue);

                        List<List> chapterChildrenValue = (List) chapterFieldValue.get("children");

                        // after each property of Chapter obj (name, xml_attr ..) is set we add the sequentials property
                        if (chapterChildrenValue != null) {
                            for (List chapterChild : chapterChildrenValue) {
                                // after each property of Chapter obj (name, xml_attr ..) is set we add the sequentials property
                                addSequentials(chapter, chapterChild.get(1).toString());
                            }
                        }

                        // TODO:: Make a new function 'addSequentials', otherwise this function will loose its purpose and simpleness
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

    private void addSequentials(Chapter chapter, String sequentialId) {
        Map sequentials = this.currentCourseStructure.get().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(sequentialId)).findFirst().get();

        // Get
        Sequential sequential = new Sequential();
        sequential.setId(sequentialId);

        Map sequentialField = (Map) sequentials.get("fields");
        sequential.setName(sequentialField.get("display_name").toString());

        addXMLAttributes(sequential, sequentialField);

        List<List> sequentialChildrenValue = (List) sequentialField.get("children");

        // after each property of Sequential obj (name, xml_attr ..) is set we add the vertical property
        if (sequentialChildrenValue != null) {
            for (List sequentialChild : sequentialChildrenValue) {
                addVerticals(sequential, sequentialChild.get(1).toString());
            }
        }
        chapter.addChildBlock(sequential);
    }

    private void addVerticals(Sequential sequential, String verticalId) {
        Map verticals = this.currentCourseStructure.get().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(verticalId)).findFirst().get();

        // Get
        Vertical vertical = new Vertical();
        vertical.setId(verticalId);

        Map verticalField = (Map) verticals.get("fields");
        vertical.setName(verticalField.get("display_name").toString());

        addXMLAttributes(vertical, verticalField);

        List<List> verticalChildrenValue = (List) verticalField.get("children");

        // after each property of Sequential obj (name, xml_attr ..) is set we add the vertical property
        if (verticalChildrenValue != null) {
            for (List verticalChild : verticalChildrenValue) {
                addProblems(vertical, verticalChild.get(1).toString());
            }
        }
        sequential.addChildBlock(vertical);
    }

    private void addProblems(Vertical vertical, String problemId) {
        Map problems = this.currentCourseStructure.get().getBlocks().stream().filter(blockmap -> blockmap.containsKey("block_id") &&
                blockmap.get("block_id").toString().equals(problemId)).findFirst().get();

        // Get
        Problem problem = new Problem();
        problem.setId(problemId);

        Map problemField = (Map) problems.get("fields");

//        vertical.setName(problemField.get("display_name").toString());

        addXMLAttributes(problem, problemField);

        List<List> problemChildrenValue = (List) problemField.get("children");

//        // after each property of Sequential obj (name, xml_attr ..) is set we add the vertical property
//        if (problemChildrenValue != null) {
//            for (List problemChild : problemChildrenValue) {
//                addProblems(problem, problemChild.get(1).toString());
//            }
//        }

        System.out.println("\n\nProblem entryset:");
        problems.entrySet().forEach(System.out::println);
        System.out.println("\n\n");
        vertical.addChildBlock(problem);
    }

    //TODO:: MAAK ALLE CLASSES DIE GEBRUIK KUNNEN MAKEN VAN XML ATTRIBUTES, IMPLEMENTS XmlAttributes
    private void addXMLAttributes(Object objectType, Map fieldValue) {
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

