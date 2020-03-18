package open.edx.qticonverter.models.qti.manifest;

import org.w3c.dom.Element;

public class Metadata {
    private Element schema;
    private Element schemaVersion;

    public Metadata(Element schema, Element schemaVersion) {
        this.schema = schema;
        this.schemaVersion = schemaVersion;
    }

    public Element getSchema() {
        return schema;
    }

    public void setSchema(Element schema) {
        this.schema = schema;
    }

    public Element getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(Element schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}
