package io.github.kypleefearz.bodybuilding.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.kypleefearz.bodybuilding.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('admin')")
@Tag(name = "Admin", description = "User management")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminUserController {

    private final UserProfileService userProfileService;

    public AdminUserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @Operation(summary = "List all users (admin)")
    public List<UserProfileResponse> list() {
        return userProfileService.listAll().stream().map(UserProfileResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user profile (admin)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
