package open.edx.qticonverter.models.qti.item.responseDeclarations;

import open.edx.qticonverter.models.qti.item.enums.BaseType;
import open.edx.qticonverter.models.qti.item.enums.Cardinality;
import org.jdom2.Element;

public class ResponseDeclaration21 implements ResponseDeclarationStrategy {
    private Element responseDeclarationElement;
    private Element correctResponse;
    private Element value;
    private String correctValue;

    // Attributes
    private String identifier;
    private BaseType baseType;
    private Cardinality cardinality;

    public ResponseDeclaration21() {
        // Build the DOM element:
        Element responseDeclaration = new Element("responseDeclaration");

        Element correctResponse = new Element("correctResponse");
        Element value = new Element("value");
        value.setText(null);

        // Associate created Elements
        responseDeclaration.addContent(correctResponse);
        correctResponse.addContent(value);

        this.responseDeclarationElement = responseDeclaration;
        this.correctResponse = correctResponse;
        this.value = value;
    }

    @Override
    public Element getElement() {
        return this.responseDeclarationElement;
    }

    public Element getResponseDeclarationElement() {
        return responseDeclarationElement;
    }

    public void setResponseDeclarationElement(Element responseDeclarationElement) {
        this.responseDeclarationElement = responseDeclarationElement;
    }

    public String getCorrectValue() {
        return correctValue;
    }

    public void setCorrectValue(String correctValue) {
        this.value.setText(correctValue);
        this.correctValue = correctValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.responseDeclarationElement.setAttribute("identifier", identifier);
        this.identifier = identifier;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(BaseType baseType) {
        this.responseDeclarationElement.setAttribute("baseType", baseType.name());
        this.baseType = baseType;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.responseDeclarationElement.setAttribute("cardinality", cardinality.name());
        this.cardinality = cardinality;
    }

    public Element getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(Element correctResponse) {
        this.correctResponse = correctResponse;
    }

    public Element getValue() {
        return value;
    }

    public void setValue(Element value) {
        this.value = value;
    }
}
