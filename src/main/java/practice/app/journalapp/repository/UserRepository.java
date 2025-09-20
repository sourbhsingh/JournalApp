package practice.app.journalapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;


@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {


    User findByUsername(String username);
}
