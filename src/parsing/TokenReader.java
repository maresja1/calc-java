package parsing;

import exception.BadExpressionFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class TokenReader{
    private HashMap<TokenType, Pattern> tokenHashMap;
    private Pattern whiteCharPattern;
    private char delimiter = ';';
    private BufferedReader in;
    private String[] buffer;
    private int posInBuffer = 0;
    private int posInWord = 0;
    private boolean EOF = false;

    /**
     * Creates a buffering character-input stream that uses a default-sized
     * input buffer.
     *
     * @param in A Reader
     */
    public TokenReader(Reader in) {
        this.in = new BufferedReader(in);
        tokenHashMap = new HashMap<TokenType, Pattern>();
        HashMap<TokenType, Pattern> funcArgsHashMap = new HashMap<TokenType, Pattern>();
        tokenHashMap.put(TokenType.asterisk, Pattern.compile("*",Pattern.LITERAL));
        tokenHashMap.put(TokenType.slash, Pattern.compile("/",Pattern.LITERAL));
        tokenHashMap.put(TokenType.plus, Pattern.compile("+",Pattern.LITERAL));
        tokenHashMap.put(TokenType.minus, Pattern.compile("-",Pattern.LITERAL));
        tokenHashMap.put(TokenType.equals, Pattern.compile("=",Pattern.LITERAL));
        tokenHashMap.put(TokenType.def, Pattern.compile("DEF",Pattern.LITERAL));
        tokenHashMap.put(TokenType.lBracket, Pattern.compile("(",Pattern.LITERAL));
        tokenHashMap.put(TokenType.rBracket, Pattern.compile(")",Pattern.LITERAL));
        tokenHashMap.put(TokenType.argSeparator, Pattern.compile(",",Pattern.LITERAL));
        tokenHashMap.put(TokenType.precisionKey, Pattern.compile("precision",Pattern.LITERAL));
        tokenHashMap.put(TokenType.lBrace, Pattern.compile("{",Pattern.LITERAL));
        tokenHashMap.put(TokenType.rBrace, Pattern.compile("}",Pattern.LITERAL));
        funcArgsHashMap.put(TokenType.number, Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"));
        funcArgsHashMap.put(TokenType.identifier, Pattern.compile("[a-zA-Z]+"));
        Pattern newLinePattern = Pattern.compile("(\\r)?\\n");
        whiteCharPattern = Pattern.compile("\\s+");
        tokenHashMap.putAll(funcArgsHashMap);
        try {
            fillBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TOKENS:
     */
    public enum TokenType {
        lBracket,
        rBracket,
        equals,
        plus,
        minus,
        asterisk,
        slash,
        def,
        identifier,
        number,
        argSeparator,
        precisionKey,
        lBrace,
        rBrace
    }

    public static class Token {
        public TokenType type;
        public String value;
    }

    private String readBuffer(BufferedReader reader, char stopChar) throws IOException {
        StringBuilder builder = new StringBuilder();
        char c;
        do{
            int next = reader.read();
            if(next == -1){
                return builder.length() > 0 ? builder.toString() : null;
            }
            c = (char) next;
            builder.append(c);
        } while (c != stopChar);
        return builder.toString();
    }

    private void fillBuffer() throws IOException {
        do {
            String line = readBuffer(in, delimiter);
            if (line == null) {
                EOF = true;
                return;
            } else {
                buffer = whiteCharPattern.split(line);
                posInBuffer = 0;
                posInWord = 0;
            }
        }while(buffer.length == 0);
    }

    private String getActualWord(){
        if(posInBuffer < buffer.length && posInWord == buffer[posInBuffer].length()){
            posInWord = 0;
            ++posInBuffer;
        }
        if(posInBuffer >= buffer.length){
            return null;
        }
        return buffer[posInBuffer];
    }

    public boolean hasToken(TokenType tokenType){
        Pattern tokenPattern = tokenHashMap.get(tokenType);
        return hasNext(tokenPattern);
    }

    private boolean hasNext(Pattern tokenPattern) {
        String actualWord = getActualWord();
        if(actualWord == null){
            return false;
        }
        Matcher matcher = tokenPattern.matcher(actualWord);
        matcher.region(posInWord,matcher.regionEnd());
        return matcher.lookingAt();
    }

    private Matcher matchNext(Pattern tokenPattern){
        String actualWord = getActualWord();
        if(actualWord == null){
            throw new BadExpressionFormatException("Unexpected end of line.");
        }
        Matcher matcher = tokenPattern.matcher(actualWord);
        matcher.region(posInWord,matcher.regionEnd());
        if(matcher.lookingAt()){
            return matcher;
        } else {
            throw new BadExpressionFormatException("Unexpected token near " + actualWord.substring(posInWord));
        }
    }

    public boolean hasDelimiter(){
        getActualWord();
        return posInBuffer == buffer.length - 1 && buffer[posInBuffer].charAt(posInWord) == delimiter;
    }

    public void skipRestToDelimiter() throws IOException {
        fillBuffer();
    }
    public void skipDelimiter() throws IOException {
        if(hasDelimiter()){
            fillBuffer();
        } else {
            throw new BadExpressionFormatException("Expected line end");
        }
    }

    public Token matchToken(TokenType tokenType){
        Pattern tokenPattern = tokenHashMap.get(tokenType);
        Matcher matcher = matchNext(tokenPattern);
        String next = getActualWord().substring(posInWord, matcher.end());
        posInWord = matcher.end();

        Token token = new Token();
        token.type = tokenType;
        token.value = next;
        return token;
    }

    public boolean hasFileEnd(){
        return EOF;
    }
}
