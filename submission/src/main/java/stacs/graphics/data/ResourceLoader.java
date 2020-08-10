package stacs.graphics.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceLoader {

    private static final ResourceLoader instance = new ResourceLoader();

    private ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        return instance;
    }

    private BufferedReader getResourceReader(String resourceName) throws IOException {
        var resourceStream = ResourceLoader.class.getResourceAsStream("/" + resourceName);

        if (resourceStream == null) {
            throw new IOException("Unable to find resource: " + resourceName);
        }

        return new BufferedReader(new InputStreamReader(resourceStream));
    }

    public String readAllToString(String resourceName) throws Exception {
        var sb = new StringBuilder();

        try (var resourceReader = getResourceReader(resourceName)) {
            var line = "";
            while ((line = resourceReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public List<String> readToFlatList(String resourceName) throws IOException {
        var values = new ArrayList<String>();

        try (var reader = getResourceReader(resourceName)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var entries = line.split(",");
                values.addAll(Arrays.asList(entries));
            }
        }

        return values;
    }
}
