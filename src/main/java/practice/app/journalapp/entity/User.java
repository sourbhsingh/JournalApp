package practice.app.journalapp.entity;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class   User {
  @Id
  private ObjectId id ;
  @Indexed(unique = true)
  @NonNull
  private String username ;
  @NonNull
  private String password ;

  private String email;
  private boolean sentimentAnalysis;
  private List<String> role ;
  @DBRef
  List<JournalEntry> journalEntries = new ArrayList<>();
}
