package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.SiteComment;
import com.mustafa.restourant.repository.SiteCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SiteCommentServiceImpl implements SiteCommentService{

    @Autowired
    SiteCommentRepository siteCommentRepository;

    @Override
    public void saveComment(SiteComment siteComment) {
        siteCommentRepository.save(siteComment);
    }

    @Override
    public Page<SiteComment> allComments(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return siteCommentRepository.findAll(pageable);
    }

    @Override
    public void deleteCommentById(int id) {
        siteCommentRepository.deleteById(id);
    }
}
