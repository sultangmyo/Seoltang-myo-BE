package com.seoltangmyo.sugarcat.domain.cat.entity;

import com.seoltangmyo.sugarcat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="cats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID catId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "diagnosed_date", nullable = false)
    private LocalDate diagnoseDate;

    @Column(name = "invite_code", length = 50)
    private String inviteCode;

    //2명 이상의 user 받아옴
    @OneToMany(mappedBy = "cat")
    private List<User> users;

    // diagnoseDate가 null이면 오늘 날짜로 자동 세팅 (DB DEFAULT CURRENT_DATE 대응) - 필요한지 검토부탁드립니다
    @PrePersist
    protected void onCreate(){
        if(this.diagnoseDate == null){
            this.diagnoseDate = LocalDate.now();
        }
    }



}
