import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.io.*;

/*
 * Classname             CRUD Indexado
 *
 * Version information   v1.1
 *
 * Date                  07/09/2020 16:38
 *
 * author                Marco Aurélio de Noronha Santos
 * 
 * changes               -O arquivo de dados agora é aberto de maneira global na classe CRUD
 */

public class CRUD<T extends Registro> {
    protected Constructor<T> construtor;
    protected String fileName;
    private HashExtensivel he;
    private ArvoreBMais_String_Int ab1;
    private RandomAccessFile arq;

    public CRUD(Constructor<T> construtor, String fn) throws Exception {
        String diretorio;
        String cestos;
        String arvoreB1;            
      
        this.construtor = construtor;
        this.fileName = fn;

        //Nomeia os arquivos índices
        diretorio = fn;
        String[] temp = fn.split("\\.");
        diretorio = temp[0] + ".diretorio.idx";
        cestos = temp[0] + ".cestos.idx";
        arvoreB1 = temp[0] + ".arvore1.idx";

        //Abre o arquivo de dados 
        arq = new RandomAccessFile(fileName, "rw");
        
        if(arq.length() == 0){//Se o arquivo não existir
            arq.writeInt(0); //Inicia o cabeçalho
            new File(diretorio).delete(); // apaga o índice anterior, caso já exista um
            new File(cestos).delete(); // apaga o índice anterior, caso já exista um
        }
        
        //Inicia as estruturas usadas nos arquivos índices
        he = new HashExtensivel(10, diretorio, cestos);        
        ab1 = new ArvoreBMais_String_Int(10, arvoreB1);

    }

    public int create(T obj) throws Exception {
        // Lê o cabeçalho e converte o obj recebido para vetor de bytes.
        arq.seek(0);
        int id = arq.readInt() + 1;
        String chaveSecundaria = obj.chaveSecundaria();
        obj.setID(id);
        byte[] ba = obj.toByteArray();

        // Avança o ponteiro ate a última posição do arquivo e cria o registro.
        arq.seek(arq.length());
        long pos = arq.getFilePointer();
        arq.writeByte(' ');
        arq.writeInt(ba.length);
        arq.write(ba);

        // Salva a nova última posição do arquivo e atualiza o cabeçalho.
        arq.seek(0);
        arq.writeInt(id);

        he.create(id, pos);
        if(chaveSecundaria != null){
            ab1.create(chaveSecundaria, id);
        }

        return id;
    }

    public T read(int id) throws Exception {
        Long pos = he.read(id);  
        T obj = this.construtor.newInstance();

        //Acessa a posição retornada pelo índice direto
        boolean found = false;

        //Lê o registro
        if(pos >= 0){
            arq.seek(pos);

            int tamR;
            byte[] ba;

            if (arq.readByte() != '*') {
                tamR = arq.readInt();
                ba = new byte[tamR];
                arq.read(ba);
                obj.fromByteArray(ba);
                if (id == obj.getID()) {
                    found = true;
                }
            }
        }

        if (!found) {
            obj = null;
        }

        return obj;
    }

    public T read(String chave) throws Exception {
        int id = ab1.read(chave);
        return this.read(id);        
    }

    public boolean update(T obj) throws Exception {

        long pos;
        T obj2 = this.read(obj.getID()); 
        byte[] ba, ba2;
        boolean ok = false;        
        
        //Compara os tamanhos dos objetos (existente e atualizado)
        if (obj2 != null) {
            pos = he.read(obj.getID());
            arq.seek(pos);

            ba = obj.toByteArray();
            ba2 = obj2.toByteArray();

            if (ba2.length >= ba.length) {//Sobrescreve o arquivo existente
                arq.seek(arq.getFilePointer() + 5);
                arq.write(ba);

            } else {//Exclui o registro existente e cria um novo no final do arquivo
                arq.writeByte('*');
                arq.seek(arq.length());
                pos = arq.getFilePointer();
                arq.writeByte(' ');
                arq.writeInt(ba.length);
                arq.write(ba);
                he.update(obj.getID(), pos);
            }

            //Atualiza o índice indireto
            if(obj.chaveSecundaria()!= null && obj.chaveSecundaria().compareTo(obj2.chaveSecundaria()) != 0){
                ab1.update(obj.chaveSecundaria(), obj.getID());
            }

            ok = true;
        }

        return ok;
    }

    public boolean delete(int id) throws Exception {

        long pos = he.read(id);
        
        int tamR;
        T obj = construtor.newInstance();
        byte[] ba;
        boolean ok = false;

        //Acessa a posição retornada pelo índice direto
        arq.seek(pos);
        
        //Lê o registro
        if (arq.readByte() != '*') {
            tamR = arq.readInt();
            ba = new byte[tamR];
            arq.read(ba);
            obj.fromByteArray(ba);
            if (id == obj.getID()) {
                ok = true;
                arq.seek(pos);
                arq.writeByte('*');
                ab1.delete(obj.chaveSecundaria());
                he.delete(id);
            }
        }

        return ok;
    }
}