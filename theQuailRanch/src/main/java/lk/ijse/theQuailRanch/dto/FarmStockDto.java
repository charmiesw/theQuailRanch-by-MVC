package lk.ijse.theQuailRanch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class FarmStockDto {
    private String id;
    private String supId;
    private String category;
    private String quantity;
}
