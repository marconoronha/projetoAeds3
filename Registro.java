import java.io.IOException;

/*
 * Interface             Registro (Crud Indexado)
 *
 * Version information   v1
 *
 * Date                  07/09/2020 16:38
 *
 * author                Marco Aurélio de Noronha Santos
 */

public interface Registro {
    public int getID();
    public void setID(int n);
    public byte[] toByteArray() throws IOException;
    public void fromByteArray(byte[] ba) throws IOException;
    public String chaveSecundaria();
    public int getUserID();
}