import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Evtl auf Stringbuilder / String.format umändern

public class JavadocListener extends JavadocParserBaseListener{

    private Documentation currentDoc;
    private HashMap<ParserRuleContext, Documentation> classDocs = new HashMap<>();
    private static String docTitle;
    private LaTexGenerator gen;

    @Override public void enterDocStart(JavadocParser.DocStartContext ctx){
        gen = new LaTexGenerator(docTitle);
    }

    @Override public void exitDocStart(JavadocParser.DocStartContext ctx){
        gen.closeWriter();
    }

    @Override public void exitDocText(JavadocParser.DocTextContext ctx){
        currentDoc.setDescription(addWhiteSpace(currentDoc.getDescription(), ctx.getText()));
    }

    @Override public void exitInlineTag(JavadocParser.InlineTagContext ctx){
        JavadocParser.InlineTagContentContext inlineTagContentContext = ctx.inlineTagContent();
        String text = inlineTagContentContext == null ? "" : inlineTagContentContext.getText();

        currentDoc.addInlineTags(new Tag(ctx.inlineTagName().getText(), text));

        String desc = currentDoc.getDescription();
        currentDoc.setDescription(desc == null? "{*/}" : desc + " " + "{*/}");
    }

    private String addWhiteSpace(String old, String add){
        return old == null? add : old + " " + add;
    }

    @Override public void enterJavaDocStart(JavadocParser.JavaDocStartContext ctx){
        currentDoc = new Documentation();
    }

    @Override public void exitParams(JavadocParser.ParamsContext ctx){

        currentDoc.addParam(ctx.NAME(0).getText());
    }

    @Override public void exitThrows(JavadocParser.ThrowsContext ctx){

        currentDoc.addThrows(ctx.typeName().getText());
    }

    @Override public void exitMethodTag(JavadocParser.MethodTagContext ctx){

        currentDoc.setReturns();
    }

    @Override public void exitJavaMethod(JavadocParser.JavaMethodContext ctx){

        // Remove ( at the end
        String methodName = ctx.FUNC_NAME().getText();
        methodName = methodName.substring(0, methodName.length() -1);

        if(currentDoc == null){
            System.out.println("WRN: Fehlendes Javadoc für Methode <" + methodName + ">");
        }
        else{
            // Return
            String methodType = ctx.type().getText();

            if("void".equals(methodType) && currentDoc.getReturns()){
                System.err.println("ERR: @return im Javadoc für void-Methode <" + methodName + ">");
            }
            else if(!"void".equals(methodType) && !currentDoc.getReturns()){
                System.out.println("WRN: Fehlendes @return für Methode <" + methodName + "> mit Returntype <" + methodType + ">");
            }
            String root = "Methode";

            // Param
            checkParams(methodName, currentDoc.getParams(), ctx.javaParams(), root);

            // Throws
            checkThrows(methodName, ctx.throwing(), root);

            currentDoc = null;
        }
    }

    @Override public void exitJavaConstructor(JavadocParser.JavaConstructorContext ctx){

        // Remove ( at the end
        String methodName = ctx.FUNC_NAME().getText();
        methodName = methodName.substring(0, methodName.length() -1);

        if(currentDoc == null){
            System.out.println("WRN: Fehlendes Javadoc für Konstruktor <" + methodName + ">");
        }
        else{
            String root = "Konstruktor";

            // Param
            checkParams(methodName, currentDoc.getParams(), ctx.javaParams(), root);

            // Throws
            checkThrows(methodName, ctx.throwing(), root);

            currentDoc = null;
        }
    }

    private void checkThrows(String methodName, JavadocParser.ThrowingContext throwing, String root) {
        ArrayList<String> docThrows = currentDoc.getThrows();

        if(throwing == null){
            return;
        }

        for (JavadocParser.TypeNameContext thrower : throwing.typeName()) {
            String throwerName = thrower.getText();

            if (!docThrows.contains(throwerName)) {
                System.out.println("WRN: Fehlendes @throw für Exception <" + throwerName + "> in " + root + " <" + methodName + ">");
            }
        }
    }

    private void checkParams(String methodName, ArrayList<String> docParams, JavadocParser.JavaParamsContext javaParamsContext, String root) {

        for(JavadocParser.JavaParamContext param: javaParamsContext.javaParam()){
            String paramName = param.NAME().getText();

            if(!docParams.contains(paramName)){
                System.out.println("WRN: Fehlendes @param für Parameter <" + paramName + "> in " + root + " <" + methodName + ">");
            }
            docParams.remove(paramName);
        }

        for(String param : docParams){
            System.err.println("ERR: <@param " + param + "> ohne zugehörigen Parameter für Methode <" + methodName + ">");
        }
    }

    @Override public void exitJavaClassOrInterfaceDef(JavadocParser.JavaClassOrInterfaceDefContext ctx) {
        String type = ctx.INTERFACE() == null ? "Klasse" : "Interface";
        String name = ctx.NAME().getText();

        if(currentDoc == null){
            System.out.println("WRN: Fehlendes Javadoc für " + type + " <" + name + ">");
        }
        else{
            if(ctx.annotation() != null){
                for(JavadocParser.AnnotationContext item : ctx.annotation()){
                    if(item != null && item.skipCodeToParatheses() != null){
                        skipCodeToParatheses(item.skipCodeToParatheses());
                    }
                }
            }

            gen.writeClassOrInterface()
            currentDoc = null;
        }
    }

    private String skipCodeToParatheses(JavadocParser.SkipCodeToParathesesContext ctx) {
        String text = null;

        for (JavadocParser.NoQuoteOrPcloseContext item : ctx.noQuoteOrPclose()){
            text = addWhiteSpace(text, item.getText());
        }

        if(ctx.skipToQuote() != null){
            text += "\"";

            text += skipCodeToQuote(ctx.skipToQuote());
            text += skipCodeToParatheses(ctx.skipCodeToParatheses());
        }

        return text + ")";
    }

    private String skipCodeToQuote(JavadocParser.SkipToQuoteContext ctx) {
        String text = null;

        for (JavadocParser.NotQuoteContext item : ctx.notQuote()){
            text = addWhiteSpace(text, item.getText());
        }

        return text + "\"";
    }

    @Override
    public void exitJavaField(JavadocParser.JavaFieldContext ctx){
        String type = ctx.type().getText();
        String name = ctx.NAME().getText();

        // Felder evtl. gar nicht prüfen
        if(currentDoc == null){
            System.out.println("INFO: Kein Javadoc für Variable <" + name + "> vom Typ <" + type + ">");
        }
        else{
            currentDoc = null;
        }
    }


    public static void main(String[] args) throws Exception {

        String path = "src/examples/SimpleExampleWithClass.java";

        FileInputStream inStream = new FileInputStream(path);
        docTitle = path.replaceAll("^.*/", "").replaceAll("\\..*$", "");

        JavadocLexer lexer = new JavadocLexer(CharStreams.fromStream(inStream));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavadocParser parser = new JavadocParser(tokens);

        parser.addParseListener(new JavadocListener());

        parser.documentation();
    }
}
