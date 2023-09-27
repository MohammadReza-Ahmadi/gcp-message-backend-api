package com.example.repositoy;

import com.example.model.Comment;
import com.example.model.Post;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

import static com.example.model.Post.FIELDS.*;
import static com.example.model.Post.FIELDS.CHANGE_DATE;


@Slf4j
public class CommentRepository extends EntityPersistence<Comment> {

    private final PostRepository postRepository;

    public CommentRepository() {
        this.postRepository = new PostRepository();
    }

    public Comment insert(Comment comment) {
        log.info("Inserting comment: {}",comment);
        postRepository.get(comment.getPostId());

        String id = UUID.randomUUID().toString();
        Key key = getKeyFactory().newKey(id);
        FullEntity.Builder<Key> builder = FullEntity.newBuilder(key);

        if (comment.getAuthor() != null) {
            builder.set("author", comment.getAuthor());
        }

        if (comment.getBody() != null) {
            builder.set("body", comment.getBody());
        }

        builder.set("postId", comment.getPostId());
        builder.set("createDate", Timestamp.now());

        return mapEntityToComment(super.save(builder.build()));
    }

    private Comment mapEntityToComment(Entity entity){
        return Comment.builder()
                .id(entity.getKey().getName())
                .author(entity.contains(AUTHOR.getName()) ? entity.getString(AUTHOR.getName()) : null)
                .body(entity.contains(BODY.getName()) ? entity.getString(BODY.getName()) : null)
                .createDate(entity.contains(CREATE_DATE.getName()) ?
                        entity.getTimestamp(CREATE_DATE.getName()) : null)
                .build();
    }

}
