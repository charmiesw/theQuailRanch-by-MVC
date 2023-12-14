package lk.ijse.theQuailRanch.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserTm {
    private String id;
    private String username;
    private String password;
    private String tel;
}
