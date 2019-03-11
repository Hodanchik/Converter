import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class myEx {


    public void saveNewFiles( JSONObject jsonFile) {
        Set setKeyJson = jsonFile.keySet();

        setKeyJson.stream()
                .forEach(keyPath -> writeFile(keyPath.toString(), jsonFile));

    }

    private void writeFile (String nameFile, JSONObject jsonFile){

        try {
            FileWriter file = new FileWriter(nameFile);
            file.write(jsonFile.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}


