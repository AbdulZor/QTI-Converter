package open.edx.qticonverter.models.qti.item.outcomeDeclarations;

import open.edx.qticonverter.models.qti.item.AssessmentItem21;
import open.edx.qticonverter.models.qti.item.enums.BaseType;
import open.edx.qticonverter.models.qti.item.enums.Cardinality;
import org.jdom2.Element;

public class MaxScoreOutcomeDeclaration implements OutcomeDeclarationStrategy {
    private Element outcomeRootElement;


    @Override
    public Element createOutcomeDeclaration(float value) {
        this.outcomeRootElement = new Element("outcomeDeclaration", AssessmentItem21.XMLNS_V21);
        this.outcomeRootElement.setAttribute("baseType" , BaseType.FLOAT.name().toLowerCase());
        this.outcomeRootElement.setAttribute("cardinality" , Cardinality.single.name());
        this.outcomeRootElement.setAttribute("identifier" , "MAX_SCORE");

        Element defaultValueEl = new Element("defaultValue", AssessmentItem21.XMLNS_V21);
        Element valueEl = new Element("value", AssessmentItem21.XMLNS_V21);
        valueEl.addContent(Float.toString(value));

        this.outcomeRootElement.addContent(defaultValueEl.addContent(valueEl));
        return this.outcomeRootElement;
    }

    @Override
    public Element getElement() {
        return this.outcomeRootElement;
    }
}
