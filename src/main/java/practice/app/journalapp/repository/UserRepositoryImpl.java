package practice.app.journalapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import practice.app.journalapp.entity.User;


import java.util.List;

@Repository
public class UserRepositoryImpl {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<User> findAllUserForSentimentAnalysis(){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
       return mongoTemplate.find(query,User.class);
    }



    }

