package uk.gov.metoffice.hello.message;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum StormSeverity {

    ONE_THOUSAND_YEARS(1000),
    ONE_HUNDRED_YEARS(100),
    THIRTY_YEARS(30);

    int years;

    StormSeverity(int years) {
        this.years = years;
    }

    public int getYears() {
        return years;
    }

    public boolean moreSevere(StormSeverity stormSeverity) {
        return getYears() > stormSeverity.getYears();
    }
}
