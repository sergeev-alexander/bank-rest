package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"token"})
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String issuer; // обязательное поле
    private Long id;
    private String email;
    private String role;

    public JwtResponse(String token,
                       String issuer,
                       Long id,
                       String email,
                       String role) {
        this.token = token;
        this.issuer = issuer;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}