package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Post> all() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            long newId = idGenerator.getAndIncrement();
            post.setId(newId);
            storage.put(newId, post);
            return post;
        } else {
            long id = post.getId();
            if (storage.containsKey(id)) {
                storage.put(id, post);
                return post;
            } else {

                storage.put(id, post);
                if (id >= idGenerator.get()) {
                    idGenerator.set(id + 1);
                }
                return post;
            }
        }
    }

    public void removeById(long id) {
        Post removed = storage.remove(id);
        if (removed == null) {
            throw new ru.netology.exception.NotFoundException("Post with id " + id + " not found");
        }
    }
}