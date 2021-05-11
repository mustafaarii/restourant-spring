package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Tip;
import com.mustafa.restourant.repository.TipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipServiceImpl implements TipService{

    @Autowired
    TipRepository tipRepository;

    @Override
    public void saveTip(Tip tip) { tipRepository.save(tip); }

}
