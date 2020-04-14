package open.edx.qticonverter.models.qti.item;

import open.edx.qticonverter.models.qti.item.outcomeDeclarations.OutcomeDeclarationStrategy;
import open.edx.qticonverter.models.qti.item.responseDeclarations.ResponseDeclarationStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class AssessmentItem {
    public static final int STRING256_CONSTRAINT = 256;
    private Document document;
    private Element documentElement;

    // Attributes of AssessmentTest
    private String identifier;
    private String title;
    private String label;
    private String lang;
    private boolean adaptive;
    private boolean timeDependent;
    private String toolName;

    // Children of AssessmentTest
    private ResponseDeclarationStrategy responseDeclaration;
    private List<OutcomeDeclarationStrategy> outcomeDeclaration;
    private List<Element> styleSheets;
    private Element itemBody;
    private Element responseProcessing;
    private Element modalFeedback;


    public AssessmentItem(String identifier, String title, boolean timeDependent) {
        this.identifier = identifier;
        this.title = title;
        this.timeDependent = timeDependent;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        assert builder != null;
        this.document = builder.newDocument();

        this.styleSheets = new ArrayList<>();
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Element getDocumentElement() {
        return documentElement;
    }

    public void setDocumentElement(Element documentElement) {
        this.document.appendChild(this.documentElement);
        this.documentElement = documentElement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.documentElement.setAttribute("identifier", identifier);
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.documentElement.setAttribute("title", title);
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {

        if (label.length() <= STRING256_CONSTRAINT) {
            this.documentElement.setAttribute("label", label);
            this.label = label;
        } else {
            System.out.println("The label contains more than " + STRING256_CONSTRAINT + " characters");
        }
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        //TODO:: Add attribute only if conform the documentation - [RFC3066]
        this.lang = lang;
    }

    public boolean isAdaptive() {
        return adaptive;
    }

    public void setAdaptive(boolean adaptive) {
        this.documentElement.setAttribute("adaptive", String.valueOf(adaptive));
        this.adaptive = adaptive;
    }

    public boolean isTimeDependent() {
        return timeDependent;
    }

    public void setTimeDependent(boolean timeDependent) {
        this.documentElement.setAttribute("timeDependent", String.valueOf(timeDependent));
        this.timeDependent = timeDependent;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        if (toolName.length() <= STRING256_CONSTRAINT) {
            this.documentElement.setAttribute("toolName", toolName);
            this.toolName = toolName;
        } else {
            System.out.println("The toolName contains more than " + STRING256_CONSTRAINT + " characters");
        }
    }


    public ResponseDeclarationStrategy getResponseDeclaration() {
        return responseDeclaration;
    }

    public void setResponseDeclaration(ResponseDeclarationStrategy responseDeclaration) {
        this.responseDeclaration = responseDeclaration;
    }

    public List<OutcomeDeclarationStrategy> getOutcomeDeclaration() {
        return outcomeDeclaration;
    }

    public void setOutcomeDeclaration(List<OutcomeDeclarationStrategy> outcomeDeclaration) {
        this.outcomeDeclaration = outcomeDeclaration;
    }

    public List<Element> getStyleSheets() {
        return styleSheets;
    }

    public void addStyleSheet(Element styleSheet) {
        this.documentElement.appendChild(styleSheet);
        this.styleSheets.add(styleSheet);
    }

    public void setStyleSheets(List<Element> styleSheets) {
        this.styleSheets = styleSheets;
    }

    public Element getItemBody() {
        return itemBody;
    }

    public void setItemBody(Element itemBody) {
        this.documentElement.appendChild(itemBody);
        this.itemBody = itemBody;
    }

    public Element getResponseProcessing() {
        return responseProcessing;
    }

    public void setResponseProcessing(Element responseProcessing) {
        this.documentElement.appendChild(responseProcessing);
        this.responseProcessing = responseProcessing;
    }

    public Element getModalFeedback() {
        return modalFeedback;
    }

    public void setModalFeedback(Element modalFeedback) {
        this.documentElement.appendChild(modalFeedback);
        this.modalFeedback = modalFeedback;
    }
}
