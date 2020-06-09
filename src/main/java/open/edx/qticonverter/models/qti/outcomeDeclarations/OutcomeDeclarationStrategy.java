package open.edx.qticonverter.models.qti.outcomeDeclarations;

import open.edx.qticonverter.models.qti.QtiElement;
import org.jdom2.Element;


public interface OutcomeDeclarationStrategy extends QtiElement {
    Element createOutcomeDeclaration(float value);
}
