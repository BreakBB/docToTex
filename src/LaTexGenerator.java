import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class LaTexGenerator {

    private BufferedWriter writer = null;

    LaTexGenerator(String title){
        try {
            writer = new BufferedWriter(new FileWriter("GeneratedLaTex/" + title + ".tex"));

            writer.write(new String(Files.readAllBytes(Paths.get("LaTexConfigFiles/latexConfig.txt"))).replace("$%$title$%$", title));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeWriter(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
