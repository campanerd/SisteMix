package org.siste.mix.user.web;

import jakarta.validation.Valid;
import org.siste.mix.infra.security.JwtService;
import org.siste.mix.user.dto.LoginRequest;
import org.siste.mix.user.dto.LoginResponse;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest data) {
        var authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authentication = authenticationManager.authenticate(authToken);

        var token = jwtService.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
