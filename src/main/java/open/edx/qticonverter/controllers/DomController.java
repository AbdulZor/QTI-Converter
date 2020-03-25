package open.edx.qticonverter.controllers;

import open.edx.qticonverter.models.Questions.CheckboxGroup;
import open.edx.qticonverter.models.Questions.Choice;
import open.edx.qticonverter.models.Questions.SingleChoice;
import open.edx.qticonverter.services.dom.DomConverter;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestMapping("dom")
@RestController()
public class DomController {
    private final DomConverter domConverter;

    //Parser that produces DOM object trees from XML content
    //Since it is a factory object, declare it on Controller level so you don't need a new factory on every call
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private static final Logger logger = Logger.getLogger(DomController.class.getName());

    public DomController(DomConverter domConverter) {
        this.domConverter = domConverter;
    }

    @RequestMapping()
    public Document getDom() {

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new File("src/main/java/open/edx/qticonverter/olx-files/course/problem/qtiConvertFile.xml"));
            // use a loger instead of System.out
            logger.log(Level.INFO, "Number of attributes: {}", doc.getFirstChild().getAttributes().getLength());

            // Nice Stackoverflow copy :)
            //Normalize the XML Structure; It's just too important !!
            doc.getDocumentElement().normalize();

//            for (int i = 0; i < doc.getFirstChild().getChildNodes().getLength(); i++) {
//                System.out.println(doc.getFirstChild().getChildNodes().item(i).getNodeName());
///            }

            //Here comes the root node
            Element root = doc.getDocumentElement();
//            System.out.println(root.getNodeName());

            NodeList nList = doc.getElementsByTagName("problem");

            visitChildNodes(nList);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void visitChildNodes(NodeList nList) {

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
//            System.out.println("Node:");
//            System.out.println(node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // If choiceresponse (Checkbox/Single problem)
                if (node.getNodeName().contains("choiceresponse")) {
                    checkBoxProblemConverter(node);
                    if (node.getNextSibling() != null) {
                        // If the problem has more problem components (Dropdown, Multiple Choice...)
                        node = node.getNextSibling();
                    }
                }

//                //Check all attributes

                if (node.hasChildNodes()) {
                    //We got more childs; Let's visit them as well
                    visitChildNodes(node.getChildNodes());
                }
            }
        }
    }

    private void checkBoxProblemConverter(Node node) {
        SingleChoice singleChoice = new SingleChoice();

        //ownerDocument is the xml document tag
        if (node.getOwnerDocument().getDocumentElement().hasAttributes()) {
            // get attributes names and values
            NamedNodeMap nodeMap = node.getOwnerDocument().getDocumentElement().getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Node tempNode = nodeMap.item(i);
                if (tempNode.getNodeName().contains("display_name")){
                    singleChoice.setTitle(tempNode.getNodeValue());
                }
            }
            Logger.getAnonymousLogger().info(nodeMap.item(0).getNodeValue());

        }
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
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
                            checkboxGroup.setChoices(choiceList);
                            singleChoice.setCheckboxGroup(checkboxGroup);
                        }
                        break;
                }
            }
        }

        //Again: prefer logger for this
        System.out.println("-------------------------");
        System.out.println("Title: " + singleChoice.getTitle());
        System.out.println("Label: " + singleChoice.getLabel());
        System.out.println("Description: " + singleChoice.getDescription());
        for (Choice choice : singleChoice.getCheckboxGroup().getChoices()) {
            System.out.println("Choice correct: " + choice.getCorrect());
            System.out.println("Choice answerText: " + choice.getText());
        }
        System.out.println("-------------------------");

        Document qtiDocument = createQtiDocument(singleChoice);
    }

    private Document createQtiDocument(SingleChoice singleChoice) {
        return null;
    }
}
