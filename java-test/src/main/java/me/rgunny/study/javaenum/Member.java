package me.rgunny.study.javaenum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_email", nullable = false, unique = true)
    private String email;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_img")
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_social_type")
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private RoleType role;

    public Member updateInfo(String name, String img) {
        this.name = name;
        this.img = img;
        return this;
    }

}