package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import open.edx.qticonverter.models.qti.item.choices.SimpleChoice;
import open.edx.qticonverter.models.qti.item.enums.Orientation;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class ChoiceInteraction21 extends BlockInteractionStrategy {
    // Attributes:
    private String responseIdentifier;
    private boolean shuffle;
    private int maxChoices;
    private int minChoices;
    private Orientation orientation;

    // Contains:
    private List<SimpleChoice> simpleChoiceList;//TODO:: use Strategy Pattern for different Choice algorithms. So first make Choice class!

    public ChoiceInteraction21(String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
        setElement(new Element("choiceInteraction", AssessmentItem21.XMLNS_V21));
        setResponseIdentifier(responseIdentifier);

        this.simpleChoiceList = new ArrayList<>();
    }

    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
        getElement().setAttribute("responseIdentifier", responseIdentifier);
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        getElement().setAttribute("shuffle", Boolean.toString(shuffle));
        this.shuffle = shuffle;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        getElement().setAttribute("maxChoices", Integer.toString(maxChoices));
        this.maxChoices = maxChoices;
    }

    public int getMinChoices() {
        return minChoices;
    }

    public void setMinChoices(int minChoices) {
        getElement().setAttribute("minChoices", Integer.toString(minChoices));
        this.minChoices = minChoices;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        getElement().setAttribute("shuffle", orientation.name());
        this.orientation = orientation;
    }

    public List<SimpleChoice> getSimpleChoiceList() {
        return simpleChoiceList;
    }

    public void setSimpleChoiceList(List<SimpleChoice> simpleChoiceList) {
        this.simpleChoiceList = simpleChoiceList;
        for (SimpleChoice simpleChoice : simpleChoiceList) {
            getElement().addContent(simpleChoice.getElement());
        }
    }

    public void addSimpleChoice(SimpleChoice simpleChoice) {
        this.simpleChoiceList.add(simpleChoice);
        getElement().addContent(simpleChoice.getElement());
    }

}
