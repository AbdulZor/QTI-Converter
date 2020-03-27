package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Problem implements BlockTypeable {
    @Id
    private String id;
    private ProblemType problemType;
    private String name;
    private List<String> xml_attributes;
    private String fileIdentifier;
    private List<String> dependencyList;
    private float weight;
    private int max_attempts;

    // add this feature later (QTI 2.2 feature)
    private String styleSheetFilePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getMax_attempts() {
        return max_attempts;
    }

    public void setMax_attempts(int max_attempts) {
        this.max_attempts = max_attempts;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        // NIET IMPLEMENTEREN, omdat problem geen child meer is
        // Anders doe je (addChapter, addVertical ...) handmatig in de (Course, Sequential ...) classes
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id='" + id + '\'' +
                ", problemType=" + problemType +
                ", name='" + name + '\'' +
                ", xml_attributes=" + xml_attributes +
                ", fileIdentifier='" + fileIdentifier + '\'' +
                ", dependencyList=" + dependencyList +
                ", weight=" + weight +
                ", max_attempts=" + max_attempts +
                ", styleSheetFilePath='" + styleSheetFilePath + '\'' +
                '}';
    }
}
