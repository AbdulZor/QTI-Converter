package open.edx.qticonverter.models.qti.item;

import open.edx.qticonverter.models.qti.item.enums.QtiVersion;
import open.edx.qticonverter.models.qti.item.interactions.Block;
import open.edx.qticonverter.models.qti.item.outcomeDeclarations.OutcomeDeclarationStrategy;
import open.edx.qticonverter.models.qti.item.responseDeclarations.ResponseDeclarationStrategy;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.ArrayList;
import java.util.List;

public class AssessmentItem21 {
    public static final int STRING256_CONSTRAINT = 256;
    public static final String XMLNS_V21 = "http://www.imsglobal.org/xsd/imsqti_v2p1";
    public static final String XMLNS_XSI_V21 = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSI_SCHEMA_LOCATION_V21 = "http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1.xsd";
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
    private List<ResponseDeclarationStrategy> responseDeclarations;
    private List<OutcomeDeclarationStrategy> outcomeDeclarations;
    private List<Element> styleSheets;
    private ItemBody21 itemBody;
    private List<Block> interactionsList;
    private Element responseProcessing;
    private Element modalFeedback;


    public AssessmentItem21(String identifier, String title, boolean timeDependent, boolean adaptive, QtiVersion qtiVersion) {
        this.identifier = identifier;
        this.title = title;
        this.timeDependent = timeDependent;
        this.adaptive = adaptive;

        this.interactionsList = new ArrayList<>();
        this.responseDeclarations = new ArrayList<>();
        this.outcomeDeclarations = new ArrayList<>();
        this.styleSheets = new ArrayList<>();

        this.document = new Document();

        setDocumentElement(new Element("assessmentItem"));

        documentElement.setAttribute("identifier", identifier);
        documentElement.setAttribute("title", title);
        documentElement.setAttribute("adaptive", Boolean.toString(adaptive));
        documentElement.setAttribute("timeDependent", Boolean.toString(timeDependent));
    }

    public AssessmentItem21(String identifier, String title, boolean timeDependent, boolean adaptive) {
        this(identifier, title, timeDependent, adaptive, null);
    }

    //TODO:: This method should be the build() method in the Builder Pattern (which needs to be implemented)

    /**
     * This method should be called as last to create the DOM object as a whole
     */
    public void buildDom() {
        // Append to the DOM in the correct order conform the QTI standard
        for (ResponseDeclarationStrategy responseDeclaration : this.responseDeclarations) {
            this.documentElement.addContent(responseDeclaration.getElement());
        }

        for (OutcomeDeclarationStrategy outcomeDeclaration : this.outcomeDeclarations) {
            this.documentElement.addContent(outcomeDeclaration.getElement());
        }

        this.documentElement.addContent(itemBody.getElement());
//        this.documentElement.appendChild(responseProcessing);

        // Make sure that each element in the doc uses the correct namespace, else children will get empty namespace URI
        // e.g. <itemBody xmlns="">

        this.documentElement.setNamespace(Namespace.getNamespace(XMLNS_V21));
        Namespace namespaceXsi = Namespace.getNamespace("xsi", XMLNS_XSI_V21);
        this.documentElement.addNamespaceDeclaration(namespaceXsi);
        this.documentElement.setAttribute("schemaLocation", XSI_SCHEMA_LOCATION_V21, namespaceXsi);
    }

    public List<Block> getInteractionsList() {
        return interactionsList;
    }

    public void setInteractionsList(List<Block> interactionsList) {
        this.interactionsList = interactionsList;
    }

    public void addInteraction(Block interaction) {
        this.interactionsList.add(interaction);
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
        if (!this.document.hasRootElement()) {
            this.document.setRootElement(documentElement);
        } else {
            this.document.removeContent(this.getDocumentElement());
        }
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


    public List<ResponseDeclarationStrategy> getResponseDeclarations() {
        return responseDeclarations;
    }

    public void setResponseDeclarations(List<ResponseDeclarationStrategy> responseDeclarations) {
        this.responseDeclarations = responseDeclarations;
    }

    public void addResponseDeclaration(ResponseDeclarationStrategy responseDeclaration) {
        this.responseDeclarations.add(responseDeclaration);
    }

    public List<OutcomeDeclarationStrategy> getOutcomeDeclarations() {
        return outcomeDeclarations;
    }

    public void setOutcomeDeclarations(List<OutcomeDeclarationStrategy> outcomeDeclarations) {
        this.outcomeDeclarations = outcomeDeclarations;
    }

    public void addOutcomeDeclaration(OutcomeDeclarationStrategy outcomeDeclaration) {
        this.outcomeDeclarations.add(outcomeDeclaration);
    }

    public List<Element> getStyleSheets() {
        return styleSheets;
    }

    public void addStyleSheet(Element styleSheet) {
        this.documentElement.addContent(styleSheet);
        this.styleSheets.add(styleSheet);
    }

    public void setStyleSheets(List<Element> styleSheets) {
        this.styleSheets = styleSheets;
    }

    public ItemBody21 getItemBody() {
        return itemBody;
    }

    public void setItemBody(ItemBody21 itemBody) {
        this.itemBody = itemBody;
    }

    public Element getResponseProcessing() {
        return responseProcessing;
    }

    public void setResponseProcessing(Element responseProcessing) {
        this.documentElement.addContent(responseProcessing);
        this.responseProcessing = responseProcessing;
    }

    public Element getModalFeedback() {
        return modalFeedback;
    }

    public void setModalFeedback(Element modalFeedback) {
        this.documentElement.addContent(modalFeedback);
        this.modalFeedback = modalFeedback;
    }

    @Override
    public String toString() {
        return "AssessmentItem2{" +
                "document=" + document +
                ", documentElement=" + documentElement +
                ", identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", label='" + label + '\'' +
                ", lang='" + lang + '\'' +
                ", adaptive=" + adaptive +
                ", timeDependent=" + timeDependent +
                ", toolName='" + toolName + '\'' +
                ", responseDeclarations=" + responseDeclarations +
                ", outcomeDeclarations=" + outcomeDeclarations +
                ", styleSheets=" + styleSheets +
                ", itemBody=" + itemBody +
                ", interactionsList=" + interactionsList +
                ", responseProcessing=" + responseProcessing +
                ", modalFeedback=" + modalFeedback +
                '}';
    }
}
