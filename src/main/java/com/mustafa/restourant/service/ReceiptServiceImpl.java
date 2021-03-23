package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.User;
import com.mustafa.restourant.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReceiptServiceImpl implements ReceiptService{
    @Autowired
    ReceiptRepository receiptRepository;

    @Override
    public void saveReceipt(Receipt receipt) {
    receiptRepository.save(receipt);
    }

    @Override
    public Receipt findLastByUser(User user) {
        return receiptRepository.findTopByUserOrderByIdDesc(user);
    }

    @Override
    public Page<Receipt> findByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return receiptRepository.findByUserOrderByDateDesc(user,pageable);
    }
}
