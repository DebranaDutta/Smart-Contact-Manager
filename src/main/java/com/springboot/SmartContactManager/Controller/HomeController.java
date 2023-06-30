package com.springboot.SmartContactManager.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.SmartContactManager.Dao.UserRepository;
import com.springboot.SmartContactManager.Helper.Message;
import com.springboot.SmartContactManager.Model.User;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String Home(Model model) {
		model.addAttribute("title", "Home- Smart Contact Manager");
		return "Home";
	}

	@RequestMapping("/about")
	public String About(Model model) {
		model.addAttribute("title", "About- Smart Contact Manager");
		return "About";
	}

	@RequestMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user", new User());
		return "Signup";
	}

	// Handler for registering user
	@RequestMapping(path = "/do_register", method = RequestMethod.POST)
	public String RegisterUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			if (result.hasErrors()) {
				// System.out.println("Error: " + result.toString());
				model.addAttribute("user", user);
				return "Signup";
			}
			user.setId(new Random().nextInt(10000));
			user.setRole("ROLE_USER");
			user.setEnabled(false);
			user.setPassword(passwordEncoder.encode(user.getPassword())); 
			User res = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			return "Signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong" + e.getMessage(), "alert-danger"));
			return "Signup";
		}
	}
	
	//Handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login");
		return "login";
	}
}
