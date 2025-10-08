package practice.app.journalapp.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepositoryImpl;
import practice.app.journalapp.service.EmailService;
import practice.app.journalapp.service.SentimentAnalysisService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailScheduler {

    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    EmailService emailService ;

    @Autowired
    SentimentAnalysisService sentimentService;

    @Scheduled(cron = "0 0 9 * * Sun")
    public void scheduleEmail(){
        List<User> userList  = userRepository.findAllUserForSentimentAnalysis();
        for(User user : userList){
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<String> filteredList = journalEntries.stream().filter(n-> n.getDate().isAfter(LocalDateTime.now().minusDays(7))).map(JournalEntry::getContent).toList();
            String entries =  String.join(" ",filteredList);
            String getSentiments = sentimentService.getSentiment(entries);
            emailService.sendEmail("17sourav.singh@gmail.com","Sentiment Analysis of last 7 days", getSentiments);
        }
    }

}

