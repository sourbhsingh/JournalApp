package practice.app.journalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.app.journalapp.helper.Sentiment;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDTO {
    private String id;
    private String title;
    private String content;
    private Sentiment sentiment;
    private LocalDateTime date;
}
