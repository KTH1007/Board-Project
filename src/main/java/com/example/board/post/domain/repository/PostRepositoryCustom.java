package com.example.board.post.domain.repository;

import com.example.board.post.api.dto.request.SearchPostRequest;
import com.example.board.post.api.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostResponse> searchPage(SearchPostRequest searchPostRequest, Pageable pageable);
}
