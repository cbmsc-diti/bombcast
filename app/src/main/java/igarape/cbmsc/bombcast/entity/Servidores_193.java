package igarape.cbmsc.bombcast.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barcellos on 26/02/15.
 */
public class Servidores_193 {

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private String nome;
    private String ip;


    public Servidores_193(String nome, String ip) {
        this.nome = nome;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return getNome();
    }

    public static List<Servidores_193> listaServidores() {
        List<Servidores_193> servidores = new ArrayList<Servidores_193>();
        servidores.add(new Servidores_193("Florianopolis", "10.193.4.55"));
        servidores.add(new Servidores_193("Criciuma", "10.194.1.121"));
        servidores.add(new Servidores_193("Tubarao", "10.194.2.251"));
        servidores.add(new Servidores_193("Lages", "10.194.62.251"));
        servidores.add(new Servidores_193("Chapeco", "10.194.24.251"));
        servidores.add(new Servidores_193("Itajai", "10.194.51.251"));
        servidores.add(new Servidores_193("Rio do Sul", "10.194.89.251"));
        servidores.add(new Servidores_193("Canoinhas", "10.194.19.251"));
        servidores.add(new Servidores_193("Sao Miguel do Oeste", "10.194.103.251"));
        servidores.add(new Servidores_193("Brusque", "10.194.14.251"));
        servidores.add(new Servidores_193("Blumenau", "10.194.1.253"));
        servidores.add(new Servidores_193("Balneario Camboriu", "10.194.5.251"));
        servidores.add(new Servidores_193("Curitibanos", "10.194.30.251"));
        servidores.add(new Servidores_193("Joaçaba", "10.193.4.110"));
        servidores.add(new Servidores_193("Servidor de testes", ""));
        return servidores;
    }
}
