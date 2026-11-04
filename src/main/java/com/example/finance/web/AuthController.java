package com.example.finance.web;

import com.example.finance.common.R;
import com.example.finance.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody Map<String, String> req) {
        // ① 认证：密码错了这里会抛 BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.get("username"), req.get("password")));

        // ② 认证通过，签发令牌
        UserDetails user = userDetailsService.loadUserByUsername(req.get("username"));
        String token = jwtService.generateToken(user);

        return R.ok(Map.of("token", token, "username", user.getUsername()));
    }
}