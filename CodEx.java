/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import java.io.*;

/** DU 1. !!! Nemente jmeno tridy !!!
  * 
  * @author Jan Mares
  */
public class CodEx {

    public static void main(String[] argv) {

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            int c;
            while ((c = input.read()) != -1) {
                System.out.print((char) c);
            }
        } catch (IOException ex) {
            System.out.println("Nastala IOException");
        }

    }
}

