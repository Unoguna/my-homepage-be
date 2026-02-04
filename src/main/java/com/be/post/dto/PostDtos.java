package com.be.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PostDtos {

    public record CreateReq(
            @Schema(example = "게시글 제목입니다")
            String title,

            @Schema(example = "게시글 내용입니다")
            String content
    ) {}

    public record UpdateReq(
            @Schema(example = "수정된 게시글 제목입니다")
            String title,

            @Schema(example = "수정된 게시글 내용입니다")
            String content
    ) {}

    public record PostListItemRes(
            @Schema(example = "1")
            Long id,

            @Schema(example = "게시글 제목입니다")
            String title,

            @Schema(example = "kim")
            String authorName,

            @Schema(example = "2026-01-31T04:15:53.555Z")
            LocalDateTime createdAt
    ) {}

    public record PostDetailRes(
            @Schema(example = "1")
            Long id,

            @Schema(example = "게시글 제목입니다")
            String title,

            @Schema(example = "게시글 내용입니다")
            String content,

            @Schema(example = "kim")
            String authorName,

            @Schema(example = "2026-01-31T04:15:53.555Z")
            LocalDateTime createdAt,

            @Schema(example = "2026-01-31T04:20:10.123Z")
            LocalDateTime updatedAt
    ) {}

    public record PostCreateRes(
            @Schema(example = "1")
            Long id
    ) {}

}
