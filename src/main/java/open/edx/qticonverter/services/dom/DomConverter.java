package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.*;
import open.edx.qticonverter.models.qti.manifest.Manifest;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DomConverter {
    public final String PACKAGE_FILES_PATH = "src/main/java/open/edx/qticonverter/files/";

    public void createQtiPackage(Course course) {
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

    private void createManifestXmlFile(Course course) {
        Manifest manifest = new Manifest();
        manifest.setMetadata("QTIv2.1 Package", "1.0.0");


        // TODO:: Maybe add problems list to Course as well
        List<Chapter> chapters = course.getChapters();
        // List of assessmentItems id's
        List<String> problemReferences = new ArrayList<>();
        for (Chapter chapter : chapters) {

            List<Sequential> sequentials = chapter.getSequentials();
            for (Sequential sequential : sequentials) {

                List<Vertical> verticals = sequential.getVerticals();
                for (Vertical vertical : verticals) {

                    List<Problem> problems = vertical.getProblems();
                    for (Problem problem : problems) {
                        problemReferences.add(problem.getId());

                        // Add resource (item/problem) to manifest
                        manifest.addResource(problem.getId().trim(),
                                "imsqti_item_xmlv2p1",
                                problem.getName() + "-" + problem.getId() + ".xml",
                                null
                        );
                    }


                }
            }
        }
        // Add assessmentItem resources with optional dependencies
        manifest.addResource(course.getId(),
                "imsqti_test_xmlv2p1",
                course.getName() + "-" + course.getId() + ".xml", // we use course because, this is what is used in the assessment part element
                problemReferences);

        try {
            build(manifest.getDocument(), course, "imsmanifest.xml");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void build(Document document, Course course, String fileName) throws TransformerException {
        // write the content into manifest xml file
        // NOTE: if you want the thespart to be something else (sequential ...) you need to move this transform code
        // over to the add* method. This piece of code creates parses the DOM into an XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource manifestSource = new DOMSource(document);
        String problemFilePath = PACKAGE_FILES_PATH + course.getId() + "/" + fileName;
        StreamResult result = new StreamResult(problemFilePath);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(manifestSource, result);
    }
}
