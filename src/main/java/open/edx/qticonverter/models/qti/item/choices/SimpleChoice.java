package open.edx.qticonverter.models.qti.item.choices;

import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import org.jdom2.Element;

public class SimpleChoice extends Choice {

    public SimpleChoice(String identifier) {
        super(identifier);
        setElement(new Element("simpleChoice", AssessmentItem21.XMLNS_V21));
        setIdentifier(identifier);
    }
}
