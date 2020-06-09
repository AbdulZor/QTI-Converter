package open.edx.qticonverter.models.qti.testpart;

import java.util.ArrayList;
import java.util.List;

public class AssessmentSection extends SectionPart {
    // Attributes
    private String title;
    private boolean visible;

    // Containing
    private List<SectionPart> sectionParts;

    public AssessmentSection(String identifier, String title, boolean visible) {
        super(identifier);
        this.title = title;
        this.visible = visible;

        this.sectionParts = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<SectionPart> getSectionParts() {
        return sectionParts;
    }

    public void setSectionParts(List<SectionPart> sectionParts) {
        this.sectionParts = sectionParts;
    }

    public void addSectionPart(SectionPart sectionPart) {
        this.sectionParts.add(sectionPart);

    }
}
