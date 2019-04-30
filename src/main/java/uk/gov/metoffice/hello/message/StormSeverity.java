package uk.gov.metoffice.hello.message;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum StormSeverity {

    THIRTY_YEARS(30),
    ONE_HUNDRED_YEARS(100),
    ONE_THOUSAND_YEARS(1000);

    int years;

    StormSeverity(int years) {
        this.years = years;
    }

    public int getYears() {
        return years;
    }}
