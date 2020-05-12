package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.QtiElement;
import org.jdom2.Element;

public class Prompt implements QtiElement {
    private Element element;

    public Prompt() {
        this.element = new Element("prompt");
    }

    @Override
    public Element getElement() {
        return element;
    }
}
