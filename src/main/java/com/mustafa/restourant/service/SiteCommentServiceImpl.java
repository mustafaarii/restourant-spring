package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.SiteComment;
import com.mustafa.restourant.repository.SiteCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiteCommentServiceImpl implements SiteCommentService{

    @Autowired
    SiteCommentRepository siteCommentRepository;

    @Override
    public void saveComment(SiteComment siteComment) {
        siteCommentRepository.save(siteComment);
    }
}
