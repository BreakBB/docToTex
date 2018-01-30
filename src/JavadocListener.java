import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JavadocListener extends JavadocParserBaseListener {

    private Documentation currentDoc;
    private String hierarchy = "";
    private static String docTitle;
    private static String packageName = "";
    private LaTexGenerator gen;
    private int level = 0;

    @Override
    public void exitJavaPackage(JavadocParser.JavaPackageContext ctx) {
        hierarchy = ctx.typeName().getText();
        packageName = "package: " + hierarchy;
    }

    @Override
    public void exitDocStart(JavadocParser.DocStartContext ctx) {
        gen.closeWriter();
    }

    @Override
    public void exitDocText(JavadocParser.DocTextContext ctx) {
        currentDoc.setDescription(addWhiteSpace(currentDoc.getDescription(), ctx.getText()));
    }

    @Override
    public void exitInlineTag(JavadocParser.InlineTagContext ctx) {
        String tagName = ctx.inlineTagName().getText();
        String tagText = ctx.inlineTagContent() == null ? "" : readMultipleRules(ctx.inlineTagContent().braceContent());

        if(ctx.inlineTagName().INLINE_LINK() != null || ctx.inlineTagName().INLINE_LINK_PLAIN() != null){
            String firstAttr = tagText.isEmpty() ? null : ctx.inlineTagContent().braceContent(0).getChild(0).getClass().equals(JavadocParser.BraceTextContext.class) ? ctx.inlineTagContent().braceContent(0).getChild(0).getText().replace("#", ":") : null;
            tagText = firstAttr == null ? tagText : tagText.substring(firstAttr.length());
            currentDoc.addInlineTags(new Tag(tagName, null, tagText, firstAttr));
        }

        currentDoc.addInlineTags(new Tag(tagName, null, tagText, null));

        String desc = currentDoc.getDescription();
        currentDoc.setDescription(desc == null ? "*/" : desc + " " + "*/");
    }

    private String addWhiteSpace(String old, String add) {
        if(old != null && !old.matches("^.*[a-zA-Z0-9][\\S]$") && !old.matches("^.*[a-zA-Z0-9]$") && !old.matches(".*[.,;!?]$")){
            return old.isEmpty() ? add : old + add;
        }

        if(add.matches("[.,;!?]")){
            return old == null || old.isEmpty() ? add : old + add;
        }

        return old == null || old.isEmpty() ? add : old + " " + add;
    }

    @Override
    public void enterJavaDocStart(JavadocParser.JavaDocStartContext ctx) {
        currentDoc = new Documentation();
    }

    @Override
    public void exitSee(JavadocParser.SeeContext ctx) {
        String typeName = ctx.typeName() == null ? ctx.SEE_REF().getText() : ctx.typeName().getText();
        String name = ctx.tagEnd().isEmpty() ? typeName : readMultipleRules(ctx.tagEnd());

        currentDoc.addTags(new Tag(ctx.SEE().getText(), null, name, typeName.replace("#", ":")));
    }

    @Override
    public void exitParams(JavadocParser.ParamsContext ctx) {
        currentDoc.addParam(new Tag(ctx.PARAM().getText(), ctx.NAME().getText(), readMultipleRules(ctx.methodOrConstructorTagEnd()), null));
    }

    @Override
    public void exitThrows(JavadocParser.ThrowsContext ctx) {

        currentDoc.addThrows(new Tag(ctx.EXCEPTION().getText(), ctx.typeName().getText(), readMultipleRules(ctx.methodOrConstructorTagEnd()), null));
    }

    @Override
    public void exitSerialData(JavadocParser.SerialDataContext ctx) {
        currentDoc.addTags(new Tag(ctx.SERIAL_DATA().getText(), null, readMultipleRules(ctx.methodOrConstructorTagEnd()), null));
    }

    @Override
    public void exitSince(JavadocParser.SinceContext ctx) {
        currentDoc.addTags(new Tag(ctx.SINCE().getText(), null, readMultipleRules(ctx.tagEnd()), null));
    }

    @Override
    public void exitDeprecated(JavadocParser.DeprecatedContext ctx) {
        currentDoc.addTags(new Tag(ctx.DEPRECATED().getText(), null, readMultipleRules(ctx.tagEnd()), null));
    }

    @Override
    public void exitClassTag(JavadocParser.ClassTagContext ctx) {
        if(ctx.tag() != null){
            return;
        }
        currentDoc.addTags(new Tag(ctx.getChild(0).getText(), null, readMultipleRules(ctx.classTagEnd()), null));
    }

    @Override
    public void exitFieldTag(JavadocParser.FieldTagContext ctx) {
        if(ctx.tag() != null){
            return;
        }
        currentDoc.addTags(new Tag(ctx.getChild(0).getText(), null, readMultipleRules(ctx.fieldTagEnd()), null));
    }

    @Override
    public void exitMethodTag(JavadocParser.MethodTagContext ctx) {

        String name = ctx.NAME(0).getText();
        if(currentDoc.getReturns() != null){
            System.err.println("ERR: Eine Methode kann nur einen Rückgabewert haben: <" + currentDoc.getReturns().getTagName() + "> wird mit <" + name + "> überschrieben!");
        }

        currentDoc.setReturns(new Tag(ctx.RETURN().getText(), name, readMultipleToken(ctx.NAME().subList(1, ctx.NAME().size() - 1)), null));
    }

    @Override
    public void exitJavaMethod(JavadocParser.JavaMethodContext ctx) {

        // Remove ( at the end
        String methodName = ctx.FUNC_NAME().getText();
        methodName = methodName.substring(0, methodName.length() - 1);

        // Return
        String methodType = ctx.type().getText();

        if (currentDoc == null) {
            System.out.println("WRN: Fehlendes Javadoc für Methode <" + methodName + ">");
            currentDoc = new Documentation();
        }
        else {
            if ("void".equals(methodType) && currentDoc.getReturns() != null) {
                System.err.println("ERR: @return im Javadoc für void-Methode <" + methodName + ">");
            } else if (!"void".equals(methodType) && currentDoc.getReturns() == null) {
                System.out.println("WRN: Fehlendes @return für Methode <" + methodName + "> mit Returntype <" + methodType + ">");
            }
            String root = "Methode";

            // Param
            checkParams(methodName, ctx.javaParams(), root);

            // Throws
            checkThrows(methodName, ctx.throwing(), root);
        }

        String annotation = "";
        if (ctx.annotation() != null) {
            annotation = readAnnotations(ctx.annotation());
        }

        String accessmod = ctx.ACCESSMODS() == null ? "" : ctx.ACCESSMODS().getText();

        String modifier = readMultipleRules(ctx.modifier());

        HashMap<String, String> currentParams = getParams(ctx.javaParams().javaParam());

        gen.writeMethod(level, hierarchy + ":" + methodName, annotation, accessmod, modifier, methodType, methodName, currentParams, currentDoc);

        currentDoc = null;
    }

    @Override
    public void exitJavaConstructor(JavadocParser.JavaConstructorContext ctx) {
        // Remove ( at the end
        String constructorName = ctx.FUNC_NAME().getText();
        constructorName = constructorName.substring(0, constructorName.length() - 1);

        if (currentDoc == null) {
            System.out.println("WRN: Fehlendes Javadoc für Konstruktor <" + constructorName + ">");
            currentDoc = new Documentation();
        }
        else {
            String root = "Konstruktor";

            // Param
            checkParams(constructorName, ctx.javaParams(), root);

            // Throws
            checkThrows(constructorName, ctx.throwing(), root);
        }

        String annotation = "";
        if (ctx.annotation() != null) {
            annotation = readAnnotations(ctx.annotation());
        }

        String accessmod = ctx.ACCESSMODS() == null ? "" : ctx.ACCESSMODS().getText();

        HashMap<String, String> currentParams = getParams(ctx.javaParams().javaParam());

        gen.writeConstructor(level, hierarchy + ":" + constructorName, annotation, accessmod, constructorName, currentParams, currentDoc);

        currentDoc = null;
    }

    @Override
    public void exitJavaField(JavadocParser.JavaFieldContext ctx) {
        String fieldName = ctx.NAME().getText();

        // Return
        String fieldType = ctx.type().getText();

        if (currentDoc == null) {
            System.out.println("INFO: Kein Javadoc für Variable <" + fieldName + "> vom Typ <" + fieldType + ">");
            currentDoc = new Documentation();
        }

        String annotation = "";
        if (ctx.annotation() != null) {
            annotation = readAnnotations(ctx.annotation());
        }

        String accessmod = ctx.ACCESSMODS() == null ? "" : ctx.ACCESSMODS().getText();

        String modifier = readMultipleRules(ctx.modifier());

        gen.writeField(level, hierarchy + ":" + fieldName, annotation, accessmod, modifier, fieldType, fieldName, currentDoc);

        currentDoc = null;
    }

    private HashMap<String, String> getParams(List<JavadocParser.JavaParamContext> ctx) {
        HashMap<String, String> map = new HashMap<>();
        for(JavadocParser.JavaParamContext item : ctx){
            map.put(item.NAME().getText(), item.VARARGS() != null ? item.type().getText() + item.VARARGS().getText() : item.type().getText());
        }

        return map;
    }

    private String readAnnotations(List<JavadocParser.AnnotationContext> ctx) {
        StringBuilder annotations = new StringBuilder();

        for (JavadocParser.AnnotationContext item : ctx) {
            TerminalNode funcName = item.FUNC_NAME();
            annotations.append("@").append(funcName == null ? item.typeName().getText() + "(" : funcName.getText());

            if (item.skipCodeToParatheses() != null) {
                annotations.append(skipCodeToParatheses(item.skipCodeToParatheses())).append(")").append("\n");
            }
        }
        return annotations.toString();
    }

    private void checkThrows(String methodName, JavadocParser.ThrowingContext throwing, String root) {
        if (throwing == null) {
            return;
        }

        for (JavadocParser.TypeNameContext thrower : throwing.typeName()) {
            String throwerName = thrower.getText();

            boolean found = false;
            for(Tag tag : currentDoc.getThrows()){
                if(tag.getTagName().equals(throwerName)){
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("WRN: Fehlendes @throw für Exception <" + throwerName + "> in " + root + " <" + methodName + ">");
            }
        }
    }

    private void checkParams(String methodName, JavadocParser.JavaParamsContext javaParamsContext, String root) {

        ArrayList<Tag> docParams = new ArrayList<>(currentDoc.getParams());

        for (JavadocParser.JavaParamContext param : javaParamsContext.javaParam()) {
            String paramName = param.NAME().getText();

            boolean found = false;
            for(Tag tag : docParams){
                String tagType = tag.getTagName();
                if(tagType.equals(paramName)){
                      found = true;
                      docParams.remove(tag);
                      break;
                }
            }
            if(!found){
                System.out.println("WRN: Fehlendes @param für Parameter <" + paramName + "> in " + root + " <" + methodName + ">");
            }
        }

        for (Tag param : docParams) {
            System.err.println("ERR: <@param" + param.getTagName() + "> ohne zugehörigen Parameter für Methode <" + methodName + ">");
        }
    }

    @Override
    public void exitJavaClassOrInterfaceDef(JavadocParser.JavaClassOrInterfaceDefContext ctx) {
        if (gen == null) {
            gen = new LaTexGenerator(docTitle, packageName);
        }

        String type = ctx.INTERFACE() == null ? "class" : "interface";
        String name = ctx.NAME().getText();

        if (hierarchy.isEmpty()) {
            hierarchy = name;
        } else {
            hierarchy += "." + name;
        }

        if (currentDoc == null) {
            System.out.println("WRN: Fehlendes Javadoc für " + type + " <" + name + ">");
        }

        String annotation = "";
        if (ctx.annotation() != null) {
             annotation = readAnnotations(ctx.annotation());
        }

        String accessmod = ctx.ACCESSMODS() == null ? "" : ctx.ACCESSMODS().getText();

        String modifier = readMultipleRules(ctx.modifier());

        String polymorphy = readMultipleRules(ctx.polymorphy());

        gen.writeClassOrInterface(level, hierarchy, annotation, accessmod, modifier, type, name, polymorphy, currentDoc);
        currentDoc = null;

        level++;
    }

    @Override
    public void exitJavaClassOrInterface(JavadocParser.JavaClassOrInterfaceContext ctx) {
        level--;
        hierarchy = hierarchy.substring(0, hierarchy.lastIndexOf('.'));
    }

    private <T extends ParserRuleContext> String readMultipleRules(List<T> ctx) {
        String items = "";
        for (T item : ctx) {
            if (item.getChildCount() > 0) {
                for (int i = 0; i < item.getChildCount(); i++) {
                    items = addWhiteSpace(items, item.getChild(i).getText());
                }
            } else {
                items = addWhiteSpace(items, item.getText());
            }
        }
        return items;
    }

    private <T extends TerminalNode> String readMultipleToken(List<T> ctx) {
        String items = "";
        for (T item : ctx) {
            items = addWhiteSpace(items, item.getText());
        }
        return items;
    }

    private String skipCodeToParatheses(JavadocParser.SkipCodeToParathesesContext ctx) {
        String text = readMultipleRules(ctx.noQuoteOrPclose());
        if (ctx.skipToQuote() != null) {
            text += "\"";

            text += skipCodeToQuote(ctx.skipToQuote());
            text += skipCodeToParatheses(ctx.skipCodeToParatheses());
        }

        return text;
    }

    private String skipCodeToQuote(JavadocParser.SkipToQuoteContext ctx) {
        return readMultipleRules(ctx.notQuote()) + "\"";
    }

    public static void main(String[] args) throws Exception {

        String path = "src/examples/SimpleExample.java";

        FileInputStream inStream = new FileInputStream(path);
        docTitle = path.replaceAll("^.*/", "").replaceAll("\\..*$", "");

        JavadocLexer lexer = new JavadocLexer(CharStreams.fromStream(inStream));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavadocParser parser = new JavadocParser(tokens);

        parser.addParseListener(new JavadocListener());

        parser.documentation();
    }
}
