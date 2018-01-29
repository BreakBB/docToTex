import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class LaTexGenerator {

    private BufferedWriter writer = null;

    LaTexGenerator(String title, String packageName){
        try {
            writer = new BufferedWriter(new FileWriter("GeneratedLaTex/" + title + ".tex"));

            writer.write(new String(Files.readAllBytes(Paths.get("LaTexConfigFiles/latexConfig.txt"))).replace("$%$title$%$", validateLaTexCode(title)).replace("$%$package$%$", packageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSectionName(int level){
        return level == 0? "chapter" : level == 1? "section" : "subsection";
    }

    void closeWriter(){
        try {
            writer.write("\n\n\\end{document}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeClassOrInterface(int level, String label, String annotation, String accessmod, String modifier, String type, String name, String polymorphy, Documentation currentDoc) {
        if(level > 2){
            return;
        }

        try {
            String latexFile = new String(Files.readAllBytes(Paths.get("LaTexConfigFiles/latexClassOrInterfaceConfig.txt")));

            latexFile = latexFile.replace("(*/section)", getSectionName(level));
            latexFile = latexFile.replace("(*/sectionName)",  concatWithWhitespaces(type, name));
            latexFile = latexFile.replace("(*/label)", label);
            latexFile = latexFile.replace("(*/annotations)", validateLaTexCode(annotation));
            latexFile = latexFile.replace("(*/classDescription)", concatWithWhitespaces(accessmod, modifier, type, name, polymorphy));

            if(currentDoc == null){
                currentDoc = new Documentation();
            }

            String desc = currentDoc.getDescription() == null ? "" : validateLaTexCode(currentDoc.getDescription());
            latexFile = latexFile.replace("(*/javaDoc)", desc);
            latexFile = latexFile.replace("(*/tags)", buildTagSection(currentDoc.getTags()));

            writer.write(latexFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void writeMethod(int level, String label, String annotation, String accessmod, String modifier, String type, String name, HashMap<String, String> params, Documentation currentDoc) {
        if(level > 2){
            return;
        }

        try {
            String latexFile = new String(Files.readAllBytes(Paths.get("LaTexConfigFiles/latexMethodConfig.txt")));

            latexFile = latexFile.replace("(*/section)", getSectionName(level));
            latexFile = latexFile.replace("(*/sectionName)", "Methode " + name);
            latexFile = latexFile.replace("(*/label)", label);
            latexFile = latexFile.replace("(*/annotations)", validateLaTexCode(annotation));

            if(currentDoc == null){
                currentDoc = new Documentation();
            }

            latexFile = latexFile.replace("(*/methodSignature)", concatWithWhitespaces(accessmod, modifier, validateLaTexCode(type), name) + listParams(params) + listThrows(currentDoc.getThrows()));

            String desc = currentDoc.getDescription() == null ? "" : validateLaTexCode(currentDoc.getDescription());
            latexFile = latexFile.replace("(*/javaDoc)", desc);
            latexFile = latexFile.replace("(*/tags)", buildTagSection(currentDoc.getTags()) + buildTagSection(currentDoc.getParams()) + buildTagSection(currentDoc.getThrows()));

            writer.write(latexFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private String concatWithWhitespaces(String... items){
        boolean isFirst = true;
        StringBuilder text = new StringBuilder();

        for(String s : items){
            if(isFirst){
                text.append(s);
                isFirst = false;
            }
            else{
                text.append(s.isEmpty() ? "" : " " + s);
            }
        }
        return text.toString();
    }

    private String buildTagSection(ArrayList<Tag> tagList){
        if(tagList == null){
            return "";
        }

        StringBuilder text = new StringBuilder();
        for(Tag tag : tagList){
            if(tag.getRef() == null){
                text.append("\\textbf{").append(tag.getTagType()).append(tag.getTagName() == null ? "" : " " + tag.getTagName()).append(":}\n\n\\quad\\quad ").append(validateLaTexCode(tag.getText())).append("\n\n");
            }
            else{
                text.append("\\textbf{").append(tag.getTagType()).append(":}\n\n\\quad\\quad ").append("\\hyperref[").append(tag.getRef()).append("]{").append(validateLaTexCode(tag.getText())).append("}\n\n");
            }
        }

        return text + "\n";
    }

    private String validateLaTexCode(String tex){
        tex = tex.replace("\\", "\\textbackslash");
        tex = tex.replace("{", "\\{");
        tex = tex.replace("}", "\\}");
        tex = tex.replace("$", "\\$");
        tex = tex.replace("&", "\\&");
        tex = tex.replace("#", "\\#");
        tex = tex.replace("°", "\\textdegree");
        tex = tex.replace("^", "\\^{}");
        tex = tex.replace("_", "\\_");
        tex = tex.replace("~", "\\~{}");
        tex = tex.replace("%", "\\%");
        tex = tex.replace("<", "\\textless ");
        tex = tex.replace(">", "\\textgreater ");

        return tex;
    }

    private String listParams(HashMap<String, String> params){
        StringBuilder text = new StringBuilder("(");
        boolean isFirst = true;

        for(Map.Entry<String, String> entry : params.entrySet()){
            if(isFirst){
                text.append(validateLaTexCode(entry.getValue())).append(" ").append(entry.getKey());
                isFirst = false;
            }
            else{
                text.append(", ").append(validateLaTexCode(entry.getValue())).append(" ").append(entry.getKey());
            }
        }

        return text.toString() + ")";
    }

    private String listThrows(ArrayList<Tag> allThrows){
        StringBuilder text = new StringBuilder();
        boolean isFirst = true;

        for(Tag t : allThrows){
            if(isFirst){
                text.append(" throws ").append(t.getTagName());
                isFirst = false;
            }
            else {
                text.append(", ").append(t.getTagName());
            }
        }

        return text.toString();
    }
}
