package lk.ijse.theQuailRanch.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class FarmStockTm {
    private String id;
    private String supId;
    private String category;
    private String quantity;
}
