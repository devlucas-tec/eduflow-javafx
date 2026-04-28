package br.edu.ifpb.esperanca.eduflow;

import org.mindrot.jbcrypt.BCrypt;


// criei essa classe apenas como um meio de "Mockar" logins de adm, monitor e professor
// essa classe nada mais é do que um gerador de hash para poder fazer inserts nno banco.
public class GerarHash {
    public static void main(String[] args) {
        String senha = "monitor"; // escolha a senha que quiser
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        System.out.println(hash);
    }
}