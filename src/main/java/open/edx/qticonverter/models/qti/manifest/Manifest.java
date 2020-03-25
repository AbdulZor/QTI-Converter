package open.edx.qticonverter.models.qti.manifest;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class Manifest {
    private final String XMLNS = "http://www.imsglobal.org/xsd/imscp_v1p1";
    private final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private final String XSI_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd " +
            "http://www.imsglobal.org/xsd/imsmd_v1p2 imsmd_v1p2p4.xsd http://www.imsglobal.org/xsd/imsqti_metadata_v2p1  " +
            "http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_metadata_v2p1.xsd";
    private final String IDENTIFIER = "manifest1";

    private Document document;
    private Element rootManifestElement;
    private Element rootResourcesElement;
    private Metadata metadata;
    private List<Resource> resources;
    private Element rootMetadataElement;

    public Manifest() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        assert builder != null;
        this.document = builder.newDocument();
        this.resources = new ArrayList<>();

        // create the root element with its attributes
        rootManifestElement = this.document.createElement("manifest");
        this.document.appendChild(rootManifestElement);
        this.rootManifestElement.setAttribute("xmlns", XMLNS);
        this.rootManifestElement.setAttribute("xmlns:xsi", XMLNS_XSI);
        this.rootManifestElement.setAttribute("xsi:schemaLocation", XSI_SCHEMA_LOCATION);
        this.rootManifestElement.setAttribute("identifier", IDENTIFIER);

        this.rootMetadataElement = this.document.createElement("metadata");
        this.rootManifestElement.appendChild(this.rootMetadataElement);

        this.rootResourcesElement = this.document.createElement("resources");
        this.rootManifestElement.appendChild(this.rootResourcesElement);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Element getRootManifestElement() {
        return rootManifestElement;
    }

    public void setRootManifestElement(Element rootManifestElement) {
        this.rootManifestElement = rootManifestElement;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(String schema, String schemaVersion) {
        Element schemaElement = document.createElement("schema");
        Element schemaVersionElement = document.createElement("schemaversion");

        schemaElement.setTextContent(schema);
        schemaVersionElement.setTextContent(schemaVersion);

        this.rootMetadataElement.appendChild(schemaElement);
        this.rootMetadataElement.appendChild(schemaVersionElement);
        this.metadata = new Metadata(schemaElement, schemaVersionElement);
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Element getRootResourcesElement() {
        return rootResourcesElement;
    }

    public void setRootResourcesElement(Element rootResourcesElement) {
        this.rootResourcesElement = rootResourcesElement;
    }

    public Element getRootMetadataElement() {
        return rootMetadataElement;
    }

    public void setRootMetadataElement(Element rootMetadataElement) {
        this.rootMetadataElement = rootMetadataElement;
    }

    public void addResource(Resource resource) {
        this.resources.add(resource);
    }

    public void addResource(String identifier, String type, String href, List<String> dependencies) {
        Element resourceElement = document.createElement("resource");
        Attr identifierAttr = document.createAttribute("identifier");
        Attr typeAttr = document.createAttribute("type");
        Attr hrefAttr = document.createAttribute("href");

        identifierAttr.setValue(identifier);
        typeAttr.setValue(type);
        hrefAttr.setValue(href);

        Element fileElement = document.createElement("file");
        fileElement.setAttribute("href", href);

        resourceElement.setAttributeNode(identifierAttr);
        resourceElement.setAttributeNode(typeAttr);
        resourceElement.setAttributeNode(hrefAttr);
        resourceElement.appendChild(fileElement);

        List<Element> dependencyList = new ArrayList<>();
        if (dependencies != null) {
            for (String dependency : dependencies) {
                Element dependencyElement = this.document.createElement("dependency");
                dependencyElement.setAttribute("identifierref", dependency);
                resourceElement.appendChild(dependencyElement);
                dependencyList.add(dependencyElement);
            }
        }

        this.rootResourcesElement.appendChild(resourceElement.cloneNode(true));

        Resource resource = new Resource(identifierAttr, typeAttr, hrefAttr, fileElement, dependencyList);
        this.resources.add(resource);
    }
}
