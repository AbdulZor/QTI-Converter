package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements BlockTypeable, XmlAttributes {
    @Id
    public String id;
    public String name;
    private List<String> xml_attributes;
    public List<Sequential> sequentials;

    public Chapter() {
        this.sequentials = new ArrayList<>();
    }

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

    public List<Sequential> getSequentials() {
        return sequentials;
    }

    public void setSequentials(List<Sequential> sequentials) {
        this.sequentials = sequentials;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        if (child != null) {
            this.sequentials.add((Sequential) child);
        }
    }

    public List<String> getXml_attributes() {
        return xml_attributes;
    }

    public void setXml_attributes(List<String> xml_attributes) {
        this.xml_attributes = xml_attributes;
    }
}
