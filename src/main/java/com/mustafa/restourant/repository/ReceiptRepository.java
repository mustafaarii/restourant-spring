package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Receipt;
import com.mustafa.restourant.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt,Integer> {
    Receipt findTopByUserOrderByIdDesc(User user);
    Page<Receipt> findByUserOrderByDateDesc(User user, Pageable pageable);
}
