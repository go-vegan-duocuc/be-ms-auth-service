package cl.govegan.msauthservice.controller.userdetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.service.userservice.UserService;
import cl.govegan.msauthservice.web.request.NewEmailRequest;
import cl.govegan.msauthservice.web.request.NewPasswordRequest;
import cl.govegan.msauthservice.web.response.ApiResponse;
import cl.govegan.msauthservice.web.response.UserPayload;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-details")
@RequiredArgsConstructor
public class UserDetailsController {

   private final UserService userService;

   @GetMapping("/status")
   public String status() {
      return "Status ok";
   }

   @GetMapping()
   public ResponseEntity<ApiResponse<UserPayload>> userDetails(Authentication authentication) {
      try {
         User user = userService.getUserDetails(authentication);

         UserPayload userPayload = UserPayload.builder()
               .username(user.getUsername())
               .email(user.getEmail())
               .password(user.getPassword())
               .build();
         return ResponseEntity.ok(ApiResponse.<UserPayload>builder()
               .status(HttpStatus.OK.value())
               .data(userPayload)
               .build());
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(ApiResponse.<UserPayload>builder()
               .status(HttpStatus.BAD_REQUEST.value())
               .message("Failed to get user details - Error : " + e.getMessage())
               .data(null)
               .build());
      }
   }

   @PatchMapping("/update-password")
   public ResponseEntity<ApiResponse<String>> updatePassword(
         @RequestBody NewPasswordRequest body,
         Authentication authentication) {

      try {
         userService.updatePassword(body, authentication);

         return ResponseEntity.ok(ApiResponse.<String>builder()
               .status(HttpStatus.OK.value())
               .message("Password updated successfully")
               .data("Password updated successfully")
               .build());
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
               .status(HttpStatus.BAD_REQUEST.value())
               .message("Failed to update password - Error : " + e.getMessage())
               .data(null)
               .build());
      }
   }

   @PatchMapping("/update-email")
   public ResponseEntity<ApiResponse<String>> updateEmail(
         @RequestBody NewEmailRequest body,
         Authentication authentication) {

      try {

         String newEmail = userService.updateEmail(body, authentication);

         return ResponseEntity.ok(ApiResponse.<String>builder()
               .status(HttpStatus.OK.value())
               .message("Email updated successfully")
               .data(newEmail)
               .build());
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
               .status(HttpStatus.BAD_REQUEST.value())
               .message("Failed to update email - Error : " + e.getMessage())
               .data(null)
               .build());
      }
   }

   @DeleteMapping("/delete-account")
   public ResponseEntity<ApiResponse<String>> deleteAccount(Authentication authentication) {

      try {
         userService.deleteAccount(authentication);

         return ResponseEntity.ok(ApiResponse.<String>builder()
               .status(HttpStatus.OK.value())
               .message("Account deleted successfully")
               .data(null)
               .build());
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
               .status(HttpStatus.BAD_REQUEST.value())
               .message("Failed to delete account - Error : " + e.getMessage())
               .data(null)
               .build());
      }
   }
}
