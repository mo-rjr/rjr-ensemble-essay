package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.domain.AdminArea;
import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.StormDuration;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class OneDurationOneEnsembleOneArea {

    private String sourceZipName;

    private StormDuration stormDuration;

    private Ensemble ensemble;

    private AdminArea adminArea;

    public OneDurationOneEnsembleOneArea() {
    }

    public OneDurationOneEnsembleOneArea(String sourceZipName,
                                         StormDuration stormDuration,
                                         Ensemble ensemble,
                                         AdminArea adminArea) {
        this.sourceZipName = sourceZipName;
        this.stormDuration = stormDuration;
        this.ensemble = ensemble;
        this.adminArea = adminArea;
    }

    public String getSourceZipName() {
        return sourceZipName;
    }

    public void setSourceZipName(String sourceZipName) {
        this.sourceZipName = sourceZipName;
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

    public void setStormDuration(StormDuration stormDuration) {
        this.stormDuration = stormDuration;
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
