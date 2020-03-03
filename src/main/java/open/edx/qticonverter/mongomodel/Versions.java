package open.edx.qticonverter.mongomodel;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

public class Versions {

    @Field(name = "published-branch")
    private ObjectId published_branch;
    @Field(name = "draft-branch")
    private ObjectId draft_branch;
    private ObjectId library;


    public ObjectId getPublished_branch() {
        return published_branch;
    }

    public void setPublished_branch(ObjectId published_branch) {
        this.published_branch = published_branch;
    }

    public ObjectId getDraft_branch() {
        return draft_branch;
    }

    public void setDraft_branch(ObjectId draft_branch) {
        this.draft_branch = draft_branch;
    }

    public ObjectId getLibrary() {
        return library;
    }

    public void setLibrary(ObjectId library) {
        this.library = library;
    }
}
