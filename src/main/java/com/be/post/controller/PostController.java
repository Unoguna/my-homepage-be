package com.be.post.controller;

import com.be.global.auth.SessionAuth;
import com.be.global.response.CommonResponse;
import com.be.post.dto.PostDtos.*;
import com.be.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "PostController", description = "게시판 관련 API")
public class PostController {

    private final PostService postService;
    private final SessionAuth sessionAuth;

    @GetMapping
    @Operation(summary = "게시판 글 다건 조회", description = "게시판의 글을 페이지 단위로 다건 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시판 글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 요청")
    })
    public CommonResponse<Page<PostListItemRes>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return CommonResponse.success(postService.list(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시판 글 단건 조회", description = "게시판의 글을  단건 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시판 글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 게시글 ID"),
            @ApiResponse(responseCode = "404", description = "게시판 글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<PostDetailRes> detail(@PathVariable Long id) {
        return CommonResponse.success(postService.detail(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "게시판 글 생성", description = "게시판의 글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시판 글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청값 오류(제목/내용 검증 실패)"),
            @ApiResponse(responseCode = "401", description = "인증 필요(로그인 필요)"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<PostCreateRes> create(@RequestBody CreateReq req, HttpServletRequest request) {
        Long userId = sessionAuth.requireUserId(request);
        Long id = postService.create(userId, req);
        return CommonResponse.success(new PostCreateRes(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "게시판 글 수정", description = "게시판의 글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시판 글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<Void> update(@PathVariable Long id, @RequestBody UpdateReq req, HttpServletRequest request) {
        Long userId = sessionAuth.requireUserId(request);
        postService.update(userId, id, req);
        return CommonResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "게시판 글 삭제", description = "게시판의 글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시판 글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = sessionAuth.requireUserId(request);
        postService.delete(userId, id);
        return CommonResponse.success(null);
    }
}