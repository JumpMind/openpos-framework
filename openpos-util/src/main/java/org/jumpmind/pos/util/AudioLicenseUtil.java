package org.jumpmind.pos.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class AudioLicenseUtil {
    public static final String AUDIO_LICENSE_PATH = "audio/licenses.csv";

    public static List<AudioLicense> getLicenses() throws IOException {
        Resource resource = ResourceUtils.getContentResource(AUDIO_LICENSE_PATH);
        CSVParser csvParser = null;
        ArrayList<AudioLicense> licenses = new ArrayList<>();

        try {
            InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream());
            csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(resourceReader);

            for (CSVRecord csvRecord : csvParser) {
                licenses.add(AudioLicense.builder()
                        .key(csvRecord.get("Key"))
                        .author(csvRecord.get("Author"))
                        .title(csvRecord.get("Title"))
                        .sourceUri(csvRecord.get("Source URI"))
                        .filename(csvRecord.get("Filename"))
                        .license(csvRecord.get("License"))
                        .licenseUri(csvRecord.get("License URI"))
                        .comments(csvRecord.get("Comments"))
                        .build()
                );
            }
        } finally {
            if (csvParser != null) {
                csvParser.close();
            }
        }

        return licenses;
    }
}
