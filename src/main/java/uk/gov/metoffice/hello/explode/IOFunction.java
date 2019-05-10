package uk.gov.metoffice.hello.explode;

import java.io.IOException;

/**
 * Just a Function of R to T, but where the function can throw an IOException
 */
@FunctionalInterface
public interface IOFunction<T, R> {
    R use(T input) throws IOException;
}
