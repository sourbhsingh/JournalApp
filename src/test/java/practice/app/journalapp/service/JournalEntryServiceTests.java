package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.JournalEntryRepository;
import practice.app.journalapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JournalEntryServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @InjectMocks
    private JournalEntryService service;

    private User mockUser;
    private JournalEntry mockEntry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = User.builder().username("testuser")
                .password("testuser")
                .journalEntries(new ArrayList<>()).build();

        mockEntry = new JournalEntry();
        mockEntry.setId(new ObjectId());
        mockEntry.setTitle("Title");
        mockEntry.setContent("Content");
    }

    @Test
    void testSaveEntry() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(mockEntry);

        service.saveEntry(mockEntry, "testuser");

        assertTrue(mockUser.getJournalEntries().contains(mockEntry));
        verify(userRepository, times(1)).save(mockUser);
        verify(journalEntryRepository, times(1)).save(mockEntry);
    }

    @Test
    void testGetAll() {
        List<JournalEntry> list = Arrays.asList(mockEntry);
        when(journalEntryRepository.findAll()).thenReturn(list);

        List<JournalEntry> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
        verify(journalEntryRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        when(journalEntryRepository.findById(mockEntry.getId())).thenReturn(Optional.of(mockEntry));

        JournalEntry found = service.findById(mockEntry.getId());

        assertNotNull(found);
        assertEquals("Title", found.getTitle());
    }

    @Test
    void testFindById_NotFound() {
        ObjectId randomId = new ObjectId();
        when(journalEntryRepository.findById(randomId)).thenReturn(Optional.empty());

        JournalEntry found = service.findById(randomId);

        assertNull(found);
    }

    @Test
    void testDeleteById() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        mockUser.getJournalEntries().add(mockEntry);

        service.deleteById(mockEntry.getId(), "testuser");

        assertFalse(mockUser.getJournalEntries().contains(mockEntry));
        verify(userRepository, times(1)).save(mockUser);
        verify(journalEntryRepository, times(1)).deleteById(mockEntry.getId());
    }

    @Test
    void testUpdateEntry_Found() {
        when(journalEntryRepository.findById(mockEntry.getId())).thenReturn(Optional.of(mockEntry));
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(mockEntry);

        JournalEntry update = new JournalEntry();
        update.setTitle("New Title");
        update.setContent("New Content");

        JournalEntry result = service.updateEntry(mockEntry.getId(), update);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(journalEntryRepository, times(1)).save(mockEntry);
    }

    @Test
    void testUpdateEntry_NotFound() {
        ObjectId randomId = new ObjectId();
        when(journalEntryRepository.findById(randomId)).thenReturn(Optional.empty());

        JournalEntry update = new JournalEntry();
        update.setTitle("Doesn't matter");

        JournalEntry result = service.updateEntry(randomId, update);

        assertNull(result);
        verify(journalEntryRepository, never()).save(any());
    }
}
