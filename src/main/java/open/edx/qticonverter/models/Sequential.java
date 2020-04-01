package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Sequential implements BlockTypeable {
    @Id
    private String id;
    private String name;
    private List<Vertical> verticals;

    public Sequential() {
        this.verticals = new ArrayList<>();
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

    public List<Vertical> getVerticals() {
        return verticals;
    }

    public void setVerticals(List<Vertical> verticals) {
        this.verticals = verticals;
    }

    @Override
    public void addChildBlock(BlockTypeable child) {
        if (child != null) {
            verticals.add((Vertical) child);
        }
    }

    @Override
    public String toString() {
        return "Sequential{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", verticals=" + verticals +
                '}';
    }
}
