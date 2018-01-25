import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Evtl auf Stringbuilder / String.format umändern

public class JavadocListener extends JavadocParserBaseListener{

    private Documentation currentDoc;
    private HashMap<ParserRuleContext, Documentation> classDocs = new HashMap<>();

    @Override public void enterJavaDocStart(JavadocParser.JavaDocStartContext ctx){
        currentDoc = new Documentation();
    }

    @Override public void enterDocumentation(JavadocParser.DocumentationContext ctx){
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

    @Override
    public void enterJavaClassOrInterface(JavadocParser.JavaClassOrInterfaceContext ctx){
        if(currentDoc != null){
            classDocs.put(ctx, new Documentation(currentDoc));
            currentDoc = null;
        }
        else{
            classDocs.put(ctx, null);
        }
    }

    @Override
    public void exitJavaClassOrInterface(JavadocParser.JavaClassOrInterfaceContext ctx){

        String type = ctx.INTERFACE() == null ? "Klasse" : "Interface";
        String name = ctx.NAME().getText();

        if(classDocs.get(ctx) == null){
            System.out.println("WRN: Fehlendes Javadoc für " + type + " <" + name + ">");
        }
        else{
            currentDoc = null;
        }
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
        JavadocLexer lexer = new JavadocLexer(CharStreams.fromStream(new FileInputStream("src/examples/SimpleExampleWithClass.java")));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavadocParser parser = new JavadocParser(tokens);

        parser.addParseListener(new JavadocListener());

        parser.documentation();
    }
}
