package open.edx.qticonverter.models.olx;

import open.edx.qticonverter.mongomodel.Definition;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Problem implements BlockTypeable {
    @Id
    private String id;
    private ProblemType problemType;
    private String name;
    private String fileIdentifier;
    private List<String> dependencyList;
    private float weight;
    private int max_attempts;
    private boolean timeDependent;
    private Definition definition;

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

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
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
                ", fileIdentifier='" + fileIdentifier + '\'' +
                ", dependencyList=" + dependencyList +
                ", weight=" + weight +
                ", max_attempts=" + max_attempts +
                ", definition=" + definition +
                ", styleSheetFilePath='" + styleSheetFilePath + '\'' +
                '}';
    }

    public boolean getTimeDependent() {
        return timeDependent;
    }

    public void setTimeDependent(boolean timeDependent) {
        this.timeDependent = timeDependent;
    }
}
