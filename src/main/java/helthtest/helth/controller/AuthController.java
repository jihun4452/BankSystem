package helthtest.helth.controller;

import helthtest.helth.domain.UserEntity;
import helthtest.helth.dto.SignInRequest;
import helthtest.helth.dto.SignUpDto;
import helthtest.helth.security.TokenProvider;
import helthtest.helth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpDto.SignUpResponse> signUp(@RequestBody SignUpDto.SignUpRequest request) {
        UserEntity userEntity = this.userService.signUp(request);
        return ResponseEntity.ok(
                SignUpDto.SignUpResponse.builder()
                        .id(userEntity.getId())
                        .phone(userEntity.getPhone())
                        .role(userEntity.getRole())
                        .username(userEntity.getUsername())
                        .build());
    }

    /**
     * 로그인 api_23.08.01
     */
    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest request) {
        UserEntity user = this.userService.authenticate(request);
        String token = this.tokenProvider.generateToken(
                user.getId(),
                user.getPhone(),
                user.getRole());
        return ResponseEntity.ok(token);
    }
}
