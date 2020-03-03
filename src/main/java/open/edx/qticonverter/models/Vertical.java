package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.models.interfaces.XmlAttributes;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Vertical implements BlockTypeable, XmlAttributes {
    @Id
    public String id;
    public String name;
    private List<String> xml_attributes;
    public List<Problem> problems;

    public Vertical() {
        this.problems = new ArrayList<>();
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

    public List<String> getXml_attributes() {
        return xml_attributes;
    }

    @Override
    public void setXml_attributes(List<String> xml_attributes) {
        this.xml_attributes = xml_attributes;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        problems.add((Problem) child);
    }
}
