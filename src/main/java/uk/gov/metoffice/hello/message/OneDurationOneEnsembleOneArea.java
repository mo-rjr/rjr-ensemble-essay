package uk.gov.metoffice.hello.message;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class OneDurationOneEnsembleOneArea {

    private String sourceZipName;

    private StormDuration duration;

    private Ensemble ensemble;

    private AdminArea adminArea;

    public String getSourceZipName() {
        return sourceZipName;
    }

    public void setSourceZipName(String sourceZipName) {
        this.sourceZipName = sourceZipName;
    }

    public StormDuration getDuration() {
        return duration;
    }

    public void setDuration(StormDuration duration) {
        this.duration = duration;
    }

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public AdminArea getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(AdminArea adminArea) {
        this.adminArea = adminArea;
    }
}
