package gugunan.balanceLab.domain.idclass;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictParticipationId implements Serializable {
    private String predictId;
    private String userId;
}
