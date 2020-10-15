import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * Classname             Pergunta
 *
 * Version information   v1
 *
 * Date                  13/09/2020 14:10
 *
 * author                Marco Aurélio de Noronha Santos
 */

public class Pergunta implements Registro{
    private int idPergunta;
    private int idUser;
    protected long criacao;
    protected long alteracao;
    protected short nota;
    protected String pergunta;
    protected boolean ativa;
    protected String termosChave;

    public Pergunta(int _idUser, String _pergunta, String _termosChave) {
        Date date = new Date();

        idPergunta = -1;
        idUser = _idUser;
        criacao = date.getTime();
        alteracao = criacao;
        nota = 0;
        pergunta = _pergunta;
        ativa = true;
        termosChave = _termosChave;
    }

    public Pergunta() {
        idPergunta = -1;
        idUser = -1;
        criacao = -1;
        alteracao = -1;
        nota = 0;
        pergunta = "";
        ativa = false;
        termosChave = "";
    }
    
    public String toString(){       
         
        SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String temp = "Nota: " + nota;

        if(!ativa){
            temp += " | (ARQUIVADA)";
        }

        temp += "\n" + pergunta;
        
        temp += "\nTermos Chave: [";
        String[] splitted = termosChave.split(";");
        for(int i = 0; i < splitted.length; i++){
            temp += splitted[i]+"]";
            if(i+1 < splitted.length){
                temp += " [";
            }
        }

        temp += "\n" + formatador.format(this.criacao);
        if(criacao < alteracao){
            temp += " - Editada em "+formatador.format(this.alteracao);
        }


        return temp;
    }

    public String toString(String nomeUser){       
         
        SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String temp = "";

        if(!ativa){
            temp += "\t(ARQUIVADA)";
        }

        temp += "\n============================================\n";
        temp += "\t" + pergunta; 
        temp += "\n============================================\n";

        
        temp += formatador.format(this.criacao);

        if(criacao < alteracao){
            temp += " - Editada em "+formatador.format(this.alteracao);
        }

        temp += " por "+nomeUser;

        temp += "\nTermos Chave: [";
        String[] splitted = termosChave.split(";");
        for(int i = 0; i < splitted.length; i++){
            temp += splitted[i]+"]";
            if(i+1 < splitted.length){
                temp += " [";
            }
        }

        temp += "\nNota: " + nota;

        return temp;
    }

    public int getID(){
        return idPergunta;
    }

    public int getUserID(){
        return idUser;
    }

    public void setID(int id){
        idPergunta = id;
    }

    public void setNota(short nota){
        this.nota = nota;
    }

    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idPergunta);
        dos.writeInt(idUser);
        dos.writeLong(criacao);
        dos.writeLong(alteracao);
        dos.writeShort(nota);
        dos.writeUTF(pergunta);
        dos.writeBoolean(ativa);
        dos.writeUTF(termosChave);
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException{    
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idPergunta = dis.readInt();
        idUser = dis.readInt();
        criacao = dis.readLong();
        alteracao = dis.readLong();
        nota = dis.readShort();
        pergunta = dis.readUTF();
        ativa = dis.readBoolean();
        termosChave = dis.readUTF();
    }

    public String chaveSecundaria(){
        return null;
    }

}