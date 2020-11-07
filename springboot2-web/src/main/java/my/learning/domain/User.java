package my.learning.domain;

import lombok.*;
import my.learning.domain.enums.SocialType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;
    private String name;
    private String password;
    private String email;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String principal;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Builder
    public User(String name, String password, String email, LocalDateTime createdDate, LocalDateTime updatedDate,
                String principal, SocialType socialType) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.principal = principal;
        this.socialType = socialType;
    }
}
