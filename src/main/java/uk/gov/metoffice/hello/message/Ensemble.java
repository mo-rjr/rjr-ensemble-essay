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

    private Map<ZonedDateTime, String> runoffFileForTimestep = new HashMap<>();

    private String rainfallFile;

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

    public Map<ZonedDateTime, String> getRunoffFileForTimestep() {
        return runoffFileForTimestep;
    }

    public void setRunoffFileForTimestep(Map<ZonedDateTime, String> runoffFileForTimestep) {
        this.runoffFileForTimestep = runoffFileForTimestep;
    }

    public String getRainfallFile() {
        return rainfallFile;
    }

    public void setRainfallFile(String rainfallFile) {
        this.rainfallFile = rainfallFile;
    }
}
