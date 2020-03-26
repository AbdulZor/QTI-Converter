package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class Problem implements BlockTypeable, XmlAttributes {
    @Id
    private String id;
    private String name;
    private List<String> xml_attributes;
    private String fileIdentifier;
    private List<String> dependencyList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getXml_attributes() {
        return xml_attributes;
    }

    public void setXml_attributes(List<String> xml_attributes) {
        this.xml_attributes = xml_attributes;
    }

    public String getFileIdentifier() {
        Assert.notNull(id, "ID of problem cannot be \"null\"");
        Assert.notNull(name, "Name of problem cannot be \"null\"");

        this.fileIdentifier = id + "-" + name;

        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public List<String> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(List<String> dependencyList) {
        this.dependencyList = dependencyList;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        // NIET IMPLEMENTEREN, omdat problem geen child meer is
        // Anders doe je (addChapter, addVertical ...) handmatig in de (Course, Sequential ...) classes
    }
}
