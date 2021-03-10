package br.com.generatecsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CreateCSV implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(CreateCSV.class);

    @Autowired
    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {

        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[] { "nome","cpf","codigo","subproduto","dias","valor" });

        Long nameID = 0L;
        String cpf = String.valueOf(generateId());
        for(int i = 0; i < 60000; i++){

            if (getRandomBoolean()) {
                ++nameID;
                cpf = String.valueOf(generateId());
            }

            dataLines.add(new String[] { "Bruno Queiroz" + nameID,
                    cpf,
                    String.valueOf(generateId()),
                    "cartao" + String.valueOf(generateId()),
                    String.valueOf(generateId()),
                    String.valueOf(generateId()) });
        }

        File csvOutputFile =  new File(generateId()+".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            LOG.error("IOException " + e.getMessage());
        }

        //csvOutputFile.deleteOnExit();


    }


    public long generateId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextLong(10_000_000_000L, 100_000_000_000L);
    }

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
