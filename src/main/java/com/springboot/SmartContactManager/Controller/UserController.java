package com.springboot.SmartContactManager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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
					contact.setImage("contact.png");
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
	// Perpage=5, Current page=0
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "Normal/show_contacts";
	}

	// Showing particular contact details
	@GetMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "Normal/contact_detail";
	}

	// Handler for delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, Model model, Principal principal, HttpSession session) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));
		}
		return "redirect:/user/show-contacts/0";
	}

	// Handler for update contact form
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") int cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Optional<Contact> optionalContact = this.contactRepository.findById(cid);
		Contact contact = optionalContact.get();
		model.addAttribute("contact", contact);
		return "Normal/update_form";
	}

	// Handler for updating contact
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		try {
			// OLD Contact Details
			Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
			if (!file.isEmpty()) {
				// Delete Old Photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();

				// Update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContact.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated!!", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/contact/" + contact.getCid();
	}

	// User profile Handler
	@GetMapping("/profile")
	public String userProfile(Model model) {
		model.addAttribute("title", "User Profile");
		return "Normal/profile";
	}

	// Open Settings Handler
	@GetMapping("/settings")
	public String OpenSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "Normal/settings";
	}

	// Handler for changing password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("old-password") String oldPassword,
			@RequestParam("new-password") String newPassword, Principal principal, HttpSession session) {
		User user = this.userRepository.getUserByUserName(principal.getName());
		String password = user.getPassword();
		if (this.bCryptPasswordEncoder.matches(oldPassword, password)) {
			// Change the password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Your password is updated!!", "success"));
		} else {
			// error
			session.setAttribute("message", new Message("Wrong old password, please check again!!", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
}
