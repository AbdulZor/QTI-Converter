package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;

import java.util.List;

public class Library {
    private String id;
    private String name;
    private List<Problem> problems;

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

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}
