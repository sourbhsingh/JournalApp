package practice.app.journalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id ;

    private String username ;

    private String password ;

    private String email;
    private boolean sentimentAnalysis;
    private List<String> role ;

    List<JournalEntryDTO> journalEntries = new ArrayList<>();

}
