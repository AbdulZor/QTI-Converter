package open.edx.qticonverter.models.qti.item;

import org.w3c.dom.Element;

public class ChoiceBox extends AssessmentItem {

    public ChoiceBox(String identifier, String title, boolean timeDependent) {
        super(identifier, title, timeDependent);
    }

    public void setResponseDeclaration(Element responseDeclaration) {
        super.getDocumentElement().appendChild(responseDeclaration);

    }
}
