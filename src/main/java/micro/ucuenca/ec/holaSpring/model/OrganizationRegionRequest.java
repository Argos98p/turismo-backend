package micro.ucuenca.ec.holaSpring.model;

public class OrganizationRegionRequest {
    private String OrganizationId;
    private String RegionId;

    public String getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(String organizationId) {
        OrganizationId = organizationId;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String regionId) {
        RegionId = regionId;
    }
}
