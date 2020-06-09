package open.edx.qticonverter.models.qti.testpart;

public abstract class SectionPart {
    // Attributes
    private String identifier;

    public SectionPart(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
