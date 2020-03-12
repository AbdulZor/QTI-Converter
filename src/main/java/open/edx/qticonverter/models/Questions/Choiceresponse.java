package open.edx.qticonverter.models.Questions;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

@JacksonXmlRootElement(localName = "choiceresponse")
public class Choiceresponse {


    @XmlAttribute(name = "correct")
    @JacksonXmlElementWrapper(localName = "checkboxgroup")
    private List choice;



    public List getChoice() {
        return choice;
    }

    public void setChoice(List choice) {
        this.choice = choice;
    }
}
