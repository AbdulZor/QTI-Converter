package open.edx.qticonverter.mongomodel;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Document(collection = "modulestore.structures")
public class Structure {

    @Id
    private String id;
    private List<String> root;
    private List<Map> blocks;
    private List<Map> fields;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public List<String> getRoot() {
        return root;
    }

    public void setRoot(List<String> root) {
        this.root = root;
    }

    public List<Map> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Map> blocks) {
        this.blocks = blocks;
    }

    public List<Map> getFieldsOfCourse(ObjectId id) {
        List<Object> fields = blocks.stream().filter(blocktype -> blocktype.get("block_type").equals(id)).
                map(block -> block.get("fields")).collect(Collectors.toList());
        fields.forEach(System.out::println);
        return this.fields;
    }

    public void setFields(List<Map> fields) {
        this.fields = fields;
    }
}
