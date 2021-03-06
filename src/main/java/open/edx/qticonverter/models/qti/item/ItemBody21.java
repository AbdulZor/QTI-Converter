package open.edx.qticonverter.models.qti.item;

import open.edx.qticonverter.models.qti.item.enums.QtiBodyElement21;
import open.edx.qticonverter.models.qti.item.interactions.Block;
import org.jdom2.Element;

public class ItemBody21 {
    private Element element;

    public ItemBody21() {
        this.element = new Element("itemBody", AssessmentItem21.XMLNS_V21);
    }

    public Element getElement() {
        return element;
    }

    /**
     * Append a text element to the itemBody
     * @param text the new text that gets appended
     * @param bodyElement the wrapper of the text element in the itemBody
     * @return the created wrapper element of the text
     */
    public Element append(String text, QtiBodyElement21 bodyElement) {
        Element textBodyElement = new Element(bodyElement.name(), AssessmentItem21.XMLNS_V21);
        textBodyElement.setText(text);

        return this.element.addContent(textBodyElement);
    }

    /**
     * Append a new Block which
     * @param block
     * @return
     */
    public Element append(Block block) {
        return this.element.addContent(block.getElement());
    }

    public Element append(Element element) {
        return getElement().addContent(element);
    }
}
