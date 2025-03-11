package org.wildcodeschool.myblog.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDTO {

    @Email(message = "L'email est invalide")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min=8, message= "Le mot de passe doit contenir au minimum 8 caractères")
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message ="Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial." )
    private String password;

    //Getters et Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
