package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.item.enums.Orientation;
import org.jdom2.Element;

public class ChoiceInteraction extends BlockInteractionStrategy {
    private Element element;

    // Attributes:
    private boolean shuffle;
    private int maxChoices;
    private int minChoices;
    private Orientation orientation;

    // Contains:
//    private List<SimpleChoice> //TODO:: use Strategy Pattern for different Choice algorithms. So first make Choice class!

    public ChoiceInteraction() {
        this.element = new Element("choiceInteraction");;
    }

    @Override
    public Element getElement() {
        return element;
    }
}
