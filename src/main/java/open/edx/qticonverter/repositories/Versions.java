package open.edx.qticonverter.repositories;

import open.edx.qticonverter.mongomodel.Version;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Versions extends MongoRepository<Version, ObjectId> {
}
