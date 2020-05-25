package open.edx.qticonverter.controllers;

import open.edx.qticonverter.models.olx.Library;
import open.edx.qticonverter.mongomodel.Structure;
import open.edx.qticonverter.services.LibraryService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping("libraries")
@RestController()
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping()
    public List<Library> courses() {
        return libraryService.getLibraries();
    }

    @GetMapping("/{id}")
    public Optional<Structure> getCourse(@PathVariable ObjectId id) {
//        return structures.findById(id);
        return null;
    }
}
