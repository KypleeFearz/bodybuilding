package ch.zhaw.service;

import java.util.Arrays;
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

    public Optional<Forum> createBeitrag(String user, String text, String creator) {
        User userCreate = userRepository.findFirstByName(user);
        Forum forumAssign = forumRepository.findFirstByCreator(creator);
        if (userCreate != null && forumAssign != null && text != "") {
            Beitrag beitrag = new Beitrag();
            beitrag.setText(text);
            beitrag.setUser(user);
            if (forumAssign.getBeitraege().length != 0) {
                Beitrag[] beitraege = Arrays.copyOf(forumAssign.getBeitraege(), forumAssign.getBeitraege().length + 1);
                beitraege[forumAssign.getBeitraege().length - 1] = beitrag;
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

   /*  public Optional<Forum> editBeitrag() {

    }

    public Optional<Forum> deleteBeitrag() {

    }*/
}
