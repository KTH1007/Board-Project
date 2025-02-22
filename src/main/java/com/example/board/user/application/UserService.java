package com.example.board.user.application;

import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 임시 회원가입 메서드
    @Transactional
    public User createTempUser() {
        User user = User.builder()
                .email("temp@example.com")
                .password("tempPassword")
                .nickname("tempUser")
                .age(20)
                .build();
        return userRepository.save(user);
    }
}
