package com.example.board.user.api;

import com.example.board.user.api.dto.request.UpdateUserRequest;
import com.example.board.user.api.dto.response.UpdateUserResponse;
import com.example.board.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PatchMapping("/user/{id}")
    @Operation(summary = "회원 정보 수정", description = "사용자의 비밀번호나 닉네임을 수정한다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        UpdateUserResponse updateUserResponse = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok().body(updateUserResponse);
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "사용자 삭제 성공")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("사용자 삭제 완료");
    }
}
