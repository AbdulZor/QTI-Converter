package open.edx.qticonverter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import open.edx.qticonverter.models.interfaces.BlockTypeable;
import open.edx.qticonverter.mongomodel.Structure;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Course implements BlockTypeable {
    @Id
    private String id;
    private String name;
    private List<Chapter> chapters;
    private List<Sequential> sequentials;
    private List<Vertical> verticals;
    private List<Problem> problems;

    @JsonIgnore
    private Structure structure;

    public Course() {
        this.chapters = new ArrayList<>();
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

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }


    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public List<Sequential> getSequentials() {
        return sequentials;
    }

    public void setSequentials(List<Sequential> sequentials) {
        this.sequentials = sequentials;
    }

    public List<Vertical> getVerticals() {
        return verticals;
    }

    public void setVerticals(List<Vertical> verticals) {
        this.verticals = verticals;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        if (child != null) {
            if (child.getClass().equals(Chapter.class))
                this.chapters.add((Chapter) child);
            else if (child.getClass().equals(Sequential.class))
                this.sequentials.add((Sequential) child);
            else if (child.getClass().equals(Vertical.class))
                this.verticals.add((Vertical) child);
            else if (child.getClass().equals(Problem.class))
                this.problems.add((Problem) child);
        }
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", chapters=" + chapters +
                ", sequentials=" + sequentials +
                ", verticals=" + verticals +
                ", problems=" + problems +
                ", structure=" + structure +
                '}';
    }
}
