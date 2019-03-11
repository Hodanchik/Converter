import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static final String ZIP_ARCHIVE = "C:\\Users\\Tank\\IdeaProjects\\html2json\\src\\main\\resources\\original.zip";
    private static final int BUFFER_SIZE = 1024;


    public static void main(String[] args) throws IOException {
        Main app = new Main();
        app.unZip(ZIP_ARCHIVE);
        Map<String, String> mapFiles = app.readFiles(ZIP_ARCHIVE);
        //System.out.println(mapFiles);
        Map<String, String> mapJsoup = app.parseToJsoup(mapFiles);
        // System.out.println(mapJsoup);
        JSONObject jsonFile = app.splitToSentences(mapJsoup);
        System.out.println(jsonFile);
       // app.saveNewFiles(jsonFile);


    }

// 5. Сохранить все файлы в какуюто директорию
// 6. Заархивировать их всех в один zip архив.
// 7. Почистить ненужные файлы.

    // 1. Распаковать файл в какую-то директорию
    public void unZip(String zipFileName) {
        byte[] buffer = new byte[BUFFER_SIZE];
        final String dstDirectory = zipFileName.substring(0, zipFileName.lastIndexOf("."));
        final File dstDir = new File(dstDirectory);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }

        try {
            // Получаем содержимое ZIP архива
            final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry ze = zis.getNextEntry();
            String nextFileName;
            while (ze != null) {
                nextFileName = ze.getName();
                File nextFile = new File(dstDirectory + File.separator + nextFileName);
                System.out.println("Распаковываем: " + nextFile.getAbsolutePath());

                if (ze.isDirectory()) {
                    nextFile.mkdir();
                } else {
                    new File(nextFile.getParent()).mkdirs();
                    // Записываем содержимое файла
                    try (FileOutputStream fos = new FileOutputStream(nextFile)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    // 2. Прочитать все файлы из этой директории и запихнуть их в какуюнить коллекцию
    public Map<String, String> readFiles(String zipFileName) {
        Map<String, String> map = new HashMap<>();
        Path path1 = Paths.get(zipFileName.substring(0, zipFileName.lastIndexOf(".")));
        try (Stream<Path> paths = Files.walk(path1)) {
            paths
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.toString().endsWith(".html"))
                    .forEach(path -> convertToMap(map, path.toString()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // 3. Пробежаться по коллекции и из html формата переделать всё в text формат(убрать все теги)
    public Map<String, String> parseToJsoup(Map<String, String> map) {

        map.entrySet()
                .stream()
                .forEach(stringStringEntry -> map.put(stringStringEntry.getKey(), Jsoup.parse(stringStringEntry.getValue()).text()));

        return map;
    }

    // 4. Пробежаться ещё раз по коллекции и разбить текст на предложения в формате json
    public JSONObject splitToSentences(Map<String, String> map) throws IOException {

JSONObject jsonFile = new JSONObject();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] sentences = splitSentences(entry.getValue());

           jsonFile.put(entry.getKey(), Arrays.asList(sentences));
        }
        return jsonFile;
    }

    // 5. Сохранить все файлы в какуюто директорию





    private String[] splitSentences(String text) throws IOException {
        SentenceModel model;
        try (InputStream is = getClass().getResourceAsStream("model/nl-sent-custom1.bin")) {
            model = new SentenceModel(is);

            SentenceDetectorME sDetector = new SentenceDetectorME(model);
            String sentences[] = sDetector.sentDetect(text);
            return sentences;
        }
    }

    private void convertToMap(Map<String, String> map, String path) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            map.put(path, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}