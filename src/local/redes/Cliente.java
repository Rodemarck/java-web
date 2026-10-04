package local.redes;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


// @author Rodemarck Melo dos Santos Júnior
public class Cliente {
    static void main() throws IOException {

        try(var s = new Socket("127.0.0.1", 8012);){
            var in = new Scanner(s.getInputStream());
            var out = new PrintStream(s.getOutputStream());
            var sc = new Scanner(System.in);
            var aux = "";
            System.out.println("digite ajuda");
            while(!s.isClosed()){
                System.out.print("client >>");
                aux = sc.nextLine();
                out.println(aux);
                out.flush();
                aux = in.nextLine();
                System.out.printf("%s << servidor\n",aux.replace("<linha>","\n"));

            }
        }
    }
}
