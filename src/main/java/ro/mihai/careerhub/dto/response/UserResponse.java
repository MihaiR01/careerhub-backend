package ro.mihai.careerhub.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private LocalDateTime createdate;

    public UserResponse(
            Long id,
            String firstname,
            String lastname,
            String email,
            String phonenumber,
            LocalDateTime createdate) {

        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.createdate = createdate;
    }
}