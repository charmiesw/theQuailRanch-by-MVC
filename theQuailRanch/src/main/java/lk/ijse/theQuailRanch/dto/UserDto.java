package lk.ijse.theQuailRanch.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserDto {
    private String id;
    private String username;
    private String password;
    private String tel;
}
