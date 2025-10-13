package practice.app.journalapp.mappers;
import org.bson.types.ObjectId;
import practice.app.journalapp.dto.UserDTO;
import practice.app.journalapp.entity.User;
public class UserMapper {


    public static UserDTO toDTO(User entity) {
        return new UserDTO(
                entity.getId() != null ? entity.getId().toHexString() : null,
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.isSentimentAnalysis(),
                entity.getRole(),
                entity.getJournalEntries().stream().map(JournalMapper::toDTO).toList()
        );
    }

    public static User toEntity(UserDTO dto) {
        return new User(
                dto.getId() != null ? new ObjectId(dto.getId()) : null,
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.isSentimentAnalysis(),
                dto.getRole(),
                dto.getJournalEntries().stream().map(JournalMapper::toEntity).toList()
        );
    }
}
