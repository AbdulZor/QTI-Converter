package open.edx.qticonverter.models.qti.testpart;

import open.edx.qticonverter.models.qti.outcomeDeclarations.OutcomeDeclarationStrategy;
import org.jdom2.Document;

import java.util.ArrayList;
import java.util.List;

public class AssessmentTest {
    private Document document;

    // Attributes
    private String identifier;
    private String title;

    // Containing
    private List<OutcomeDeclarationStrategy> outcomeDeclarations;
    private List<TestPart> testParts;
    private OutcomeProcessing outcomeProcessing;

    public AssessmentTest(String identifier, String title) {
        this.identifier = identifier;
        this.title = title;

        outcomeDeclarations = new ArrayList<>();
        testParts = new ArrayList<>();
    }

    public Document getDocument() {
        return document;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OutcomeDeclarationStrategy> getOutcomeDeclarations() {
        return outcomeDeclarations;
    }

    public void setOutcomeDeclarations(List<OutcomeDeclarationStrategy> outcomeDeclarations) {
        this.outcomeDeclarations = outcomeDeclarations;
    }

    public void addOutcomeDeclaration(OutcomeDeclarationStrategy outcomeDeclaration) {
        this.outcomeDeclarations.add(outcomeDeclaration);
    }

    public List<TestPart> getTestParts() {
        return testParts;
    }

    public void setTestParts(List<TestPart> testParts) {
        this.testParts = testParts;
    }

    public OutcomeProcessing getOutcomeProcessing() {
        return outcomeProcessing;
    }

    public void setOutcomeProcessing(OutcomeProcessing outcomeProcessing) {
        this.outcomeProcessing = outcomeProcessing;
    }
}
