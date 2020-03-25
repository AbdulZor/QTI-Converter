package open.edx.qticonverter.repositories;

import open.edx.qticonverter.mongomodel.Structure;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StructuresRepo extends MongoRepository<Structure, ObjectId> {
}
