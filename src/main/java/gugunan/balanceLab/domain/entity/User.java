package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import gugunan.balanceLab.domain.CustomGeneratedId;
import gugunan.balanceLab.support.ColumnCryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "USER", catalog = "balance_experiment")
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @Column(name = "user_id")
    @CustomGeneratedId(method = "USER")
    private String userId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "email")
    @Convert(converter = ColumnCryptoConverter.class)
    private String email;

    @Column(name = "total_point")
    private Integer totalPoint;

    @ColumnDefault("0")
    @Column(name = "del_yn")
    private Boolean delYn;

    @Column(name = "user_stus_cd")
    private String userStusCd;

    @Column(name = "join_dtm")
    @CreationTimestamp
    private LocalDateTime joinDtm;

    @Column(name = "updated_dtm")
    @UpdateTimestamp
    private LocalDateTime updatedDtm;

}
