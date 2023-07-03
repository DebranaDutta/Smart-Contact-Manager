package com.springboot.SmartContactManager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.SmartContactManager.Dao.ContactRepository;
import com.springboot.SmartContactManager.Dao.UserRepository;
import com.springboot.SmartContactManager.Helper.Message;
import com.springboot.SmartContactManager.Model.Contact;
import com.springboot.SmartContactManager.Model.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	// Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		// Get user using userName(Email)
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	// Dashboar Home
	@RequestMapping("/index")
	public String Dashboard(Model model, Principal principal) {
		/*
		 * String userName = principal.getName(); // Get user using userName(Email) User
		 * user = userRepository.getUserByUserName(userName); model.addAttribute("user",
		 * user);
		 */
		model.addAttribute("title", "User Dashboard");
		return "Normal/UserDashboard";
	}

	// Open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "Normal/add_contact_form";
	}

	// Handler for process-contact
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result, Principal principal,
			Model model, @RequestParam("profileImage") MultipartFile file, HttpSession session) {
		try {
			if (result.hasErrors()) {
				return "Normal/add_contact_form";
			} else {
				if (file.isEmpty()) {
					// If file is empty try below message
					System.out.println("File is empty");
				} else {
					// upload the file to folder and update the name to contact
					contact.setImage(file.getOriginalFilename());
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				String name = principal.getName();
				User user = this.userRepository.getUserByUserName(name);
				contact.setCid(new Random().nextInt(10000));
				contact.setUser(user);
				user.getContacts().add(contact);
				this.userRepository.save(user);

				// Sent Success Message
				session.setAttribute("message", new Message("Your contact is added!!", "success"));
				return "Normal/add_contact_form";
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!! Try aagain...", "danger"));
			return "Normal/add_contact_form";
		}
	}

	// Show contact Handler
	@GetMapping("/show-contacts")
	public String showContacts(Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
		model.addAttribute("contacts", contacts);
		return "Normal/show_contacts";
	}
}
