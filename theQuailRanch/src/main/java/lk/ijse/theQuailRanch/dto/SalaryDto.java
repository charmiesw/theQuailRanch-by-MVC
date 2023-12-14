package lk.ijse.theQuailRanch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SalaryDto {
    private String salId;
    private String empId;
    private String Amount;
    private Date paidDate;
}
