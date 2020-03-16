package open.edx.qticonverter.models.Questions;

import java.util.ArrayList;
import java.util.List;

public class CheckboxGroup {
    private List<Choice> choices;

    public CheckboxGroup() {
        choices = new ArrayList<>();
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
