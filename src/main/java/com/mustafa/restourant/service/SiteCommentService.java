package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.SiteComment;
import org.springframework.data.domain.Page;


public interface SiteCommentService {
    void saveComment(SiteComment siteComment);
    Page<SiteComment> allComments(int page, int size);
    void deleteCommentById(int id);
}
