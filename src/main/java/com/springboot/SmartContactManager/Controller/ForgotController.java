package com.springboot.SmartContactManager.Controller;

import java.util.Random;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.SmartContactManager.Dao.UserRepository;
import com.springboot.SmartContactManager.Model.User;
import com.springboot.SmartContactManager.Service.EmailService;

@Controller
public class ForgotController {
	Random random = new Random(1000);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Email id form Handler
	@RequestMapping(value = "/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	// Send OTP Handler
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		// Generating OTP of 4 digits
		int otp = random.nextInt(999999);

		// Sending OTP to email
		String subject = "OTP from Smart Contact Manager for Verification";
		String message = "<h3>OTP = " + otp + "</h3>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);
		if (flag) {
			session.setAttribute("myOtp", otp);
			session.setAttribute("myEmail", email);
			return "verify_otp";
		} else {
			session.setAttribute("message", "Check your email id!!");
			return "forgot_email_form";
		}
	}

	// Handler for verify-otp
	@PostMapping("/verify-otp")
	public String varifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("myEmail");
		if (myOtp == otp) {
			User user = this.userRepository.getUserByUserName(email);
			if (user == null) {
				session.setAttribute("message", "No user exists with this email!!");
				return "forgot_email_form";
			} else {
				return "password_change_form";
			}
		} else {
			session.setAttribute("message", "You have entered wrong OTP!!");
			return "verify_otp";
		}
	}

	// Handler for changing password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email = (String) session.getAttribute("myEmail");
		User user = this.userRepository.getUserByUserName(email);
		System.out.println(newpassword);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change=Password Changed Successfully";
	}
}
