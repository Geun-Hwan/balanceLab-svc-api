package gugunan.balanceLab.api.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;

    private String loginId;
    private String email;
    private String nickName;
    private Integer totalPoint;

    private List<GrantedAuthority> authorities;

}
