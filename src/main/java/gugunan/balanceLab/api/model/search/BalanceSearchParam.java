package gugunan.balanceLab.api.model.search;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BalanceSearchParam extends PageParam {

    private String search;
    private Boolean showEnded;
    private List<String> categories;

    private LocalDate startDate;
    private LocalDate endDate;
}
