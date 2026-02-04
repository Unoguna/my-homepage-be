package com.be.post.domain;

import com.be.global.entity.BaseEntity;
import com.be.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_created_at", columnList = "createdAt")
})
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    public static Post of(User author, String title, String content) {
        Post post = new Post();
        post.author = author;
        post.title = title;
        post.content = content;
        return post;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}