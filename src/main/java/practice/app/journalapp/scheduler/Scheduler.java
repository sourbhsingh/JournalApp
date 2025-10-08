package practice.app.journalapp.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.helper.Sentiment;
import practice.app.journalapp.repository.UserRepositoryImpl;
import practice.app.journalapp.service.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Scheduler {

    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    EmailService emailService ;



    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);


    @Scheduled(cron = "0 * * * * *")
    public void scheduleEmail(){
        log.info("Email Scheduler Started");
        List<User> userList  = userRepository.findAllUserForSentimentAnalysis();
        log.info("User List Fetched");
        try {
            for (User user : userList)
            {
                List<JournalEntry> journalEntries = user.getJournalEntries();
                log.info("Journal Entries of Users Fetched");
                List<Sentiment> filteredList = journalEntries.stream().filter(n -> n.getDate().isAfter(LocalDateTime.now().minusDays(7))).map(JournalEntry::getSentiment).toList();
                Map<String, Integer> map = new HashMap<>();
                for (Sentiment s : filteredList) {
                    map.put(s.toString(), map.getOrDefault(map.get(s.toString()), 0) + 1);
                }
                String getSentiments = "";
                if (map != null)
                {
                    int maxFreq = 0;
                    for (Map.Entry<String, Integer> entry : map.entrySet())
                    {
                        if (entry.getValue() > maxFreq)
                        {
                            getSentiments = entry.getKey();
                        }
                    }
                }
                if (getSentiments.equals(""))
                {

                }
                else emailService.sendEmail("17sourav.singh@gmail.com", "Sentiment Analysis of last 7 days for" + user.getUsername(), getSentiments);
                log.info("Email Sent to {} successfully",user.getUsername());
            }
        }
        catch (RuntimeException e)
        {
            log.error("Error Accurred while sending scheduled email",e);
            throw  e;
        }
    }

}

