package com.example.api;

import com.example.api.request.PostUpdateRequest;
import com.example.api.response.PostGetResponse;
import com.example.api.response.SampleResponse;
import com.example.api.response.UpdateResponse;
import com.example.model.Post;
import com.example.repositoy.PostRepository;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(
        name = "posts",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "posts.example.com",
                ownerName = "posts.example.com",
                packagePath = ""
        )
)

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostApi {

    PostRepository repository;


    public PostApi() {
        this.repository = new PostRepository();
    }

    @ApiMethod(name = "TestApi",
            path = "/posts/v1/test-api",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SampleResponse testApi() {
        log.info("call testApi ...");
        SampleResponse response = SampleResponse.builder()
                .message("Test API is called")
                .build();
        log.info(response.getMessage());
        return response;
    }

    @ApiMethod(name = "posts", httpMethod = ApiMethod.HttpMethod.GET)
    public Map<String, List<PostGetResponse>> posts(HttpServletRequest req) {
        getUsername(req);
        log.info("get all posts ...");
        Map<String, List<PostGetResponse>> map = new HashMap<>();
        map.put("posts",
                repository.getAllPosts().stream()
                        .map(p -> PostGetResponse.builder()
                                .id(p.getId())
                                .author(p.getAuthor())
                                .subject(p.getSubject())
                                .body(p.getBody())
                                .createDate(p.getCreateDate())
                                .changeDate(p.getChangeDate())
                                .comments(p.getComments())
                                .build())
                        .collect(Collectors.toList()));
        return map;
    }

    @ApiMethod(name = "create", httpMethod = ApiMethod.HttpMethod.POST)
    public Post create(PostUpdateRequest postReq, HttpServletRequest req) {
        log.info("create post: {}", postReq);
        String username = getUsername(req);
        return repository.insert(Post.builder()
                .author(username)
                .subject(postReq.getSubject())
                .body(postReq.getBody())
                .build());
    }

    @ApiMethod(name = "modify", httpMethod = ApiMethod.HttpMethod.PUT)
    public UpdateResponse modify(PostUpdateRequest postReq,
                                 @Named("key") String key,
                                 HttpServletRequest req) {
        log.info("updating post with id: {}", key);

        String username = getUsername(req);
        boolean isSuccess = repository.update(Post.builder()
                .author(username)
                .subject(postReq.getSubject())
                .body(postReq.getBody())
                .build(), username, key);

        if (isSuccess) {
            return UpdateResponse.builder()
                    .message("Post is updated successfully")
                    .build();
        } else {
            return UpdateResponse.builder()
                    .message("Post updated is failed!")
                    .build();
        }
    }

    private String getUsername(HttpServletRequest req) {
        UserService userService = UserServiceFactory.getUserService();
        String thisUrl = req.getRequestURI();
        String currentUser = getCurrentUser(req);
        if (currentUser == null) {
            throw new RuntimeException(
                    "Please sign in via " + userService.createLoginURL(thisUrl));
        }
        return currentUser;
    }

    private String getCurrentUser(HttpServletRequest req){
        return req.getUserPrincipal()!= null? req.getUserPrincipal().getName(): "Backend-user";
    }
}