package com.example.board.global.oauth2;

import com.example.board.global.security.config.EncoderConfig;
import com.example.board.user.domain.Role;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final EncoderConfig encoderConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 네이버인 경우 응답 데이터가 "response" 키로 감싸져 있음
        if (provider.equals("naver")) {
            attributes = (Map<String, Object>) attributes.get("response");
        }

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .password(encoderConfig.encoder().encode("oauth2user")) // 기본 비밀번호 설정 (사용되지 않음)
                            .nickname(name)
                            .age(0) // 기본값
                            .roles(List.of(Role.USER))
                            .build();
                    return userRepository.save(newUser);
                });


        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRoles().toString())), // 권한 정보
                attributes, // 사용자 정보
                provider.equals("naver") ? "id" : "sub" // userNameAttributeName
        );
    }

}
