package open.edx.qticonverter.models.Questions;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

@JacksonXmlRootElement(localName = "problem")
public class Problem {
    @XmlAttribute(name = "p")
    private List p;
    private String solution;
    private String description;

    private Choiceresponse choiceresponse;

    public Choiceresponse getChoiceresponse() {
        return choiceresponse;
    }

    public void setChoiceresponse(Choiceresponse choiceresponse) {
        this.choiceresponse = choiceresponse;
    }

    public List getP() {
        return p;
    }

    public void setP(List p) {
        this.p = p;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
