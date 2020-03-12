package open.edx.qticonverter.controllers;

import open.edx.qticonverter.services.xslt.XsltConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.Document;

@RequestMapping("xslt")
@RestController()
public class XsltController {
    private final XsltConverter xsltConverter;

    public XsltController(XsltConverter xsltConverter) {
        this.xsltConverter = xsltConverter;
    }

    public Document getXsltDocument() {
        return null;
    }
}
