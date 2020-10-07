import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/*
 * Classname             User
 *
 * Version information   v1
 *
 * Date                  07/09/2020 16:38
 *
 * author                Marco Aurélio de Noronha Santos
 */

public class User implements Registro{
    private int idUser;
    protected String nome;
    protected String email;
    private String senha;

    public User(int i, String n, String e, String s) {
        idUser = i;
        nome = n;
        email = e;
        senha = s;
    }

    public User(String n, String e, String s) {
        idUser = -1;
        nome = n;
        email = e;
        senha = s;
    }

    public User() {
        idUser = -1;
        nome = "";
        email = "";
        senha = "";
    }

    public String toString() {
        return "\nNome: " + nome + "\nEmail: " + email;
    }

    public void setSenha(String s){
        this.senha = s;
    }

    public String getSenha(){
        return this.senha;
    }

    public int getID(){
        return idUser;
    }

    public void setID(int id){
        idUser = id;
    }


    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idUser);
        dos.writeUTF(nome);
        dos.writeUTF(email);
        dos.writeUTF(senha);

        return baos.toByteArray();

    }

    public void fromByteArray(byte[] ba) throws IOException{    
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idUser = dis.readInt();
        nome = dis.readUTF();
        email = dis.readUTF();
        senha = dis.readUTF();

    }

    public String chaveSecundaria(){
        return this.email;
    }

    public int getUserID(){
        return idUser;
    }

}