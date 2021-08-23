package eu.dbortoluzzi.auth;

import eu.dbortoluzzi.auth.model.User;
import eu.dbortoluzzi.auth.service.UserRegistrationService;
import eu.dbortoluzzi.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthDataSeeder {

	Logger logger = LoggerFactory.getLogger(AuthDataSeeder.class);

	@Autowired
	UserRegistrationService userRegistrationService;

	@Autowired
	UserService userService;

	String fakeUserUsername;
	String fakeUserPassword;

	public AuthDataSeeder(@Value("${auth.fake.user.username}") String fakeUserUsername, @Value("${auth.fake.user.password}") String fakeUserPassword) {
		this.fakeUserPassword = fakeUserPassword;
		this.fakeUserUsername = fakeUserUsername;
	}

	public void seedIfEmpty(){
		try {
			Optional<User> userOptional = userService.getByUsername(fakeUserUsername);
			if (!userOptional.isPresent()) {
				userRegistrationService.register(fakeUserUsername, fakeUserPassword);
			}
            logger.info("finish");
		}catch (Exception e) {
			logger.error("error in seedIfEmpty", e);
		}
	}

}
