package com.springboot.SmartContactManager.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.SmartContactManager.Dao.UserRepository;
import com.springboot.SmartContactManager.Model.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/index")
	public String Dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		// Get user using userName(Email)
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		return "Normal/UserDashboard";
	}
}
