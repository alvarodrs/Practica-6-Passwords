package info.jab.microservices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String newPassword2;
}
