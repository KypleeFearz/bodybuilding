package ch.zhaw.bodybuilding.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.zhaw.bodybuilding.model.Beitrag;
import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.repository.ForumRepository;
import ch.zhaw.bodybuilding.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ForumServiceTest {
    @Mock
    private ForumRepository forumRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ForumService forumService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBeitrag() {
        Forum forum = new Forum();
        forum.setCreator("Marko");
        String user = "Marko";
        String text = "Hallo";
        User benutzer = new User();
        benutzer.setName(user);
        benutzer.setEmail("test");
        when(forumRepository.findFirstByCreator("Marko")).thenReturn(forum);
        when(userRepository.findFirstByName("Marko")).thenReturn(benutzer);
        Optional<Forum> result = forumService.createBeitrag(user, text, forum.getCreator());
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getBeitraege().length);
    }
    @Test
    public void testEditBeitrag() {
        Forum forum = new Forum();
        forum.setCreator("Marko");
        String newText = "Grüezi";
        String oldText = "Hallo";
        User benutzer = new User();
        benutzer.setName("Nikola");
        benutzer.setEmail("test");
        Beitrag beitrag = new Beitrag();
        beitrag.setText(oldText);
        beitrag.setUser(benutzer.getName());
        Beitrag[] beitraege = {beitrag};
        forum.setBeitraege(beitraege);
        when(forumRepository.findFirstByCreator("Marko")).thenReturn(forum);
        Optional<Forum> result = forumService.editBeitrag(newText, oldText, forum.getCreator(), benutzer.getName());
        assertTrue(result.isPresent());
        assertEquals(newText, result.get().getBeitraege()[0].getText());
    }
    @Test
    public void testDeleteBeitrag() {
        Forum forum = new Forum();
        forum.setCreator("Marko");
        String text = "Grüezi";
        String user = "Nikola";
        User benutzer = new User();
        benutzer.setName("Nikola");
        benutzer.setEmail("test");
        Beitrag beitrag = new Beitrag();
        beitrag.setText(text);
        beitrag.setUser(user);
        Beitrag[] beitraege = {beitrag};
        forum.setBeitraege(beitraege);
        when(forumRepository.findFirstByCreator("Marko")).thenReturn(forum);
        Optional<Forum> result = forumService.deleteBeitrag(text, benutzer.getName(), forum.getCreator(), null, user);
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getBeitraege().length);
    }
}