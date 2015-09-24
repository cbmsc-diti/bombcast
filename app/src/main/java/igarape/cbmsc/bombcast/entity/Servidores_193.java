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
        servidores.add(new Servidores_193("Florianopolis", "fpolis.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Criciuma", "cua.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Tubarao", "tro.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Lages", "lgs.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Chapeco", "cco.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Itajai", "iai.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Rio do Sul", "rsl.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Canoinhas", "cni.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Sao Miguel do Oeste", "sge.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Brusque", "bqe.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Blumenau", "bnu.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Balneario Camboriu", "bcu.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Curitibanos", "cbs.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Joaçaba", "jca.cbm.sc.gov.br"));
        servidores.add(new Servidores_193("Servidor de testes", "10.193.4.55"));
       // servidores.add(new Servidores_193("Servidor de apresentação", "localhost"));
        return servidores;
    }
}
