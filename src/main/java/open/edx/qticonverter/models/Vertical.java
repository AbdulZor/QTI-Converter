package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Vertical implements BlockTypeable {
    @Id
    public String id;
    public String name;
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

    @Override
    public String toString() {
        return "Vertical{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", problems=" + problems +
                '}';
    }
}
