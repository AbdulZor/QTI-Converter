package open.edx.qticonverter.models.qti.testpart;

import open.edx.qticonverter.models.qti.testpart.enums.NavigationMode;
import open.edx.qticonverter.models.qti.testpart.enums.SubmissionMode;

import java.util.List;

public class TestPart {
    // Attributes
    private String identifier;
    private NavigationMode navigationMode;
    private SubmissionMode submissionMode;

    // Containing
    private List<AssessmentSection> assessmentSections;

    public TestPart(String identifier, NavigationMode navigationMode, SubmissionMode submissionMode) {
        this.identifier = identifier;
        this.navigationMode = navigationMode;
        this.submissionMode = submissionMode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public NavigationMode getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(NavigationMode navigationMode) {
        this.navigationMode = navigationMode;
    }

    public SubmissionMode getSubmissionMode() {
        return submissionMode;
    }

    public void setSubmissionMode(SubmissionMode submissionMode) {
        this.submissionMode = submissionMode;
    }

    public List<AssessmentSection> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<AssessmentSection> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }

    public void addAssessmentSection(AssessmentSection assessmentSection) {
        this.assessmentSections.add(assessmentSection);
    }
}
