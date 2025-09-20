package practice.app.journalapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.app.journalapp.entity.JournalEntry;
@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

}
