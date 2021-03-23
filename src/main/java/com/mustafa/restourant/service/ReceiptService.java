package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReceiptService {
    void saveReceipt(Receipt receipt);
    Receipt findLastByUser(User user);
    Page<Receipt> findByUser(User user, int page, int size);
}
