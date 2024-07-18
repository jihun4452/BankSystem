package helthtest.helth.dto;

import helthtest.helth.domain.UserEntity;
import lombok.Builder;
import lombok.Getter;

public class SignUpDto {

    @Getter
    @Builder
    public static class SignUpRequest {
        private final String username;
        private final String password;
        private final String phone;
        private final String role;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .phone(this.phone)
                    .username(this.username)
                    .password(this.password)
                    .role(this.role)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SignUpResponse {
        private Long id;
        private String username;
        private String phone;
        private String role;
    }
}
