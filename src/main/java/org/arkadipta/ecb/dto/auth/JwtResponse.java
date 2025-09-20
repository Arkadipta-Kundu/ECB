package org.arkadipta.ecb.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
    private String name;
    private String role;

    public JwtResponse(String token, String refreshToken, String email, String name, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}