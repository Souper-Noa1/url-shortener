package com.url.shortener.domain.entities;

import com.url.shortener.domain.roles.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, updatable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Url> urls;

    public User(@NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be 3–50 characters") String username, @NotBlank(message = "Email is required") @Email(message = "Must be a valid email address") String email, @Nullable String encode) {
    }

    @PrePersist
    protected void createdAt() {
        this.createdAt = LocalDateTime.now();
    }


}
