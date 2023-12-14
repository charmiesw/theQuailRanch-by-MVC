package lk.ijse.theQuailRanch.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class RanchTm {
    private String id;
    private Date date;
    private String category;
    private String amountOfBirds;
}
