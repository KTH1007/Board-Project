package com.example.board.user.application;

import com.example.board.comment.domain.repository.CommentRepository;
import com.example.board.global.security.config.EncoderConfig;
import com.example.board.post.domain.repository.PostRepository;
import com.example.board.global.auth.api.dto.request.SignUpRequest;
import com.example.board.user.api.dto.request.UpdateUserRequest;
import com.example.board.global.auth.api.dto.response.SignUpResponse;
import com.example.board.user.api.dto.response.UpdateUserResponse;
import com.example.board.user.domain.Role;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import com.example.board.user.exception.DuplicatedUserException;
import com.example.board.user.exception.InvalidUserException;
import com.example.board.user.exception.NotFoundUserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EncoderConfig encoderConfig;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        checkEmailDuplication(signUpRequest.email());
        checkNicknameDuplication(signUpRequest.nickname());

        User user = User.builder()
                .email(signUpRequest.email())
                .password(encoderConfig.encoder().encode(signUpRequest.password()))
                .nickname(signUpRequest.nickname())
                .age(signUpRequest.age())
                .roles(List.of(Role.USER))
                .build();

        userRepository.save(user);

        return SignUpResponse.toDto(user);
    }

    @Transactional
    public UpdateUserResponse updateUser(Long id, @Valid UpdateUserRequest updateUserRequest) {
        User user = getUser(id);

        // 닉네임 업데이트
        String nickname = updateUserRequest.nickname();
        if (nickname != null && !nickname.isBlank()) {
            checkNicknameDuplication(nickname); // 중복 체크
            user.updateNickname(nickname);
        }

        // 비밀번호 업데이트 (oldPassword와 newPassword가 모두 입력된 경우에만)
        if (updateUserRequest.oldPassword() != null && updateUserRequest.newPassword() != null) {
            if (!encoderConfig.encoder().matches(updateUserRequest.oldPassword(), user.getPassword())) {
                throw new InvalidUserException("기존 비밀번호와 일치하지 않습니다.");
            }
            user.updatePassword(encoderConfig.encoder().encode(updateUserRequest.newPassword()));
        }
        return new UpdateUserResponse(user.getId(), user.getNickname());
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);

        commentRepository.deleteByUser(user);
        postRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundUserException::new);
    }

    private void checkEmailDuplication(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new DuplicatedUserException("email", email);
        });
    }

    private void checkNicknameDuplication(String nickname) {
        userRepository.findByNickname(nickname).ifPresent(user -> {
            throw new DuplicatedUserException("nickname", nickname);
        });
    }
}
