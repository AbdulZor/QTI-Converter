package open.edx.qticonverter.models.qti.manifest;

import org.jdom2.*;

import java.util.List;

public class Manifest21Builder implements ManifestBuilder {
    private final String XMLNS = "http://www.imsglobal.org/xsd/imscp_v1p1";
    private final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private final String XSI_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd " +
            "http://www.imsglobal.org/xsd/imsmd_v1p2 imsmd_v1p2p4.xsd http://www.imsglobal.org/xsd/imsqti_metadata_v2p1  " +
            "http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_metadata_v2p1.xsd";
    private final String IDENTIFIER = "manifest1";

    private Document document;
    private Element rootManifestElement;
    private Element rootResourcesElement;
    private Element rootMetadataElement;

    @Override
    public void initializeDocument() {

        this.document = new Document();

        // create the root element with its attributes
        rootManifestElement = new Element("manifest", XMLNS);
        this.document.setRootElement(rootManifestElement);
        this.rootManifestElement.setNamespace(Namespace.getNamespace(XMLNS));
        Namespace namespaceXsi = Namespace.getNamespace("xsi", XMLNS_XSI);
        this.rootManifestElement.addNamespaceDeclaration(namespaceXsi);
        this.rootManifestElement.setAttribute("schemaLocation", XSI_SCHEMA_LOCATION, namespaceXsi);
        this.rootManifestElement.setAttribute("identifier", IDENTIFIER);

        this.rootMetadataElement = new Element("metadata");
        this.rootManifestElement.addContent(this.rootMetadataElement);

        this.rootResourcesElement = new Element("resources");
        this.rootManifestElement.addContent(this.rootResourcesElement);
    }

    @Override
    public void setMetadata(String schema, String schemaVersion) {
        Element schemaElement = new Element("schema");
        Element schemaVersionElement = new Element("schemaversion");

        schemaElement.setText(schema);
        schemaVersionElement.setText(schemaVersion);

        this.rootMetadataElement.addContent(schemaElement);
        this.rootMetadataElement.addContent(schemaVersionElement);
    }

    @Override
    public void addResource(String identifier, String type, String href, List<String> dependencies) {
        Element resourceElement = new Element("resource");
        Attribute identifierAttr = new Attribute("identifier", identifier);
        Attribute typeAttr = new Attribute("type", type);
        Attribute hrefAttr = new Attribute("href", href);

        Element fileElement = new Element("file");
        fileElement.setAttribute("href", href);

        resourceElement.setAttribute(identifierAttr);
        resourceElement.setAttribute(typeAttr);
        resourceElement.setAttribute(hrefAttr);
        resourceElement.addContent(fileElement);

        if (dependencies != null) {
            for (String dependency : dependencies) {
                Element dependencyElement = new Element("dependency");
                dependencyElement.setAttribute("identifierref", dependency);
                resourceElement.addContent(dependencyElement);
            }
        }

        this.rootResourcesElement.addContent(resourceElement.clone());
    }

    @Override
    public Manifest getResult() {
        return new Manifest(document, rootManifestElement, rootResourcesElement, rootMetadataElement);
    }
}
