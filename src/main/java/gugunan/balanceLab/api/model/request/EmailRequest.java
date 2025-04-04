package gugunan.balanceLab.api.model.request;

import lombok.Data;

@Data
public class EmailRequest {

    private String email;
    private String loginId;
    private String verifyCode;

}
