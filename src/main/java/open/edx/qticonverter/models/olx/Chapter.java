package open.edx.qticonverter.models.olx;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements BlockTypeable {
    @Id
    public String id;
    public String name;
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

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sequentials=" + sequentials +
                '}';
    }
}
