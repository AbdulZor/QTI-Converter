package open.edx.qticonverter.models.qti.item.choices;

import open.edx.qticonverter.models.qti.QtiElement;
import org.jdom2.Element;

public abstract class Choice implements QtiElement {
    private Element element;

    // Attributes
    private String identifier;
    private boolean fixed;

    public Choice(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        this.element.setAttribute("identifier", identifier);
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
        this.element.setAttribute("fixed", Boolean.toString(fixed));
    }
}
