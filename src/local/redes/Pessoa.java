package local.redes;

import java.io.Serializable;

// @author Rodemarck Melo dos Santos Júnior
public class Pessoa implements Serializable {
    String nome;
    int idade;

    public Pessoa(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "nome='" + nome + '\'' +
                ", idade=" + idade +
                '}';
    }
}
