package com.springboot.SmartContactManager.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.springboot.SmartContactManager.Model.Contact;

@Component
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// Pagination Method
	@Query("from Contact as c where c.user.id=:userId")
	public List<Contact> findContactsByUser(@Param("userId") int userId);
}
