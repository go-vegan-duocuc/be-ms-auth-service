package cl.govegan.msauthservice.controller;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.service.login.LoginService;
import cl.govegan.msauthservice.service.register.RegisterService;
import cl.govegan.msauthservice.web.request.LoginRequest;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import cl.govegan.msauthservice.web.response.ApiResponse;
import cl.govegan.msauthservice.web.response.TokenLoad;
import cl.govegan.msauthservice.web.response.UserLoad;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final RegisterService registerService;
    private final LoginService loginService;

    @GetMapping("/status")
    public ResponseEntity<String> status () {
        return ResponseEntity.ok("Status ok");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserLoad>>
    register (@RequestBody RegisterRequest registerRequest) {

        User user = registerService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.<UserLoad>builder()
                .status(200)
                .message("User registered successfully")
                .data(UserLoad.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenLoad>> login (@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse.<TokenLoad>builder()
                .status(200)
                .message("User logged successfully")
                .data(loginService.login(loginRequest))
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenLoad> refreshToken (@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(loginService.refreshToken(refreshToken));
    }

    @GetMapping("/secure-endpoint")
    public ResponseEntity<String> getSecureEndpoint () {
        return ResponseEntity.ok("This is a secured endpoint.");
    }


}

