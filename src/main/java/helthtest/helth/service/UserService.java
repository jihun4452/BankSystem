package helthtest.helth.service;

import helthtest.helth.controller.repository.UserRepository;
import helthtest.helth.domain.UserEntity;
import helthtest.helth.dto.SignInRequest;
import helthtest.helth.dto.SignUpDto;
import helthtest.helth.exception.CustomException;
import helthtest.helth.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. => " + phone));

        return new org.springframework.security.core.userdetails.User(
                userEntity.getPhone(), userEntity.getPassword(), new ArrayList<>()
        );
    }

     // 회원 가입 메서드.
    @Transactional
    public UserEntity signUp(SignUpDto.SignUpRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_PHONE);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .phone(request.getPhone())
                .username(request.getUsername())
                .password(encodedPassword)
                .role(request.getRole())
                .build();

        return userRepository.save(userEntity);
    }

    public UserEntity authenticate(SignInRequest request) {
        UserEntity userEntity = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return userEntity;
    }
}
