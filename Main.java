import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.io.*;

public class Main{
    private static CRUD<User> arqUser;
    private static CRUD<Pergunta> arqPerguntas;
    private static CRUD<Resposta> arqRespostas;
    private static int idUsuarioAtual;
    public static Scanner leitor = new Scanner(System.in);
    private static ArvoreBMais_Int_Int arvoreUP;
    private static ArvoreBMais_Int_Int arvorePR;
    private static ArvoreBMais_Int_Int arvoreUR;
    private static ListaInvertida listaInvertida;

    public static void main(String[] args) throws IOException, InterruptedException {
        int opcao;

        //new File("dados/users.db").delete(); // apaga o arquivo anterior, caso já exista um (Usado em testes apenas)

        try {

            arqUser = new CRUD<>(User.class.getConstructor(), "dados/users.db");
            arqPerguntas = new CRUD<>(Pergunta.class.getConstructor(), "dados/perguntas.db");
            arqRespostas = new CRUD<>(Resposta.class.getConstructor(), "dados/respostas.db");
            
            arvoreUP = new ArvoreBMais_Int_Int(10, "dados/arvRelacaoUserPerg.idx");
            arvorePR = new ArvoreBMais_Int_Int(10, "dados/arvRelacaoPergResp.idx");
            arvoreUR = new ArvoreBMais_Int_Int(10, "dados/arvRelacaoUserResp.idx");
            
            listaInvertida = new ListaInvertida(5, "dados/dicionario", "dados/dicBlocos");

            do {
                //Tela inicial
                opcao = telaDeLogin();

                switch (opcao) {
                    case 1: {
                        //Faz login                
                        fazLogin();                        
                    }break;

                    case 2: {
                        //cria nova conta                        
                        criaConta();
                    }break;

                    case 0: break;

                    default: System.out.println("Opção inválida");
                }            
            
            }while(opcao != 0);

            leitor.close();
        
        }catch (Exception e){
            e.printStackTrace();
        }
    
    }

    public static int telaDeLogin(){
        int opcao;
        
        System.out.println("\n\n-------------------------------");
        System.out.println("        TELA DE LOGIN");
        System.out.println("-------------------------------");
        System.out.println("1 - Entrar");
        System.out.println("2 - Criar conta (primeiro acesso)");
        System.out.println("0 - Sair");
        System.out.print("\nOpção: ");
        
        try {
            opcao = Integer.valueOf(leitor.nextLine());
        } catch(NumberFormatException e) {
            opcao = -1;
        }

        return opcao;
    }

    public static void fazLogin() throws Exception {
        User userLogin = new User();
        int i;
        boolean emailCorreto = false;
        int opcao = -1;
        String emailDigitado, senhaDigitada;
        Console con = System.console();

        System.out.println("\n-------------------------------");
        System.out.println("            Entrar");
        System.out.println("-------------------------------");

        do {
            System.out.print("\nEmail: ");
            emailDigitado = leitor.nextLine();

            if(emailDigitado.length() > 0){
                if (arqUser.read(emailDigitado) == null) {//Verifica no arquivo db se o email digitado existe
                    System.out.println("\nEmail não encontrado!\n");
                    Thread.sleep(1500);
                    System.out.println("Tentar novamente?\n");
                    opcao = confirmar();

                } else {
                    userLogin = arqUser.read(emailDigitado);
                    emailCorreto = true;
                }
            }else{
                opcao = 2;
            }

        } while (opcao == 1 && !emailCorreto);

        if (emailCorreto){

            for (i = 0; i < 3; i++) {//3 tentativas para acertar a senha
                System.out.print("Senha: ");
                senhaDigitada = new String(con.readPassword());//recebe a senha de maneira oculta
                con.flush();

                if (userLogin.getSenha().equals(senhaDigitada)) {//autentica a senha digitada
                    System.out.println("Login bem sucedido! ");
                    Thread.sleep(1500);
                    idUsuarioAtual = arqUser.read(emailDigitado).getID();//guarda o id do usuario logado no momento (global)                                    
                    menuPrincipal();
                    i = 4;
                }else{
                    System.out.println("Senha incorreta! Tentativas: "+ (i+1) +" de 3");
                }
            }

            if(i == 3){
                System.out.println("Falha no login!");
                Thread.sleep(1500);
            }

        }else{
            System.out.println("Login cancelado");
            Thread.sleep(1500);
        }
    }

