package lk.ijse.theQuailRanch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class RanchDto {
    private String id;
    private Date date;
    private String category;
    private String amountOfBirds;
}
