package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.item.enums.QtiBodyElement21;
import open.edx.qticonverter.models.qti.item.interactions.Block;
import org.jdom2.Element;

public abstract class BlockInteractionStrategy implements Block {
    private Element element; // Each subclass needs to define this element in it's constructor
    private Prompt prompt;

    public BlockInteractionStrategy() {
    }

    /**
     * Append a text element to the prompt
     *
     * @param text        the new text that gets appended
     * @param bodyElement the wrapper of the text element in the prompt
     * @return the created wrapper element of the text
     */
    public Element appendToPrompt(String text, QtiBodyElement21 bodyElement) {
        Element textBodyElement = new Element(bodyElement.name());
        textBodyElement.setText(text);

        return textBodyElement;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
        this.element.addContent(prompt.getElement());
    }
}
