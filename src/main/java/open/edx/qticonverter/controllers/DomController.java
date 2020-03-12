package open.edx.qticonverter.controllers;

import open.edx.qticonverter.services.dom.DomConverter;
import open.edx.qticonverter.services.xslt.XsltConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@RequestMapping("dom")
@RestController()
public class DomController {
    private final DomConverter domConverter;

    public DomController(DomConverter domConverter) {
        this.domConverter = domConverter;
    }

    @RequestMapping()
    public Document getDom() {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new File("src/main/java/open/edx/qticonverter/olx-files/course/problem/qtiConvertFile.xml"));
            System.out.println(doc.getFirstChild().getAttributes().getLength());

            //Normalize the XML Structure; It's just too important !!
            doc.getDocumentElement().normalize();

//            System.out.println(doc.getFirstChild().getAttributes().getLength());
//            System.out.println(doc.getFirstChild().getChildNodes().getLength());

            for (int i = 0; i < doc.getFirstChild().getChildNodes().getLength(); i++) {
//                System.out.println(doc.getFirstChild().getChildNodes().item(i).getNodeName());
            }

            //Here comes the root node
            Element root = doc.getDocumentElement();
//            System.out.println(root.getNodeName());

            //Get all employees
            NodeList nList = doc.getElementsByTagName("problem");
            System.out.println("============================");

            visitChildNodes(nList);


            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void visitChildNodes(NodeList nList) {
        System.out.println(nList.getLength());
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
//            System.out.println("Node:");
//            System.out.println(node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }
                }
            }

        }
    }
}
