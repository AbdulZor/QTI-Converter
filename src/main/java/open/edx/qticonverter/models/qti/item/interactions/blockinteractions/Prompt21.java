package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.QtiElement;
import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import org.jdom2.Element;

public class Prompt21 implements QtiElement {
    private Element element;

    public Prompt21() {
        this.element = new Element("prompt", AssessmentItem21.XMLNS_V21);
    }

    @Override
    public Element getElement() {
        return element;
    }
}
