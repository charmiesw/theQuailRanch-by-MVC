package lk.ijse.theQuailRanch.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SellStockTm {
    private String id;
    private String category;
    private int quantity;
    private double unitPrice;
}
