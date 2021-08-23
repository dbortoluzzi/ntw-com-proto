package eu.dbortoluzzi.auth.service;

import eu.dbortoluzzi.auth.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;


public interface IUserAuthenticationService {
    User login(String username, String password) throws BadCredentialsException;

    User authenticateByToken(String token) throws AuthenticationException;

    void logout(String username);
}