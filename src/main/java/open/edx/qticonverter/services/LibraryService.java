package open.edx.qticonverter.services;

import open.edx.qticonverter.models.Course;
import open.edx.qticonverter.models.Library;
import open.edx.qticonverter.mongomodel.Structure;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.Structures;
import open.edx.qticonverter.repositories.Versions;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class LibraryService {
    private final Versions versions;
    private final Structures structures;

    public LibraryService(Versions versions, Structures structures) {
        this.versions = versions;
        this.structures = structures;
    }

    public List<Library> getLibraries() {
        ArrayList<Library> libraries = new ArrayList<>();
        //Get Active versions from repo
        List<Version> all = versions.findAll();

        for (Version version : all) {
            // Map each value of active_versions to create the Course object
            if (version.getVersions().getLibrary() != null) {
                Library library = new Library();
                library.setId(version.getId());
                library.setName(version.getCourse());

                //Get published branch
                ObjectId publishedBranchId = version.getVersions().getPublished_branch();
                System.out.println("Published branch id: " + version.getVersions().getPublished_branch());

                libraries.add(library);
            }
        }
        return libraries;
    }
}

