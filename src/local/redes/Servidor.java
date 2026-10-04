package local.redes;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


// @author Rodemarck Melo dos Santos Júnior
public class Servidor {
    static ServerSocket ss;
    static final String MENSAGEM_HELP = """
            Servidor baseado em Socket para validação de CPF.
            Para utiliza-lo, digite no console do cliente e dar enter.
            Sua mensagem vai interpretada para verificar se é um comando
            ou é não. Caso não seja um dos comandos aceitados vai ser interpretado
            como sendo um possivel CPF. e será validado.
            
            P.S. interpretar multiplas linhas, deu muita dor de cabeça então criei
            uma tag < linha >. mas sem os espaços evite digitar, para não quebrar.
            
            os comandos existentem são:
            sair() -> encerra a conexão por parte do servidor
            ajuda() -> exibe esta mensagem.
            ping() -> exibe a mensagem "pong"
            """;

    static final String MENSAGEM_ERRO_PADRAO = """
            Este CPF é inválido.
            
            0 CPF enviando não segue o padrão esperado, verifique se a quantidade de
            números está correta.
            padrão esperado: 333.333.333-22
            """;

    static final String MENSAGEM_ERRO_VALIDACAO = """
            Este CPF é inválido.
            
            0 CPF enviando não passou nos testes de validação.
            """;

    static final String MENSAGEM_ERRO_NUMERO_REPETIDO = """
            Este CPF é inválido.
            
            0 CPF com todos so números iguais, é reprovado, pelo enunciado.
            """;
    static String msg(String txt){
        return txt.replace("\n","<linha>");
    }
    static boolean checaComandos(String txt,PrintStream out, Socket con) throws IOException {
        var txtl = txt.toLowerCase();
        if(txtl.matches("(\\.\\s\\t)*(exit)|(sair)(\\(\\))?")){
            out.println("Adios");
            out.flush();
            con.close();
            ss.close();
            System.out.println("Desligando o servbidor");
            System.exit(0);
            return false;
        }
        if(txtl.matches("(\\.\\s\\t)*(help)|(ajuda)(\\(\\))?")){
            out.println(msg(MENSAGEM_HELP));
            out.flush();
            return false;
        }
        if(txtl.matches("(\\.\\s\\t)*p[io]ng(\\(\\))?")){
            out.println("Pong");
            out.flush();
            con.close();
            return false;
        }
        return true;
    }


    static void checaCPF(String txt,PrintStream out, Socket con){
        var pt = Pattern.compile("(\\d{3})\\.?(\\d{3})\\.?(\\d{3})-?(\\d{2})");
        var mr = pt.matcher(txt);

        if(mr.matches()){
            var numeros = new int[11];
            for(int i =0; i<3; i++){
                for(int j=0; j<3; j++){
                    numeros[(i * 3) + j] = Integer.parseInt("" + mr.group(i+1).charAt(j));
                }
            }
            numeros[9] = Integer.parseInt("" + mr.group(4).charAt(0));
            numeros[10] = Integer.parseInt("" + mr.group(4).charAt(1));

            var soma = 0;
            for(int n = 0; n < 8; n++){
                if(numeros[n] != numeros[n+1]){
                    ++soma;
                }
            }
            if(soma ==0){
                out.println(msg(MENSAGEM_ERRO_NUMERO_REPETIDO));
                out.flush();
                return;
            }

            soma = 0;
            for(int n = 0; n < 9; n++){
                soma += numeros[n] * (10-n);
            }
            var resto = soma % 11;
            var d10 = 0;
            if(resto < 2 ){
                if(numeros[9] !=0) {
                    out.println(msg(MENSAGEM_ERRO_VALIDACAO));
                    out.flush();
                    return;
                }
            }else{
                d10 = (11-resto);
                if(numeros[9] != d10){
                    out.println(msg(MENSAGEM_ERRO_VALIDACAO));
                    out.flush();
                    return;
                }
            }

            soma = d10 * 2;
            for(int n = 0; n < 9; n++){
                soma += numeros[n] * (11 - n);
            }

            resto = soma % 11;

            if(resto < 2){
                if(numeros[10] !=0) {
                    out.println(msg(MENSAGEM_ERRO_VALIDACAO));
                    out.flush();
                    return;
                }
            }else{
                var d11 = (11-resto);
                if(numeros[10] != d11){
                    out.println(msg(MENSAGEM_ERRO_VALIDACAO));
                    out.flush();
                    return;
                }
            }


            System.out.println(Arrays.toString(numeros));
            out.println("Este CPF é válido");
            out.flush();
        }else{
            out.println(msg(MENSAGEM_ERRO_PADRAO));
            out.flush();
        }

    }
    static void trataConexao(Socket con) throws IOException {
        var in = new Scanner(con.getInputStream());
        var out = new PrintStream(con.getOutputStream());
        var aux = "";

        while(true){
            while (in.hasNextLine()){
                aux = in.nextLine();
                System.out.printf("recebendo %s\n",aux);

                if(checaComandos(aux, out, con)){
                    checaCPF(aux, out, con);
                }
            }
        }
    }

    static void main() throws IOException {

        try(var server = new ServerSocket();) {
            ss = server;
            ss.bind(new InetSocketAddress("127.0.0.1", 8012));
            try (var tp = Executors.newCachedThreadPool();) {
                System.out.println("inicializando servidor");
                while (true) {
                    var con = ss.accept();
                    tp.submit(() -> {
                        System.out.println("NOVA CONEXÃO!!!!");
                                try {
                                    trataConexao(con);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                }
            }
        }

    }
}
