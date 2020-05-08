package stacs.graphics.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public String readAllToString(String resourceName) throws Exception {
        try {
            var resource = ResourceLoader.class.getResource("/" + resourceName);
            return new String(Files.readAllBytes(Paths.get(resource.toURI())));
        } catch (URISyntaxException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<String> readToFlatList(String resourceName) throws IOException {
        var resourceStream = ResourceLoader.class.getResourceAsStream("/" + resourceName);
        var reader = new BufferedReader(new InputStreamReader(resourceStream));

        var values = new ArrayList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                var entries = line.split(",");
                values.addAll(Arrays.asList(entries));
            }
        } finally {
            reader.close();
            resourceStream.close();
        }

        return values;
    }
}
