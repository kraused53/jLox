package io.github.kraused53.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst( String outDir, String baseName, List<String> types ) throws IOException {
        String path = outDir + "/" + baseName + ".java";
        System.out.println("Generating AST for " + path);

        PrintWriter writer = new PrintWriter( path, StandardCharsets.UTF_8);

        writer.println("package io.github.kraused53.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");
        writer.println();

        for( String type : types ) {
            String className = type.split(":")[0].trim();
            String    fields = type.split(":")[1].trim();

            defineType( writer, baseName, className, fields );
            writer.println();
        }

        writer.println("}");
        writer.println();
        writer.close();
    }

    private static void defineType( PrintWriter writer, String baseName, String className, String fieldList ) {
        String tabSpace = "\t";
        writer.println( tabSpace.repeat( 1 ) + "static class " + className + " extends " + baseName + " {" );
        writer.println();
        writer.println( tabSpace.repeat( 2 ) + className + "( " + fieldList + " ) {"  );

        String[] fields = fieldList.split(", ");
        for ( String field : fields ) {
            String name = field.split(" ")[1];
            writer.println( tabSpace.repeat( 3 ) + "this." + name + " = " + name + ";" );
        }

        writer.println( tabSpace.repeat( 2 ) + "}" );

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println( tabSpace.repeat( 2 ) + "final " + field + ";");
        }

        writer.println( tabSpace.repeat( 1 ) + "}");
    }
}
