package ro.mihai.careerhub.dto.response;

import lombok.Getter;

@Getter
public class CompanyResponse {

    private Long id;
    private String name;
    private String city;
    private String website;

    public CompanyResponse() {
    }

    public CompanyResponse(Long id, String name, String city, String website) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.website = website;
    }
}