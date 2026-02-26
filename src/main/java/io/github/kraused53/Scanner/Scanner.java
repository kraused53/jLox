package io.github.kraused53.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import io.github.kraused53.Token.Token;
import io.github.kraused53.Token.TokenType;
import io.github.kraused53.jLox;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Constructor
    public Scanner( String source ) {
        this.source = source;
    }

    // Get next char in source and advance current pointer
    private char advance() {
        char ret = source.charAt( current );
        current++;
        return ret;
    }

    // Add the given token to the list ( no literal )
    private void addToken( TokenType type ) {
        addToken( type, null );
    }

    // Add the given token, and its literal / string rep. to list
    private void addToken( TokenType type, Object literal ) {
        String text = source.substring( start, current );
        tokens.add( new Token( type, text, literal, line ) );
    }

    // Used to select 1 or 2 char lexemes
    private Boolean match( char expected ) {
        // Nothing to check against...
        if( isAtEnd() ) {
            return false;
        }

        // Not a match
        if( source.charAt( current ) != expected ) {
            return false;
        }

        // Consume next character and return true
        current++;
        return true;
    }

    // Scan individual chars into tokens
    private void scanToken() {
        char c = advance();

        switch( c ) {
            // Single char lexemes
            case '(': addToken( TokenType.LEFT_PAREN ); break;
            case ')': addToken( TokenType.RIGHT_PAREN ); break;
            case '{': addToken(  TokenType.LEFT_BRACE ); break;
            case '}': addToken( TokenType.RIGHT_BRACE ); break;
            case ',': addToken(       TokenType.COMMA ); break;
            case '.': addToken(         TokenType.DOT ); break;
            case '-': addToken(       TokenType.MINUS ); break;
            case '+': addToken(        TokenType.PLUS ); break;
            case ';': addToken(   TokenType.SEMICOLON ); break;
            case '*': addToken(        TokenType.STAR ); break;

            // 1 -> 2 char lexemes
            case '!': addToken( match('=') ? TokenType.BANG_EQUAL : TokenType.EQUAL ); break;

            default: jLox.error( line, "Unexpected character." ); break;
        }
    }

    // Generate a list of tokens, ending with an EOF token
    public List<Token> scanTokens() {
        while( !isAtEnd() ) {
            start = current;
            scanToken();
        }

        tokens.add( new Token( TokenType.EOF, "", null, line ) );
        return tokens;
    }

    // Return true if nothing else to scan
    private Boolean isAtEnd() {
        return current >= source.length();
    }
}
