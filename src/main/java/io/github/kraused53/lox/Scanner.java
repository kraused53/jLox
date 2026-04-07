package io.github.kraused53.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

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

    // Peek at next char without consuming it
    private char peek() {
        if( isAtEnd() ) {
            return '\0';
        }
        return source.charAt( current );
    }

    // Peek at the next-next char without consuming it
    private char peekNext() {
        if( current + 1 >= source.length() ) {
            return '\0';
        }

        return source.charAt( current + 1 );
    }

    // Check to see if given char is numeric
    private Boolean isDigit( char c ) {
        return ( c >= '0' ) && ( c <= '9' );
    }

    // Check to see if given char is an alphabetical character
    private Boolean isAlpha( char c ) {
        return  ( c >= 'a'  &&  c <= 'z' ) ||
                ( c >= 'A'  &&  c <= 'Z' ) ||
                c == '_';
    }

    // Check to see if given char is an alphabetical or numerical character
    private Boolean isAlphaNumeric( char c ) {
        return isAlpha( c ) ||  isDigit( c );
    }

    // Scan a numeric literal
    private void number() {
        // Decimal portion
        while( isDigit( peek() ) ) {
            advance();
        }

        if( peek() == '.' && isDigit( peekNext() ) ) {
            // Consume '.'
            advance();

            // Fractional portion
            while( isDigit( peek() ) ) {
                advance();
            }
        }

        addToken(
                TokenType.NUMBER,
                Double.parseDouble( source.substring( start, current ) )
        );
    }

    // Scan an identifier
    private void identifier() {
        // Scan until reached the end of the identifier
        while( isAlphaNumeric( peek() ) ) {
            advance();
        }

        // Check to see if the scanned identifier is a keyword
        String text = source.substring( start, current );

        // If it is, type will have a value. Otherwise, null
        TokenType type = keywords.get( text );
        if( type == null ) {
            // If null, set to identifier
            type = TokenType.IDENTIFIER;
        }

        // Add token to list
        addToken( type );
    }

    // Scan a string literal
    private void string() {
        while( peek() != '"' && !isAtEnd() ) {
            if( peek() == '\n' ) {
                line++;
            }
            advance();
        }

        if( isAtEnd() ) {
            Lox.error( line, "Unterminated string..." );
            return;
        }

        // Consume closing '"'
        advance();

        // Trim the quotes
        String value = source.substring( start + 1, current - 1 );

        // Add String token to list
        addToken( TokenType.STRING, value );
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
            case '!': addToken( match('=') ?    TokenType.BANG_EQUAL :    TokenType.BANG ); break;
            case '=': addToken( match('=') ?   TokenType.EQUAL_EQUAL :   TokenType.EQUAL ); break;
            case '<': addToken( match('=') ?    TokenType.LESS_EQUAL :    TokenType.LESS ); break;
            case '>': addToken( match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER ); break;

            // Handle '/'
            case '/':
                if( match('/') ) {
                    // A comment will go until the end of the current line
                    while( peek() != '\n' && !isAtEnd() ) {
                        advance();
                    }
                }else {
                    addToken( TokenType.SLASH );
                }
                break;

            // Literals
            case '"': string(); break;

            // Skip whitespaces
            case ' ':
            case '\r':
            case '\t':
                break;

            // '\n' will advance the line count
            case '\n':
                line++;
                break;

            default:
                // Check for numeric
                if( isDigit( c ) ) {
                    number();
                }else if( isAlpha( c ) ) {
                    identifier();
                }else {
                    Lox.error( line, "Unexpected character." );
                }
                break;
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

    // Define a map of the strings to keywords
    static {
        keywords = new HashMap<>();
        keywords.put(    "and",    TokenType.AND );
        keywords.put(  "class",  TokenType.CLASS );
        keywords.put(   "else",   TokenType.ELSE );
        keywords.put(  "false",  TokenType.FALSE );
        keywords.put(    "for",    TokenType.FOR );
        keywords.put(    "fun",    TokenType.FUN );
        keywords.put(     "if",     TokenType.IF );
        keywords.put(    "nil",    TokenType.NIL );
        keywords.put(     "or",     TokenType.OR );
        keywords.put(  "print",  TokenType.PRINT );
        keywords.put( "return", TokenType.RETURN );
        keywords.put(  "super",  TokenType.SUPER );
        keywords.put(   "this",   TokenType.THIS );
        keywords.put(   "true",   TokenType.TRUE );
        keywords.put(    "var",    TokenType.VAR );
        keywords.put(  "while",  TokenType.WHILE );
    }
}
