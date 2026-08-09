package ro.mihai.careerhub.dto.response;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getWebsite() {
        return website;
    }
}