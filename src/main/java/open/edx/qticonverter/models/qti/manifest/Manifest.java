package open.edx.qticonverter.models.qti.manifest;

import org.jdom2.Document;
import org.jdom2.Element;

public class Manifest {
    private Document document;
    private Element rootManifestElement;
    private Element rootResourcesElement;
    private Element rootMetadataElement;

    public Manifest(Document document, Element rootManifestElement, Element rootResourcesElement, Element rootMetadataElement) {
        this.document = document;
        this.rootManifestElement = rootManifestElement;
        this.rootResourcesElement = rootResourcesElement;
        this.rootMetadataElement = rootMetadataElement;
    }

    public Document getDocument() {
        return document;
    }

    public Element getRootManifestElement() {
        return rootManifestElement;
    }

    public Element getRootResourcesElement() {
        return rootResourcesElement;
    }

    public Element getRootMetadataElement() {
        return rootMetadataElement;
    }
}
