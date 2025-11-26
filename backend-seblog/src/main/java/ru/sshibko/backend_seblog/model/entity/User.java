package ru.sshibko.backend_seblog.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "String default ROLE_USER")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "String default ACTIVE")
    private UserStatus status;

    public org.springframework.security.core.userdetails.User toUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                this.email,
                this.passwordHash,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
