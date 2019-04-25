package uk.gov.metoffice.hello.message;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class Ensemble {

    private String sourceZipName;

    private int ensembleId;

    private Map<ZonedDateTime, String> runoffFilePerTimestep = new HashMap<>();

    private String rainfallFile;

    public Ensemble() {
    }

    public Ensemble(String sourceZipName, int ensembleId, Map<ZonedDateTime, String> runoffFilePerTimestep, String rainfallFile) {
        this.sourceZipName = sourceZipName;
        this.ensembleId = ensembleId;
        this.runoffFilePerTimestep = runoffFilePerTimestep;
        this.rainfallFile = rainfallFile;
    }

    public String getSourceZipName() {
        return sourceZipName;
    }

    public void setSourceZipName(String sourceZipName) {
        this.sourceZipName = sourceZipName;
    }

    public int getEnsembleId() {
        return ensembleId;
    }

    public void setEnsembleId(int ensembleId) {
        this.ensembleId = ensembleId;
    }

    public Map<ZonedDateTime, String> getRunoffFilePerTimestep() {
        return runoffFilePerTimestep;
    }

    public void setRunoffFilePerTimestep(Map<ZonedDateTime, String> runoffFilePerTimestep) {
        this.runoffFilePerTimestep = runoffFilePerTimestep;
    }

    public String getRainfallFile() {
        return rainfallFile;
    }

    public void setRainfallFile(String rainfallFile) {
        this.rainfallFile = rainfallFile;
    }
}
