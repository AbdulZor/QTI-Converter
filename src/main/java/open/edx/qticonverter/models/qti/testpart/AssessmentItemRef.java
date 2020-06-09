package open.edx.qticonverter.models.qti.testpart;

public class AssessmentItemRef extends SectionPart {
    private String href;

    public AssessmentItemRef(String identifier, String href) {
        super(identifier);
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
