import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * Classname             Resposta
 *
 * Version information   v1
 *
 * Date                  14/10/2020 12:08
 *
 * author                Marco Aurélio de Noronha Santos
 */

public class Resposta implements Registro{
    private int idResposta;
    private int idPergunta;
    private int idUser;
    protected long criacao;
    protected long alteracao;
    protected short nota;
    protected String resposta;
    protected boolean ativa;

    public Resposta(int _idPergunta, int _idUser, String _resposta) {
        Date date = new Date();

        idResposta = -1;
        idPergunta = _idPergunta;
        idUser = _idUser;
        criacao = date.getTime();
        alteracao = criacao;
        nota = 0;
        resposta = _resposta;
        ativa = true;
    }

    public Resposta() {
        idResposta = -1;
        idPergunta = -1;
        idUser = -1;
        criacao = -1;
        alteracao = -1;
        nota = 0;
        resposta = "";
        ativa = false;
    }
    
    public String toString(){       
         
        SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String temp = "Nota: " + nota;

        if(!ativa){
            temp += " | (ARQUIVADA)";
        }

        temp += "\n" + resposta + "\n" + formatador.format(this.criacao);

        if(criacao < alteracao){
            temp += " - Editada em "+formatador.format(this.alteracao);
        }

        return temp;
    }

    public int getID(){
        return idResposta;
    }
    
    public void setID(int id){
        idResposta = id;
    }

    public int getPerguntaID(){
        return idPergunta;
    }

    public int getUserID(){
        return idUser;
    }

    public void setNota(short nota){
        this.nota = nota;
    }

    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idResposta);
        dos.writeInt(idPergunta);
        dos.writeInt(idUser);
        dos.writeLong(criacao);
        dos.writeLong(alteracao);
        dos.writeShort(nota);
        dos.writeUTF(resposta);
        dos.writeBoolean(ativa);
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException{    
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idResposta = dis.readInt();
        idPergunta = dis.readInt();
        idUser = dis.readInt();
        criacao = dis.readLong();
        alteracao = dis.readLong();
        nota = dis.readShort();
        resposta = dis.readUTF();
        ativa = dis.readBoolean();
    }

    public String chaveSecundaria(){
        return null;
    }

}