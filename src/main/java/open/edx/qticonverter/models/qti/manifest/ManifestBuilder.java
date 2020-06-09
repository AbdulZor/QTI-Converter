package open.edx.qticonverter.models.qti.manifest;

import open.edx.qticonverter.models.qti.manifest.enums.SchemaVersion;

import java.util.List;

public interface ManifestBuilder {
    void initializeDocument();
    void setMetadata(String schema, SchemaVersion schemaVersion);
    void addResource(String identifier, String type, String href, List<String> dependencies);
    Manifest getResult();
}
