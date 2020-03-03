package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

public class Problem implements BlockTypeable, XmlAttributes {
    @Id
    private String id;
    private String name;
    private List<String> xml_attributes;
    private Map definition;

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

    public Map getDefinition() {
        return definition;
    }

    public void setDefinition(Map definition) {
        this.definition = definition;
    }

    public List<String> getXml_attributes() {
        return xml_attributes;
    }

    public void setXml_attributes(List<String> xml_attributes) {
        this.xml_attributes = xml_attributes;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        // NIET IMPLEMENTEREN, omdat problem geen child meer is
        // Anders doe je (addChapter, addVertical ...) handmatig in de (Course, Sequential ...) classes
    }
}
