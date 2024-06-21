package cl.govegan.msauthservice.controller;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.service.register.RegisterService;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import cl.govegan.msauthservice.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>>
    register (@RequestBody RegisterRequest registerRequest) {


        return ResponseEntity.ok(ApiResponse.<User>builder()
                .status(200)
                .message("User registered successfully")
                .data(registerService.register(registerRequest)
                ).build());
    }

}

