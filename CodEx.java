/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import sun.plugin.dom.exception.InvalidStateException;

import java.io.*;
import java.io.BufferedReader;
import java.lang.Character;
import java.lang.StringBuilder;
import java.util.ArrayList;

/** DU 1. !!! Nemente jmeno tridy !!!
  * 
  * @author Jan Mares
  */
public class CodEx {

    public static class WordReader extends BufferedReader{
        /**
         * Creates a buffering character-input stream that uses a default-sized
         * input buffer.
         *
         * @param in A Reader
         */
        public WordReader(Reader in) {
            super(in);
        }

        private Character readChar() throws IOException {
            int i = this.read();
            if(i==-1){
                return null;
            } else {
                return Character.forDigit()
            }
        }
        /**
         * Reads a word from the stream and returns it. If the stream is at the end
         * returns null.
         *
         * @return next word from the stream or null if stream is at the end
         * @throws IOException
         */
        public String readWord() throws IOException {
            readLine();
            StringBuilder builder = new StringBuilder();
            int i;
            while (() != -1) {
                char c = (char)i;
                while(Character.isWhitespace(c)){
                    int eols = 0;
                    do{
                        c = (char)i;
                        eols += isLineSeparator(c) ? 1 : 0;
                        mark(2);
                    } while(Character.isWhitespace(c) && (i = this.read()) != -1);
                    if(i!=-1){
                        this.reset();
                    }
                    break;
                }
                builder.append(c);
            }
            if(builder.length() == 0 && i == -1){
                return null;
            }
            return builder.toString();
        }

        private boolean isLineSeparator(char c){
            return c == '\n';
        }
    }

    public static class BlockFormatter{
        private InputStream inputStream;
        private PrintStream outputStream;
        private ArrayList<String> rowBuffer = new ArrayList<String>();
        private int lineMax;
        private int rowLetters;

        public BlockFormatter(InputStream inputStream, PrintStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public void format() throws IOException {
            WordReader input = new WordReader(new InputStreamReader(inputStream));
            initialize(input);
            String word = input.readWord();
            while (word != null) {
                if(word.length() == 0){

                }
                if(lineSize() + 1 + word.length() > lineMax){
                    if(rowBuffer.size() == 0){
                        addWord(word);
                        printRow();
                    } else {
                        printRow();
                    }
                }
                addWord(word);
                word = input.readWord();
            }
            printRow();
        }

        private void addWord(String word) {
            rowBuffer.add(word);
            rowLetters += word.length();
        }

        private void clearWords(){
            rowBuffer.clear();
            rowLetters = 0;
        }

        private String repeatChar(char c, int count){
            //TODO: Possibly cache already created strings for speed-up
            assert count >= 0;
            if(count == 0){
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<count;i++){
                builder.append(c);
            }
            return builder.toString();
        }

        private void printRow(){
            int missingSpace = Math.max(lineMax - rowLetters,0);
            int wordWithSpaceCount = rowBuffer.size() - 1;
            int perEach = missingSpace / wordWithSpaceCount;
            int fromLeft = missingSpace % wordWithSpaceCount;
            int i=0;
            for(;i<wordWithSpaceCount;i++){
                int spaces = perEach;
                if(fromLeft > 0){
                    ++spaces;
                    --fromLeft;
                }
                outputStream.print(rowBuffer.get(i));
                outputStream.print(repeatChar(' ',spaces));
            }
            outputStream.println(rowBuffer.get(i));
            clearWords();
        }

        private int lineSize(){
            return rowLetters + rowBuffer.size() - 1;
        }

        private void initialize(WordReader input) throws IOException {
            String str = input.readWord();
            if(str == null){
                throw new InvalidStateException("Bat pants");
            }
            this.lineMax = Integer.parseInt(str);
        }
    }

    public static void main(String[] argv) {
        try {
            BlockFormatter formatter = new BlockFormatter(System.in, System.out);
            formatter.format();
        } catch(Exception e){
            System.out.println("Error");
        }
    }
}

