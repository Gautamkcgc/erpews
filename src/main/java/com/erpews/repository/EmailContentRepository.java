package com.erpews.repository;

import com.erpews.entity.EmailContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailContentRepository extends JpaRepository<EmailContent, Integer> {
    EmailContent findByTitle(String title);
}
