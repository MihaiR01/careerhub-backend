package ro.mihai.careerhub.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateCompanyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String city;
    
    @NotBlank
    private String website;

    public CreateCompanyRequest() {
    }

    public CreateCompanyRequest(String name, String city, String website) {
        this.name = name;
        this.city = city;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    
}
