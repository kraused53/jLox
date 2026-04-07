package io.github.kraused53.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    // Track if there has been an error
    static Boolean hadError = false;

    static void main( String[] args ) throws IOException {
        if( args.length > 1 ) {
            System.out.println("Usage: jlox [script].lox");
            System.exit(64);
        }else if( args.length == 1 ) {
            runFile( args[0] );
        }else {
            runPrompt();
        }
    }

    /*
    *   Report error
    */
    static void report( int line, String where, String message ) {
        System.err.println( "[line " + line + "] Error" + where + ": " + message );
        hadError = true;
    }

    /*
     *   Aggregate error report
     */
    public static void error( int line, String message ) {
        report( line, "", message );
    }

    /*
    *   Run given String of Lox commands
    */
    private static void run( String script ) throws IOException {
        Scanner scanner = new Scanner( script );
        List<Token> tokens = scanner.scanTokens();

        for( Token token : tokens ) {
            System.out.println( token );
        }
    }

    /*
    *   Attempt to load a Lox script
    *   If successful, pass byte array as string to run()...
    */
    private static void runFile( String filepath ) throws IOException {
        byte[] rawBytes = Files.readAllBytes( Paths.get( filepath ) );
        run( new String( rawBytes, Charset.defaultCharset() ) );

        // Check for errors
        if( hadError ) {
            System.exit(65);
        }
    }

    /*
    *   Set up user prompt, python-style
    *   Each line is fed to run()...
    */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader( System.in );
        BufferedReader reader = new BufferedReader( input );

        while( true ) {
            System.out.print( "> " );
            String line = reader.readLine();

            if( line == null ) {
                break;
            }

            run( line );

            // Errors in interactive shell should NOT close shell
            hadError = false;
        }
    }
}
