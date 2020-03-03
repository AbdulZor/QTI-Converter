package open.edx.qticonverter.models;

import open.edx.qticonverter.models.interfaces.BlockTypeable;

import java.util.ArrayList;
import java.util.List;

public class Course implements BlockTypeable {
    private String id;
    private String name;
    private List<Chapter> chapters;

    public Course() {
        chapters = new ArrayList<>();
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

    @Override
    public void addChildBlock(BlockTypeable child) {
        if (child != null) {
            this.chapters.add((Chapter) child);
        }
    }
}
