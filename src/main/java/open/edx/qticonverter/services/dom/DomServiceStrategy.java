package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.olx.Course;

import java.io.IOException;

public interface DomServiceStrategy {
    void createQtiPackages() throws IOException;
    void createQtiPackageForId(String id) throws IOException;
}
