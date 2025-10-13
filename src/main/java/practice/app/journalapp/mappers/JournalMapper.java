package practice.app.journalapp.mappers;

import org.bson.types.ObjectId;
import practice.app.journalapp.dto.JournalEntryDTO;
import practice.app.journalapp.entity.JournalEntry;

public class JournalMapper {

    public static JournalEntryDTO toDTO(JournalEntry entity) {
        return new JournalEntryDTO(
                entity.getId() != null ? entity.getId().toHexString() : null,
                entity.getTitle(),
                entity.getContent(),
                entity.getSentiment(),
                entity.getDate()
        );
    }

    public static JournalEntry toEntity(JournalEntryDTO dto) {
        return new JournalEntry(
                dto.getId() != null ? new ObjectId(dto.getId()) : null,
                dto.getTitle(),
                dto.getContent(),
                dto.getSentiment(),
                dto.getDate()
        );
    }
}
