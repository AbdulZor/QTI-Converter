package open.edx.qticonverter.repositories;

import open.edx.qticonverter.mongomodel.Definition;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefinitionsRepo extends MongoRepository<Definition, ObjectId> {
}
