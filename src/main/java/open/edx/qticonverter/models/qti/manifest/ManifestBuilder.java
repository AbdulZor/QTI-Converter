package open.edx.qticonverter.models.qti.manifest;

import java.util.List;

public interface ManifestBuilder {
    void initializeDocument();
    void setMetadata(String schema, String schemaVersion);
    void addResource(String identifier, String type, String href, List<String> dependencies);
    Manifest getResult();
}
