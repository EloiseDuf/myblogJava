package org.wildcodeschool.myblog.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wildcodeschool.myblog.exception.AlreadyUseException;
import org.wildcodeschool.myblog.model.User;
import org.wildcodeschool.myblog.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password, Set<String> roles){
        if(userRepository.existsByEmail(email)){
            throw new AlreadyUseException("Cet email est déjà utilisé");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
}
