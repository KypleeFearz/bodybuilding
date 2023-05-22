package ch.zhaw.bodybuilding.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.bodybuilding.model.Beitrag;
import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.repository.ForumRepository;
import ch.zhaw.bodybuilding.repository.UserRepository;

@Service
public class ForumService {
    @Autowired
    ForumRepository forumRepository;
    @Autowired
    UserRepository userRepository;

    public Optional<Forum> createBeitrag(String name, String text, String creator) {
        User userCreate = userRepository.findFirstByName(name);
        Forum forumAssign = forumRepository.findFirstByCreator(creator);
        if (userCreate != null && forumAssign != null && text != "") {
            Beitrag beitrag = new Beitrag();
            beitrag.setText(text);
            beitrag.setUser(name);
            if (forumAssign.getBeitraege() != null) {
                Beitrag[] beitraege = Arrays.copyOf(forumAssign.getBeitraege(), forumAssign.getBeitraege().length + 1);
                beitraege[forumAssign.getBeitraege().length] = beitrag;
                forumAssign.setBeitraege(beitraege);
            } else {
                Beitrag[] beitraege = { beitrag };
                forumAssign.setBeitraege(beitraege);
            }
            forumRepository.save(forumAssign);
            return Optional.of(forumAssign);
        }
        return Optional.empty();
    }

    public Optional<Forum> editBeitrag(String newText, String oldText, String creator, String beitragCreator) {
        Forum forum = forumRepository.findFirstByCreator(creator);
        System.out.println(forum);
        if (forum != null && newText != "" && oldText != "" && beitragCreator != null) {
            Beitrag beitrag = new Beitrag();
            beitrag.setText(newText);
            beitrag.setUser(beitragCreator);
            Beitrag[] beitraege = forum.getBeitraege();
            for (int i = 0; i < beitraege.length; i++) {
                if (beitraege[i].getUser().equals(beitragCreator) && beitraege[i].getText().equals(oldText)) {
                    beitraege[i] = beitrag;
                }
            }
            forum.setBeitraege(beitraege);
            forumRepository.save(forum);
            return Optional.of(forum);
        }
        return Optional.empty();
    }

    public Optional<Forum> deleteBeitrag(String text, String beitragCreator, String creator, List<String> userRole,
            String user) {
        Forum forum = forumRepository.findFirstByCreator(creator);
        if ((forum != null && text != "" && beitragCreator != null)
                && (user.equals(beitragCreator) || (userRole != null && userRole.contains("admin")))) {
            Beitrag[] beitraege = forum.getBeitraege();
            Beitrag[] newBeitraege = new Beitrag[beitraege.length - 1];
            if (newBeitraege.length != 0) {

                int k = 0;
                for (int i = 0; i < beitraege.length; i++) {
                    if (beitraege[i].getUser().equals(beitragCreator) && beitraege[i].getText().equals(text)) {
                        k++;
                    } else if (k > 0) {
                        newBeitraege[i - k] = beitraege[i];
                    } else {
                        newBeitraege[i] = beitraege[i];
                    }
                }
            }
            forum.setBeitraege(newBeitraege);
            forumRepository.save(forum);
            return Optional.of(forum);
        }
        return Optional.empty();
    }
}
