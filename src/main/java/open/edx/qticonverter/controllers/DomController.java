package open.edx.qticonverter.controllers;

import open.edx.qticonverter.services.dom.DomService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("dom")
@RestController()
public class DomController {
    private final DomService domService;

    public DomController(DomService domService) {
        this.domService = domService;
    }

    @RequestMapping()
    public void createQtiPackages() {
        this.domService.createQtiPackages();
    }

    @RequestMapping("/{courseId}")
    public void createQtiPackageForId(@PathVariable(name = "courseId") String id) {
        this.domService.createQtiPackageForId(id);
    }
}
