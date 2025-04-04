package gugunan.balanceLab.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ID_MGMT", catalog = "balance_experiment")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class IdMgmt {

    @Id
    @Column(name = "tbl_nm")

    private String tblNm;

    @Column(name = "pre_nm")

    private String preNm;

    @Column(name = "srno")

    private Integer srno;

    @Column(name = "srno_len")

    private Integer srnoLen;

    @Column(name = "incr_val")

    private Integer incrVal;

    @Column(name = "updated_dtm")
    @UpdateTimestamp
    private LocalDateTime updatedDtm;
}
