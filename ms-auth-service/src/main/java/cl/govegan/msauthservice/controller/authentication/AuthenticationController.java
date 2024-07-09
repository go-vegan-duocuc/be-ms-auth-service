package cl.govegan.msauthservice.controller.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.service.login.LoginService;
import cl.govegan.msauthservice.service.recoverpassword.RecoveryPasswordService;
import cl.govegan.msauthservice.service.register.RegisterService;
import cl.govegan.msauthservice.web.request.LoginRequest;
import cl.govegan.msauthservice.web.request.RefreshTokenRequest;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import cl.govegan.msauthservice.web.request.ResetPasswordRequest;
import cl.govegan.msauthservice.web.response.ApiResponse;
import cl.govegan.msauthservice.web.response.TokenPayload;
import cl.govegan.msauthservice.web.response.UserPayload;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final RecoveryPasswordService recoveryPasswordService;

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Status ok");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserPayload>> register(@RequestBody RegisterRequest registerRequest) {

        try {
            User user = registerService.register(registerRequest);
            return ResponseEntity.ok(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("User registered successfully")
                    .data(UserPayload.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPayload>> login(@RequestBody LoginRequest loginRequest) {
        try {
            TokenPayload tokenPayload = loginService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("User logged successfully")
                    .data(tokenPayload)
                    .build());
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPayload>> refreshToken(@RequestBody RefreshTokenRequest body) {
        String refreshToken = body.getRefreshToken();

        try {
            TokenPayload tokenPayload = loginService.refresh(refreshToken);
            return ResponseEntity.ok(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("Token refreshed successfully")
                    .data(tokenPayload)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/recover-password")
    public ResponseEntity<ApiResponse<RecoveryPasswordCode>> recoverPassword(
            @RequestBody ResetPasswordRequest body) {

        try {
            RecoveryPasswordCode recoveryPasswordCode = recoveryPasswordService
                    .sendRecoveryCodeByEmail(body.getEmail());

            return ResponseEntity.ok(ApiResponse.<RecoveryPasswordCode>builder()
                    .status(HttpStatus.OK.value())
                    .message("Recovery code sent successfully")
                    .data(recoveryPasswordCode)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<RecoveryPasswordCode>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }

    }
}
