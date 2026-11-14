package io.github.kypleefearz.bodybuilding.forum;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.common.ForbiddenException;
import io.github.kypleefearz.bodybuilding.common.NotFoundException;

@Service
public class ForumService {

    private final ForumThreadRepository threadRepository;

    public ForumService(ForumThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public List<ForumThread> listThreads() {
        return threadRepository.findAllByOrderByLastActivityAtDesc();
    }

    public ForumThread getThread(String threadId) {
        return threadRepository.findById(threadId).orElseThrow(() -> new NotFoundException("Thread", threadId));
    }

    public ForumThread createThread(CurrentUser user, String title, String content) {
        Post openingPost = new Post(user.id(), user.username(), content.trim());
        return threadRepository.save(new ForumThread(title.trim(), openingPost));
    }

    public void deleteThread(CurrentUser user, String threadId) {
        ForumThread thread = getThread(threadId);
        if (!user.canModerate(thread.getAuthorId())) {
            throw new ForbiddenException("Only the author or an admin can delete this thread");
        }
        threadRepository.delete(thread);
    }

    public Post addPost(CurrentUser user, String threadId, String content) {
        ForumThread thread = getThread(threadId);
        Post post = new Post(user.id(), user.username(), content.trim());
        thread.addPost(post);
        threadRepository.save(thread);
        return post;
    }

    /** Only the author may edit a post. Admins can delete but not rewrite other people's words. */
    public Post editPost(CurrentUser user, String threadId, String postId, String content) {
        ForumThread thread = getThread(threadId);
        Post post = thread.findPost(postId).orElseThrow(() -> new NotFoundException("Post", postId));
        if (!post.getAuthorId().equals(user.id())) {
            throw new ForbiddenException("Only the author can edit this post");
        }
        post.edit(content.trim());
        threadRepository.save(thread);
        return post;
    }

    public void deletePost(CurrentUser user, String threadId, String postId) {
        ForumThread thread = getThread(threadId);
        Post post = thread.findPost(postId).orElseThrow(() -> new NotFoundException("Post", postId));
        if (!user.canModerate(post.getAuthorId())) {
            throw new ForbiddenException("Only the author or an admin can delete this post");
        }
        thread.removePost(postId);
        threadRepository.save(thread);
    }
}
