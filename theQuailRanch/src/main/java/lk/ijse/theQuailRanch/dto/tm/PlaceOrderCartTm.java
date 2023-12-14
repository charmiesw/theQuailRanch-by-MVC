package lk.ijse.theQuailRanch.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

public class PlaceOrderCartTm {
    private String sellStockId;
    private String category;
    private int quantity;
    private double unitPrice;
    private double total;
    private Button btn;
}
