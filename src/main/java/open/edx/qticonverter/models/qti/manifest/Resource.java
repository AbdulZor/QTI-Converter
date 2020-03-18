package open.edx.qticonverter.models.qti.manifest;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.List;

public class Resource {
    private Attr identifier;
    private Attr type;
    private Attr href;
    private Element file;
    private List<Element> dependencies;

    public Resource(Attr identifier, Attr type, Attr href, Element file) {
        this.identifier = identifier;
        this.type = type;
        this.href = href;
        this.file = file;
    }

    public Resource(Attr identifier, Attr type, Attr href, Element file, List<Element> dependencies) {
        this.identifier = identifier;
        this.type = type;
        this.href = href;
        this.file = file;
        this.dependencies = dependencies;
    }

    public Attr getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Attr identifier) {
        this.identifier = identifier;
    }

    public Attr getType() {
        return type;
    }

    public void setType(Attr type) {
        this.type = type;
    }

    public Attr getHref() {
        return href;
    }

    public void setHref(Attr href) {
        this.href = href;
    }

    public Element getFile() {
        return file;
    }

    public void setFile(Element file) {
        this.file = file;
    }

    public List<Element> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Element> dependencies) {
        this.dependencies = dependencies;
    }
}