    public static void criaConta() throws Exception{
        int opcao = 0;
        String emailDigitado, nomeDigitado, senhaDigitada, senhaConfirmada;
        boolean emailValido = false;
        Console con = System.console();

        System.out.println("\n-------------------------------");
        System.out.println("       Criar nova conta");
        System.out.println("-------------------------------");

        do{

            System.out.print("\nDigite um email válido: ");
            emailDigitado = leitor.nextLine();

            if(emailDigitado.length() > 0){
                if(arqUser.read(emailDigitado) != null){//verifica se é um email não utilizado
                    System.out.println("O email digitado já está sendo utilizado!\nTentar novamente?");
                    
                    opcao = confirmar();
                }
                else{
                    emailValido = true;
                }
            }
            else{
                opcao = 2;
            }

        }while(opcao == 1 && !emailValido);

        if(emailValido){ 

            System.out.print("Digite seu nome: ");
            nomeDigitado = leitor.nextLine();

            //cria a senha
            do{
                do{
                    System.out.print("Crie uma senha (tamanho mínimo: 6 dígitos): ");
                    senhaDigitada = new String(con.readPassword());
                    con.flush();
                    if(senhaDigitada.length() < 6){
                        System.out.println("A senha deve ter no mínimo 6 dígitos!");
                        Thread.sleep(1500);
                    }
                }while(senhaDigitada.length() < 6);

                System.out.print("Confime a senha: ");
                senhaConfirmada = new String(con.readPassword());
                con.flush();

                if(!senhaDigitada.equals(senhaConfirmada)){
                    System.out.println("As senhas digitadas estão diferentes!");
                }

            }while(!senhaDigitada.equals(senhaConfirmada) );

            //cria obj User com os dados passados
            User novoUser = new User(nomeDigitado, emailDigitado, senhaDigitada);

            //confirma a criação
            System.out.print("\nNovo usuário: ");
            System.out.println(novoUser.toString());
            System.out.println("\nConfirmar?");

            opcao = confirmar();

            if(opcao == 1){
                arqUser.create(novoUser);
                System.out.println("Usuário criado com sucesso!");
                Thread.sleep(1500);
            }else{
                System.out.println("Criação de usuário cancelada!");
                Thread.sleep(1500);
                opcao = -1;
            }
            
        }else{
            System.out.println("Criação de usuário cancelada!");
            Thread.sleep(1500);
        }
    }

    //======================================Metodo Menu Principal==============================================//

