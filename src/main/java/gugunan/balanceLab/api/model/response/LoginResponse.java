
package gugunan.balanceLab.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String userId;
    private String email;
    private String loginId;

    private String nickName;

    private Integer totalPoint;

    private String accessToken;

}
