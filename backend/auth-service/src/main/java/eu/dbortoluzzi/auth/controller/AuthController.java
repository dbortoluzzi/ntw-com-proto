package eu.dbortoluzzi.auth.controller;

import eu.dbortoluzzi.auth.model.UserForm;
import eu.dbortoluzzi.auth.service.IUserAuthenticationService;
import eu.dbortoluzzi.auth.service.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Slf4j
public class AuthController {
	@Autowired
	private UserRegistrationService registrationService;
	@Autowired
	private IUserAuthenticationService authenticationService;

	@PostMapping("/api/auth/register")
	@CrossOrigin
	public Object register(
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		try {
			return registrationService
					.register(username, password);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/api/auth/login")
	@CrossOrigin
	public Object login(@RequestBody UserForm user) {
		try {
			return authenticationService
					.login(user.getUsername(), user.getPassword());
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(UNAUTHORIZED).body(e.getMessage());
		}
	}

	@GetMapping("/api/auth/auth")
	@CrossOrigin
	public Object auth(
			@RequestHeader("token") String token) {
		try {
			return authenticationService
					.authenticateByToken(token);
		} catch (Exception e) {
			log.error("error in auth token", e);
			return ResponseEntity.status(UNAUTHORIZED).body(e.getMessage());
		}
	}
}
