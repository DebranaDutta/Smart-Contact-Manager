package com.springboot.SmartContactManager.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.springboot.SmartContactManager.Model.Contact;

@Component
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// Pagination Method->Pageable will contain 2 things 1. Page number, 2.Page per contact
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
}
