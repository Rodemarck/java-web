package local.redes;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.Executors;


// @author Rodemarck Melo dos Santos Júnior
public class Cliente extends JFrame {
    private JTextField tfNome;
    private JTextField tfIdade;
    private JTextArea taResposta;
    private JButton btnEnviar;


    private Socket socket;
    private InputStream in;
    private ObjectOutputStream out;

    private StringBuilder buffer = new StringBuilder();


    public Cliente() {
        setSize(800, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblNome = new JLabel("Nome", SwingConstants.LEFT);
        JLabel lblIdade = new JLabel("Idade", SwingConstants.LEFT);
        JLabel lblResposta = new JLabel("Retorno do Servidor", SwingConstants.LEFT);


        tfNome = new JTextField(25);
        tfIdade = new JTextField(15);
        taResposta = new JTextArea(25, 40);
        btnEnviar = new JButton("Enviar");



        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));

        JPanel painelNome = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelNome.add(new JLabel("Nome:"));
        painelNome.add(tfNome);

        JPanel painelIdade = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelIdade.add(new JLabel("Idade:"));
        painelIdade.add(tfIdade);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotao.add(btnEnviar);

        painelFormulario.add(painelNome);
        painelFormulario.add(painelIdade);
        painelFormulario.add(painelBotao);

        JPanel painelResposta = new JPanel(new BorderLayout());
        painelResposta.add(new JLabel("Retorno do Servidor:"), BorderLayout.NORTH);
        painelResposta.add(new JScrollPane(taResposta), BorderLayout.CENTER);

        add(painelFormulario, BorderLayout.NORTH);
        add(painelResposta, BorderLayout.CENTER);
        setTitle("Cliente");

        conectar();

        btnEnviar.addActionListener(e -> enviar());

        Timer timer = new Timer(100, e -> receber());
        timer.start();

        setVisible(true);

        System.out.println("aaaaa");
    }


    private void enviar() {
        try {
            System.out.println("enviando....");
            String nome = tfNome.getText();
            int idade = Integer.parseInt(tfIdade.getText());

            Pessoa pessoa = new Pessoa(nome, idade);
            System.out.println(pessoa);
            out.writeObject(pessoa);
            out.flush();

            System.out.println("fluh...");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Idade inválida",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void receber() {
        try {

            int quantidade = in.available();

            if (quantidade == 0) {
                return;
            }

            byte[] dados = new byte[quantidade];

            in.read(dados);

            buffer.append(new String(dados));

            int fimLinha;

            while ((fimLinha = buffer.indexOf("\n")) != -1) {

                String mensagem = buffer
                        .substring(0, fimLinha)
                        .trim();

                buffer.delete(0, fimLinha + 1);

                taResposta.append(
                        "Recebeu do Servidor:\n"
                                + mensagem
                                + "\n"
                );
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void conectar() {
        try {
            socket = new Socket(Variaveis.URL, Variaveis.PORTA);

            in = socket.getInputStream();
            out = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível conectar ao servidor.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            System.exit(1);
        }
    }


    static void main() throws IOException {
        System.out.println("loggando");
        SwingUtilities.invokeLater(Cliente::new);

    }
}
