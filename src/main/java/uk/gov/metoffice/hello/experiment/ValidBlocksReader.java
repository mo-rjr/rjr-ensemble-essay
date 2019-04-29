package uk.gov.metoffice.hello.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ValidBlocksReader {

    private static final String GRID_2_GRID_BLOCKS = "C:\\Workarea\\rjr-ensemble-essay\\src\\main\\resources\\grid2GridSquares.txt";
    private static final String COMMA = ",";

    public List<Integer> readValidBlocks() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(GRID_2_GRID_BLOCKS))) {

            return bufferedReader.lines()
                    .map(this::trimOffEndComma)
                    .flatMap(str -> Arrays.stream(str.split(COMMA)))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO handle exception properly
        }


    }


    private String trimOffEndComma(String input) {
        if (input.endsWith(",")) {
            return input.substring(0, input.length() - 1);

        } else {
            return input;
        }

    }

}
