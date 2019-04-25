package uk.gov.metoffice.hello.gatekeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.metoffice.hello.message.AdminArea;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AdminAreaReader {
    public static final String RESOURCES_FOLDER = "C:\\Workarea\\hello-bil\\src\\main\\resources\\";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public List<AdminArea> read(String fileName) {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            AdminAreaList adminAreaList = OBJECT_MAPPER.readValue(inputStream, AdminAreaList.class);
            return adminAreaList.getAdminAreas();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

class AdminAreaList {
    private List<AdminArea> adminAreas;

    public List<AdminArea> getAdminAreas() {
        return adminAreas;
    }

    public void setAdminAreas(List<AdminArea> adminAreas) {
        this.adminAreas = adminAreas;
    }
}
