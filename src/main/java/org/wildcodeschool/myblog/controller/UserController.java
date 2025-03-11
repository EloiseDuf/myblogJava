package org.wildcodeschool.myblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wildcodeschool.myblog.exception.ResourceNotFoundException;
import org.wildcodeschool.myblog.model.User;
import org.wildcodeschool.myblog.service.UserService;

@RestController
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id== authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return ResponseEntity.ok(user);
    }
}
