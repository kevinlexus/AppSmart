package com.ast.app.security;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {

    private String email;
    private String password;


}
