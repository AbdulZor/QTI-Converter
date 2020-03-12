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

    @JsonIgnore
    private Structure structure;

    public Course() {
        this.chapters = new ArrayList<>();
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

    @Override
    public void addChildBlock(BlockTypeable child) {
        if (child != null) {
            this.chapters.add((Chapter) child);
        }
    }
}
