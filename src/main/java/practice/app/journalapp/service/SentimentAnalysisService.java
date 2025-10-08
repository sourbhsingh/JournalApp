package practice.app.journalapp.service;

import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {

    public String getSentiment(String s){
        return "I love your Sentiments: "+s;
    }
}
