package uk.gov.metoffice.hello.domain;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum StormReturnPeriod {

    ONE_THOUSAND_YEARS(1000),
    ONE_HUNDRED_YEARS(100),
    THIRTY_YEARS(30);

    int years;

    StormReturnPeriod(int years) {
        this.years = years;
    }

    public int getYears() {
        return years;
    }

    public boolean moreSevere(StormReturnPeriod stormReturnPeriod) {
        return getYears() > stormReturnPeriod.getYears();
    }
}
