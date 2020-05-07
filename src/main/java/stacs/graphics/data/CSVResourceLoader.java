package stacs.graphics.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVResourceLoader {

    public List<String> readToFlatList(String resourceName) throws IOException {
        var indicesFileStream = CSVResourceLoader.class.getResourceAsStream("/" + resourceName);
        var reader = new BufferedReader(new InputStreamReader(indicesFileStream));

        var values = new ArrayList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                var entries = line.split(",");
                values.addAll(Arrays.asList(entries));
            }
        } finally {
            reader.close();
            indicesFileStream.close();
        }

        return values;
    }
}
