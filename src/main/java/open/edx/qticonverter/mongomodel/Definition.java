package open.edx.qticonverter.mongomodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "modulestore.definitions")
public class Definition {
    @Id
    private String id;

    @Field(name = "block_type")
    private String blockType;

    private Map<String, String> fields;

    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getData() {
        if (fields != null){
            data = fields.get("data").toString();
        }
        return data;
    }

    public void setData(String data) {
        if (fields != null){
            fields.put("data", data);
        }
    }
}
