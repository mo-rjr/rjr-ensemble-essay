package uk.gov.metoffice.hello.unit;

import java.io.InputStream;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public interface InputStreamProvider {

    InputStream open(String fileReference);
}
