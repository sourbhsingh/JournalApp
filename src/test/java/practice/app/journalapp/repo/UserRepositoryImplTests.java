package practice.app.journalapp.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.app.journalapp.repository.UserRepositoryImpl;

@SpringBootTest
public class UserRepositoryImplTests {
    @Autowired
    private UserRepositoryImpl userRepository ;

    @Test
    public void testSentimentAnalysis(){

        userRepository.findAllUserForSentimentAnalysis().forEach(System.out :: println);

    }
}
