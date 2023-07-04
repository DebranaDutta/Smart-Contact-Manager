package com.springboot.SmartContactManager.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.SmartContactManager.Dao.ContactRepository;
import com.springboot.SmartContactManager.Dao.UserRepository;
import com.springboot.SmartContactManager.Model.Contact;
import com.springboot.SmartContactManager.Model.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> Search(Principal principal, @PathVariable("query") String query) {
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAllIgnoreCaseAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}
