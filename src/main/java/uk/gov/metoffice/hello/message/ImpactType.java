package uk.gov.metoffice.hello.message;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum ImpactType {

    TRANSPORT("trans"),
    PROPERTY("prop"),
    KEY_INFRASTRUCTURE("ksinfra"),
    POPULATION("pop"),
    MAXIMUM("max");

    String fileNamePart;

    ImpactType(String fileNamePart) {
        this.fileNamePart = fileNamePart;
    }

    public String getFileNamePart() {
        return fileNamePart;
    }



}
