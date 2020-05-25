package open.edx.qticonverter.models.qti.item.interactions.blockinteractions;

import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import open.edx.qticonverter.models.qti.item.enums.QtiBodyElement21;
import open.edx.qticonverter.models.qti.item.interactions.Block;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

public abstract class BlockInteractionStrategy implements Block {
    private Element element; // Each subclass needs to define this element in it's constructor
    private Prompt21 prompt;

    public BlockInteractionStrategy() {
    }

    /**
     * Append a text element to the prompt
     *
     * @param text        the new text that gets appended
     * @param bodyElement the wrapper of the text element in the prompt
     * @return the created wrapper element of the text
     */
    //TODO::!!!!! ONLY FOR QTI 2.2 AND ABOVE
    public Element appendToPrompt(String text, QtiBodyElement21 bodyElement) {
        Element textBodyElement = new Element(bodyElement.name());
        textBodyElement.setText(text);
        getPrompt().getElement().addContent(textBodyElement);
//        this.element.addContent(textBodyElement);

        return textBodyElement;
    }

    public Element appendToPrompt(Text element) {
        if (this.prompt == null) {
            setPrompt(new Prompt21());
        }
        return getPrompt().getElement().addContent(new Element(QtiBodyElement21.br.name(), AssessmentItem21.XMLNS_V21)).addContent(element);
    }

    public Prompt21 getPrompt() {
        return prompt;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public void setElement(Element choiceInteraction) {
        this.element = choiceInteraction;
    }

    public void setPrompt(Prompt21 prompt) {
        this.prompt = prompt;

        if (prompt == null) {
//            if (this.prompt != null){
            this.element.removeChild("prompt");
//            }
        } else {
            this.element.addContent(prompt.getElement());
        }

    }
}
