package open.edx.qticonverter.models.Questions;

/**
 * This class is the xml representation of the Single Choice question from the OLX
 */
public class SingleChoice {
    private String title;
    private String label;
    private String description;
    private CheckboxGroup checkboxGroup;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CheckboxGroup getCheckboxGroup() {
        return checkboxGroup;
    }

    public void setCheckboxGroup(CheckboxGroup checkboxGroup) {
        this.checkboxGroup = checkboxGroup;
    }
}
