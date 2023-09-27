package com.example.api;

import com.example.api.request.CommentRequest;
import com.example.model.Comment;
import com.example.repositoy.CommentRepository;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Api(
        name = "comments",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "comments.example.com",
                ownerName = "comments.example.com",
                packagePath = ""
        )
)

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentApi {

    CommentRepository repository;

    public CommentApi() {
        this.repository = new CommentRepository();
    }

    @ApiMethod(name = "create", httpMethod = ApiMethod.HttpMethod.POST)
    public Comment create(CommentRequest commReq, HttpServletRequest req) {
        String username = getUsername(req);
        return repository.insert(Comment.builder()
                .postId(commReq.getPostId())
                .author(username)
                .body(commReq.getBody())
                .postId(commReq.getPostId())
                .build());
    }


    private String getUsername(HttpServletRequest req) {
        return req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "Backend-User";
    }
}
