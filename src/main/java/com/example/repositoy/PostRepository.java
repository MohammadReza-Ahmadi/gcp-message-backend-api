package com.example.repositoy;

import com.example.api.response.PostGetResponse;
import com.example.model.Comment;
import com.example.model.Post;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.model.Post.FIELDS.*;

@Slf4j
public class PostRepository extends EntityPersistence<Post> {

    //todo: test repository
    public static void main(String[] args) {
        PostRepository repo = new PostRepository();

//        repo.getAllPosts().stream().forEach(i -> System.out.println(i));

//        List<PostGetResponse> posts = repo.getAllPosts().stream()
//                .map(p -> PostGetResponse.builder()
//                        .id(p.getKey().getName())
//                        .author(p.getAuthor())
//                        .subject(p.getSubject())
//                        .createDate(p.getCreateDate())
//                        .changeDate(p.getChangeDate())
//                        .build())
//                .collect(Collectors.toList());
//
//        posts.forEach(i -> System.out.println(i));


        for (int i = 1; i <= 6; i++) {
            repo.insert(Post.builder()
                    .author("Backend-User")
                    .subject("Sub Dev-" + i)
                    .body("Body Dev-" + i)
                    .build());
        }

//        repo.getAllPosts()
//                .forEach(p -> {
//                    System.out.println("id=" + p.getKey().getNameOrId());
//                    System.out.println("author=" + p.getAuthor());
//                    System.out.println("subject=" + p.getSubject());
//                    System.out.println("body=" + p.getBody());
//                    System.out.println("createDate=" + p.getCreateDate());
//                    System.out.println("changeDate=" + p.getChangeDate());
//                });


//        Iterator<Entity> iter = repo.getAll();
//        while (iter.hasNext()) {
//            Entity entity = iter.next();
//            System.out.println("keyNameOrId=" + entity.getKey().getNameOrId());
//            System.out.println("author=" + entity.getString("author"));
//            System.out.println("subject=" + entity.getString("subject"));
//            System.out.println("body=" + entity.getString("body"));
//            System.out.println();
//        }

    }

    public Post insert(Post post) {
        log.info("inserting post: {}", post);
        String id = UUID.randomUUID().toString();
        Key key = getKeyFactory().newKey(id);
        FullEntity.Builder<Key> builder = FullEntity.newBuilder(key);

        if (post.getAuthor() != null) {
            builder.set(AUTHOR.getName(), post.getAuthor());
        }

        if (post.getSubject() != null) {
            builder.set(SUBJECT.getName(), post.getSubject());
        }

        if (post.getBody() != null) {
            builder.set(BODY.getName(), post.getBody());
        }

        post.setCreateDate(Timestamp.now());
        builder.set(CREATE_DATE.getName(), post.getCreateDate());

        return mapEntityToPost(super.save(builder.build()));
    }

    public boolean update(Post post, String username, String id) {
        log.info("updating post with id: {}", id);
        Entity dbEntity = this.get(id);
        Entity.Builder builder = Entity.newBuilder(getKeyFactory().newKey(id));

        builder.set(AUTHOR.getName(), username);

        if (post.getSubject() != null) {
            builder.set(SUBJECT.getName(), post.getSubject());
        }

        if (post.getBody() != null) {
            builder.set(BODY.getName(), post.getBody());
        }

        builder.set(CREATE_DATE.getName(), dbEntity.getTimestamp(CREATE_DATE.getName()));
        builder.set(CHANGE_DATE.getName(), Timestamp.now());

        return super.update(builder.build());
    }

    public List<Post> getAllPosts() {
        log.info("get all posts ...");
        List<Post> posts = new ArrayList<>();
        Iterator<Entity> iter = super.getAll(CREATE_DATE.getName());
        while (iter.hasNext()) {
            Entity entity = iter.next();
            Post post = mapEntityToPost(entity);
            post.setComments(getCommentsByPostId(post.getId()));
            posts.add(post);
        }
        log.info("fetched posts: {}", posts);
        return posts;
    }

    private List<Comment> getCommentsByPostId(String postId){
        log.info("get all comments for postId: {}", postId);
        List<Comment> comments = new ArrayList<>();
        Iterator<Entity> iter = this.getAllCommentsByPostId(postId);
        while (iter.hasNext()) {
            Entity entity = iter.next();
            comments.add(mapEntityToComment(entity));
        }
        log.info("fetched all comments of post with postId: {}", postId);
        return comments;

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

    private Post mapEntityToPost(Entity entity){
       return Post.builder()
//                .key(entity.getKey())
                .id(entity.getKey().getName())
                .author(entity.contains(AUTHOR.getName()) ? entity.getString(AUTHOR.getName()) : null)
                .subject(entity.contains(SUBJECT.getName()) ? entity.getString(SUBJECT.getName()) : null)
                .body(entity.contains(BODY.getName()) ? entity.getString(BODY.getName()) : null)
                .createDate(entity.contains(CREATE_DATE.getName()) ?
                        entity.getTimestamp(CREATE_DATE.getName()) : null)
                .changeDate(entity.contains(CHANGE_DATE.getName()) ?
                        entity.getTimestamp(CHANGE_DATE.getName()) : null)
                .build();
    }

}
