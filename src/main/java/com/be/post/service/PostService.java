package com.be.post.service;

import com.be.global.exception.ApplicationException;
import com.be.global.exception.ErrorCode;
import com.be.post.domain.Post;
import com.be.post.dto.PostDtos.CreateReq;
import com.be.post.dto.PostDtos.PostDetailRes;
import com.be.post.dto.PostDtos.PostListItemRes;
import com.be.post.dto.PostDtos.UpdateReq;
import com.be.post.repository.PostRepository;
import com.be.user.domain.User;
import com.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Long create(Long userId, CreateReq req) {
        validate(req.title(), req.content());

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        Post post = Post.of(author, req.title().trim(), req.content().trim());
        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public Page<PostListItemRes> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(p -> new PostListItemRes(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor().getUsername(),
                        p.getCreatedAt()
                ));
    }

    @Transactional
    public PostDetailRes detail(Long id) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return new PostDetailRes(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor().getUsername(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public void update(Long userId, Long postId, UpdateReq req) {
        validate(req.title(), req.content());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        User me = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        if (!isOwnerOrAdmin(me, post)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        post.update(req.title().trim(), req.content().trim());
    }

    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        User me = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        if (!isOwnerOrAdmin(me, post)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        postRepository.delete(post);
    }

    private boolean isOwnerOrAdmin(User me, Post post) {
        return post.getAuthor().getId().equals(me.getId()) || me.isAdmin();
    }

    private void validate(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
        }
        if (title.trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title too long");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content required");
        }
    }
}
