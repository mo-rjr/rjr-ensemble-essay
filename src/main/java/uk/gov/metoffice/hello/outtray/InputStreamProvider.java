package uk.gov.metoffice.hello.outtray;

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
