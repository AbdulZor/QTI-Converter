package open.edx.qticonverter.services;

import open.edx.qticonverter.models.Library;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.StructuresRepo;
import open.edx.qticonverter.repositories.VersionsRepo;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LibraryService {
    private final VersionsRepo versionsRepo;
    private final StructuresRepo structuresRepo;

    public LibraryService(VersionsRepo versionsRepo, StructuresRepo structuresRepo) {
        this.versionsRepo = versionsRepo;
        this.structuresRepo = structuresRepo;
    }

    public List<Library> getLibraries() {
        ArrayList<Library> libraries = new ArrayList<>();
        //Get Active versions from repo
        List<Version> all = versionsRepo.findAll();

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

