package eu.dbortoluzzi.auth.service;

import eu.dbortoluzzi.auth.model.User;
import eu.dbortoluzzi.auth.utils.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDAuthenticationService implements IUserAuthenticationService {

    static Logger logger = LoggerFactory.getLogger(UUIDAuthenticationService.class);

    @Autowired
    private UserService userService;

    @Override
    public User login(String username, String password) {
        return userService.getByUsername(username)
                .filter(u -> u.getPassword().equals(PasswordEncoder.encryptPassword(password)))
                .map(u -> {
                    u.setToken(UUID.randomUUID().toString());
                    userService.save(u);
                    return u;
                })
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
    }

    @Override
    public User authenticateByToken(String token) {
        return new User();
//        return userService.getByToken(token)
//                .orElseThrow(() -> new BadCredentialsException("Token not found."));
    }

    @Override
    public void logout(String username) {
        userService.getByUsername(username)
                .ifPresent(u -> {
                    u.setToken(null);
                    userService.save(u);
                });
    }
}