    private static void menuPrincipal(){
        int opcao;

        try{

            do{
                System.out.println("\n\n-------------------------------");
                System.out.println("        Menu Principal");
                System.out.println("-------------------------------");
                System.out.println("1 - Minhas Perguntas");
                System.out.println("2 - Consultar Perguntas");
                System.out.println("3 - Notificações: ");
                System.out.println("0 - Sair");
                System.out.print("\nOpção: ");
                
                try {
                    opcao = Integer.valueOf(leitor.nextLine());
                } catch(NumberFormatException e) {
                    opcao = -1;
                }

                switch(opcao){
                    case 1:{
                        minhasPerguntas();
                    }break;

                    case 2: {
                        consulta();
                    }break;

                    case 3: {
                        System.out.println("Não há nada aqui ainda (Não implementado)");
                    }break;

                    case 0: break;

                    default: {
                        System.out.println("Opção inválida!");
                        Thread.sleep(1500);
                    }
                }
                
            }while(opcao != 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //======================================Metodos Minhas Perguntas==============================================//

    private static void minhasPerguntas() throws Exception {
        int opcao; 

        try{
            do{   
                System.out.println("\n\n-------------------------------");
                System.out.println("        Menu Principal > Minhas Perguntas");
                System.out.println("-------------------------------");
                System.out.println("1 - Listar");
                System.out.println("2 - Incluir");
                System.out.println("3 - Alterar");
                System.out.println("4 - Arquivar");
                System.out.println("0 - Retornar");
                System.out.print("\nOpção: ");

                try {
                    opcao = Integer.valueOf(leitor.nextLine());
                } catch(NumberFormatException e) {
                    opcao = -1;
                }

                switch(opcao){

                    case 1:{//Listar

                        System.out.println("\n\n-------------------------------");
                        System.out.println("        Menu Principal > Minhas Perguntas > Listar");
                        System.out.println("-------------------------------");


                        listaPerguntas(arvoreUP.read(idUsuarioAtual));

                        System.out.println("\nPressione qualquer tecla para voltar");
                        leitor.nextLine();
                                                
                    }break;
                        
                    case 2: {//Incluir

                        System.out.println("\n\n-------------------------------");
                        System.out.println("        Menu Principal > Minhas Perguntas > Incluir");
                        System.out.println("-------------------------------");

                        System.out.print("Digite sua pergunta: ");
                        String novaPergunta = leitor.nextLine();
                        System.out.println("\nDigite termos chave da pergunta separados por ponto e vírgula.");
                        System.out.print("Ex: política;eleições 2020;Brasil\n\nTermos Chave: ");
                        String termosChave = leitor.nextLine();


                        if(novaPergunta.length() > 0){

                            System.out.println("\nConfirmar?");
                            opcao = confirmar();

                            if(opcao == 1){
                                termosChave = converteChaves(termosChave);
                                Pergunta novaPergunta_obj = new Pergunta(idUsuarioAtual, novaPergunta, termosChave);
                                arqPerguntas.create(novaPergunta_obj);
                                arvoreUP.create(idUsuarioAtual, novaPergunta_obj.getID());
                                String[] tmp = termosChave.split(";");
                                adicionaTermosChaves(tmp, novaPergunta_obj.getID());                                

                                System.out.println("Pergunta criada!");
                                
                            }else{
                                System.out.println("Criação cancelada!");
                                
                            }
                            
                        }else{
                            System.out.println("Criação cancelada!");
                            
                        }

                        Thread.sleep(1500);
                    }break;

                    case 3: {//Alterar
                        System.out.println("\n\n-------------------------------");
                        System.out.println("        Menu Principal > Minhas Perguntas > Alterar");
                        System.out.println("-------------------------------");
                        System.out.println("*Perguntas arquivadas não estão sendo exibidas");

                        Pergunta temp;

                        int [] arrayPerguntas = arvoreUP.read(idUsuarioAtual);
                        arrayPerguntas = retiraArquivadas(arrayPerguntas);
                        listaPerguntas(arrayPerguntas);

                        do{
                            System.out.print("\nDigite o número da pergunta que será alterada (0 para sair): ");

                            try {
                                opcao = Integer.valueOf(leitor.nextLine());                                
                            } catch(NumberFormatException e) {
                                System.out.println("Opção inválida!");
                                opcao = -1;
                            }

                            if(opcao <= arrayPerguntas.length && opcao >= 1){

                                temp = arqPerguntas.read(arrayPerguntas[(opcao-1)]);
                                System.out.print("Digite a alteração que deseja fazer: ");                                
                                String perguntaEditada = leitor.nextLine();
                                temp.pergunta = perguntaEditada;
                                                                
                                System.out.print("Digite os termos chaves: ");
                                String termosEditados = leitor.nextLine();
                                termosEditados = converteChaves(termosEditados);
                                atualizaTermosChave(temp.termosChave, termosEditados, temp.getID());
                                
                                temp.termosChave = termosEditados;
                                
                                Date date = new Date();
                                temp.alteracao = date.getTime();
                                
                                arqPerguntas.update(temp);
                                
                                System.out.println("Alteração realizada com sucesso!");
                                Thread.sleep(1500);
                            }else{

                                if(opcao != 0){
                                    System.out.println("Opção inválida!");
                                }                                                          
                            }
                            
                        }while(opcao < 0 || opcao > arrayPerguntas.length);

                        if(opcao != 0){
                            
                        }                        

                        opcao = -1;
                    }break;

                    case 4: {//Arquivar
                        System.out.println("\n\n-------------------------------");
                        System.out.println("        Menu Principal > Minhas Perguntas > Arquivar");
                        System.out.println("-------------------------------");
                        System.out.println("*Perguntas já arquivadas não estão sendo exibidas");

                        Pergunta temp;

                        int [] arrayPerguntas = arvoreUP.read(idUsuarioAtual);
                        arrayPerguntas = retiraArquivadas(arrayPerguntas);
                        listaPerguntas(arrayPerguntas);

                        do{
                            System.out.print("\nDigite o número da pergunta que será arquivada (0 para sair): ");

                            try {
                                opcao = Integer.valueOf(leitor.nextLine());                                
                            } catch(NumberFormatException e) {
                                System.out.println("Opção inválida!");
                                opcao = -1;
                            }

                            if(opcao <= arrayPerguntas.length && opcao >= 1){
                                
                                temp = arqPerguntas.read(arrayPerguntas[(opcao-1)]);                                
                                temp = arqPerguntas.read(arrayPerguntas[(opcao-1)]);
                                System.out.println("\nConfirmar?");
                                opcao = confirmar();

                                if(opcao == 1){                            
                                    Date date = new Date();
                                    temp.ativa = false;
                                    temp.alteracao = date.getTime();
                                    arqPerguntas.update(temp);
                                    System.out.println("Pergunta arquivada!");
                                }else{
                                    System.out.println("Arquivamento cancelado!");
                                
                                }
                                Thread.sleep(1500);

                            }else{
                                if(opcao != 0){
                                    System.out.println("Opção inválida!");
                                }                                                      
                            }
                            

                        }while(opcao < 0 || opcao > arrayPerguntas.length);
                        opcao = -1;

                    }break;

                    case 0: break;

                    default: {
                        System.out.println("Opção inválida!");
                        Thread.sleep(1500);
                    }
                }

            }while(opcao != 0);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //======================================Metodos Consultar==========================================//

    public static void consulta() throws Exception {
        int opcao;
        Pergunta temp;

        System.out.println("\n\n-------------------------------");
        System.out.println("        Menu Principal > Minhas Perguntas > Consultar Perguntas");
        System.out.println("-------------------------------");
        System.out.println("Busque as perguntas por palavra chave separadas por ponto e vírgula");
        System.out.println("Ex: política;eleições 2020;Brasil");
        System.out.print("\nBuscar: ");

        String termosDigitados = leitor.nextLine();

        termosDigitados = converteChaves(termosDigitados);
        int[] arrayPerguntas = procuraTermosChaves(termosDigitados);

        arrayPerguntas = ordenaNotas(arrayPerguntas);
        
        int qntdResultados = listaPerguntas(arrayPerguntas);
        System.out.println("\n"+qntdResultados+" resultados");

        if(qntdResultados > 0){
            do{
                System.out.print("\nDigite o número da pergunta que deseja consultar (0 para sair): ");

                try {
                    opcao = Integer.valueOf(leitor.nextLine());                                
                } catch(NumberFormatException e) {
                    System.out.println("Opção inválida!");
                    opcao = -1;
                }

                if(opcao <= arrayPerguntas.length && opcao >= 1){
                    
                    temp = arqPerguntas.read(arrayPerguntas[(opcao-1)]);                                
                    exibePerguntaCompleta(temp);
                }else{

                    if(opcao != 0){
                        System.out.println("Opção inválida!");
                    }                                                      
                }            

            }while(opcao < 0 || opcao > arrayPerguntas.length);
        }else{
            Thread.sleep(1000);
        }
                
    }

    public static int[] procuraTermosChaves(String termosChave) throws Exception {
        String tmp[] = termosChave.split(";");
        HashSet<Integer> arrset1 = new HashSet<Integer>();
        HashSet<Integer> arrset2 = new HashSet<Integer>();
        int a1[] = listaInvertida.read(tmp[0]);
        int a2[];

        for(int i = 0; i < a1.length; i++){
            arrset1.add(a1[i]);
        }

        for(int i = 1; i < tmp.length; i++){
            a2 = listaInvertida.read(tmp[i]);
            for(int j = 0; j < a2.length; j++){
                arrset2.add(a2[j]);
            }
            arrset1.retainAll(arrset2);
        }

        int[] conjResposta = new int[arrset1.size()];
        
        int k = 0;

        for(int i : arrset1){
            conjResposta[k++] = i;
        }

        return conjResposta;
    }

    public static void exibePerguntaCompleta(Pergunta p) throws Exception {
        User autor = arqUser.read(p.getUserID());
        int opcao;
        
        do{
            System.out.println("\n"+p.toString(autor.nome));
            System.out.println("\n______________Comentários______________\n");

            System.out.println("\n______________Respostas______________\n");
            int[] arrayIdRespostas = arvorePR.read(p.getID());
            listaRespostas(arrayIdRespostas);
        
            System.out.println("\n\n1 - Responder");
            System.out.println("2 - Comentar");
            System.out.println("3 - Avaliar");
            System.out.println("\n0 - Retornar");

            System.out.print("\nOpção: ");
            
            try {
                opcao = Integer.valueOf(leitor.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch(opcao){
                case 1:{
                    respostas(p);                    
                }break;

                case 2:{
                    System.out.println("2 - Comentar (não implementado)");                    
                }break;

                case 3:{
                    System.out.println("3 - Avaliar (não implementado)");                    
                }break;

                case 0: break;

                default: {
                    System.out.println("Opção Inválida!");
                    Thread.sleep(1500);
                }
            }
        }while(opcao != 0);

    }

    public static int[] ordenaNotas(int[] arrayPerguntas) throws Exception {
        int[] ordenado = new int[arrayPerguntas.length];
        Pergunta[] aux = new Pergunta[arrayPerguntas.length];
        for(int i = 0; i < arrayPerguntas.length; i++){
            aux[i] = arqPerguntas.read(arrayPerguntas[i]);
        }

        quicksort(aux, 0, aux.length-1);

        for(int i = 0; i < aux.length; i++){
            ordenado[i] = aux[i].getID();
        }

        return ordenado;
    }

    private static void quicksort(Pergunta[] dados, int inicio, int fim){
        if(inicio < fim){
            int posPivo = _quicksort(dados, inicio, fim);
            quicksort(dados, inicio, posPivo - 1);
            quicksort(dados, posPivo+1, fim);
        }
    }

    private static int _quicksort(Pergunta[] dados, int inicio, int fim){
        Pergunta pivo = dados[inicio];
        int i = inicio+1, f = fim;
        while(i <= f){
            if(dados[i].nota > pivo.nota){
                i++;
            }else{
                if(pivo.nota >= dados[f].nota){
                    f--;
                }else{
                    Pergunta troca = dados[i];
                    dados[i] = dados[f];
                    dados[f] = troca;
                    i++;
                    f--;
                }
            }
        }

        dados[inicio] = dados[f];
        dados[f] = pivo;

        return f;
    }

    //======================================Metodos Respostas==================================================//

    public static void respostas(Pergunta p) throws Exception {
        int opcao;
        
        do{    
            System.out.println("\n\n-------------------------------");
            System.out.println("           Respostas");
            System.out.println("-------------------------------");

            System.out.println("\n"+p);
            System.out.println("\n1 - Listar minhas respostas");
            System.out.println("2 - Responder");
            System.out.println("3 - Alterar");
            System.out.println("4 - Arquivar");

            System.out.println("\n0 - Retornar");

            System.out.print("\nOpção: ");
            
            try {
                opcao = Integer.valueOf(leitor.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch(opcao){
                case 1:{
                    System.out.println("(não implementado)");                    
                }break;

                case 2:{
                    responder(p.getID());                    
                }break;

                case 3:{
                    System.out.println("(não implementado)");                    
                }break;

                case 4:{
                    System.out.println("(não implementado)");                    
                }break;

                case 0: break;

                default: {
                    System.out.println("Opção Inválida!");
                    Thread.sleep(1500);
                }
            }
        }while(opcao != 0);
    }

    public static void responder(int idPergunta) throws Exception {
        System.out.println("\n\n-------------------------------");
        System.out.println("           Respostas > Responder");
        System.out.println("-------------------------------");

        System.out.println("Digite sua resposta:");
        String novaResposta = leitor.nextLine();

        System.out.println("\nConfirmar?:");
        int opcao = confirmar();
        
        if(opcao == 1){
            Resposta r = new Resposta(idPergunta, idUsuarioAtual, novaResposta);
            arqRespostas.create(r);
            arvorePR.create(idPergunta, r.getID());
            arvoreUR.create(idUsuarioAtual, r.getID());            
            System.out.println("Resposta postada!");
            Thread.sleep(1500);
        }else{
            System.out.println("Criação cancelada!");
            Thread.sleep(1500);
        }
    }



    //======================================Metodos Auxiliares==================================================//

    public static int listaPerguntas(int[] arrayIdPerguntas) throws Exception{
        int cont = 0;

        System.out.println("\t\nListando:\n___________________________________");

        for(int i = 0; i < arrayIdPerguntas.length; i++){
            Pergunta temp = arqPerguntas.read(arrayIdPerguntas[i]);
            System.out.print("\n\n"+(i+1)+". ");
            System.out.println(temp);

            System.out.println("\n___________________________________");

            cont++;
        }

        return cont;
    }

    public static int listaRespostas(int[] arrayIdRespostas) throws Exception{
        int cont = 0;

        for(int i = 0; i < arrayIdRespostas.length; i++){
            Resposta temp = arqRespostas.read(arrayIdRespostas[i]);
            System.out.print("\n"+(i+1)+". ");
            System.out.println(temp);

            System.out.println("\n___________________________________");

            cont++;
        }

        return cont;
    }

    public static int[] retiraArquivadas(int[] arrayIdPerguntas) throws Exception {
        int[] aux = new int[arrayIdPerguntas.length];
        int j = 0;

        for(int i = 0; i < arrayIdPerguntas.length; i++){
            Pergunta temp = arqPerguntas.read(arrayIdPerguntas[i]);            
            if(temp.ativa){
                aux[j] = temp.getID();
                j++;
            }
        }

        int[] arrayAtivas = new int[j];
        for(int i = 0; i < j; i++){
            arrayAtivas[i] = aux[i];
        }

        return arrayAtivas;
    }

    public static int confirmar() throws InterruptedException {
        int opcao;
        System.out.println("1 - Sim");
        System.out.println("2 - Não");
        System.out.print("\nOpção: ");

        try {
            opcao = Integer.valueOf(leitor.nextLine());
        } catch(NumberFormatException e) {
            opcao = 2;
            System.out.println("Opção inválida!");
        }
        return opcao;
    }

     //====================================== Metodos Termos Chaves ==================================================//

    public static String converteChaves(String termosChaves){
        String convertida = termosChaves;

        convertida = convertida.toLowerCase();
        
        convertida = convertida.replaceAll(" ; ",";");
        convertida = convertida.replaceAll("; ",";");
        convertida = convertida.replaceAll(" ;",";");
        convertida = convertida.replaceAll(" ", "-");

        convertida = convertida.replaceAll("ç", "c");
        convertida = convertida.replaceAll("ñ", "n");

        convertida = convertida.replaceAll("á", "a");
        convertida = convertida.replaceAll("à", "a");
        convertida = convertida.replaceAll("â", "a");
        convertida = convertida.replaceAll("ã", "a");
        convertida = convertida.replaceAll("ä", "a");

        convertida = convertida.replaceAll("é", "e");
        convertida = convertida.replaceAll("è", "e");
        convertida = convertida.replaceAll("ê", "e");
        convertida = convertida.replaceAll("ë", "e");

        convertida = convertida.replaceAll("í", "i");
        convertida = convertida.replaceAll("ì", "i");
        convertida = convertida.replaceAll("î", "i");
        convertida = convertida.replaceAll("ï", "i");

        convertida = convertida.replaceAll("ó", "o");
        convertida = convertida.replaceAll("ò", "o");
        convertida = convertida.replaceAll("ô", "o");
        convertida = convertida.replaceAll("õ", "o");
        convertida = convertida.replaceAll("ö", "o");

        convertida = convertida.replaceAll("ú", "u");
        convertida = convertida.replaceAll("ù", "u");
        convertida = convertida.replaceAll("û", "u");
        convertida = convertida.replaceAll("ü", "u");

        return convertida;
    }

    public static void atualizaTermosChave(String termosAntigos, String termosEditados, int idPerg) throws Exception {
              
        HashSet<String> arrset1 = new HashSet<String>();
        HashSet<String> arrset2 = new HashSet<String>();
        String[] tmp, tmp2;

        tmp = termosAntigos.split(";");
        for(int i = 0; i < tmp.length; i++){
            arrset1.add(tmp[i]);
        }
        
        tmp = termosEditados.split(";");
        for(int i = 0; i < tmp.length; i++){
            arrset2.add(tmp[i]);
        }

        arrset1.removeAll(arrset2);
        tmp2 = new String[arrset1.size()];
        tmp = arrset1.toArray(tmp2);
        removeTermosChaves(tmp2, idPerg);

        //===========================================//

        tmp = termosAntigos.split(";");
        for(int i = 0; i < tmp.length; i++){
            arrset1.add(tmp[i]);
        }

        arrset2.removeAll(arrset1);
        tmp2 = new String[arrset2.size()];
        tmp2 = arrset2.toArray(tmp2);
        adicionaTermosChaves(tmp2, idPerg);
    }

    public static void adicionaTermosChaves(String[] tmp, int idPerg) throws Exception {

        for(int i = 0; i < tmp.length; i++){
            listaInvertida.create(tmp[i], idPerg);
        }
    }

    public static void removeTermosChaves(String[] tmp, int idPerg) throws Exception {

        for(int i = 0; i < tmp.length; i++){
            listaInvertida.delete(tmp[i], idPerg);
        }
    }
    

    
}
