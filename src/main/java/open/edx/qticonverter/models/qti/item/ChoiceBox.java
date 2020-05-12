package open.edx.qticonverter.models.qti.item;

import org.jdom2.Element;

public class ChoiceBox extends AssessmentItem2 {

    public ChoiceBox(String identifier, String title, boolean timeDependent, boolean adaptive) {
        super(identifier, title, timeDependent, adaptive);
    }

    public void setResponseDeclaration(Element responseDeclaration) {
        super.getDocumentElement().addContent(responseDeclaration);

    }
}
