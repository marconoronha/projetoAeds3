#### Grupo: Marco Aurélio de Noronha, Júnior Heleno Ávila, Cláudio Jansen, Paulo Rievrs Oliveira

# Versão 4

## Respondendo Perguntas

Agora o usuário é capaz de responder perguntas. Quando um usuário seleciona uma das perguntas que ele buscou digitando termos chaves, a pergunta selecionada é listada de maneira completa exibindo: 
 - Informações da pergunta
 - Todas as respostas que foram dadas à essa pergunta

O usuário também pode arquivar e editar as próprias respostas à uma determinada pergunta.

_Obs: Nesse caso, o arquivamento é definitivo e respostas arquivadas não são mais exibidas._


# Versão 3

## Interagindo com as Perguntas

### Termos Chaves

Quando uma pergunta é criada por um usuário também é pedido à ele que digite alguns termos chaves relacionados às perguntas. Esse termos chaves são armazenados em uma lista invertida. Cada termo chave tem uma lista de idPerguntas relacionadas. Na busca o usuário digita os termos que deseja pesquisar e então cada lista de cada termo é verificada. O retorno desse método é apenas as perguntas que tiverem todos os termos.

_Obs: Perguntas arquivadas não são exibidas na busca._


# Versão 2

## Relação Usário - Pergunta

Uma ÁrvoreB+ que recebe dois inteiros (idUser, idPergunta) é criada de maneira global na Main. Essa árvore retorna todas as perguntas de um usuário cujo idUser foi passado por parâmetro no método _read_ dessa árvore.

# Versão 1

## Main(Class)

|name|type|description|
|---|---|---|
|idUsuarioAtual|int|Guarda o id do usuário que está logado atualmente de maneira global para ser acessado em outros métodos|

## main (método)
|name|type|description|
|---|---|---|
|con|Console|Usado apenas para que o terminal oculte os caracters quando senhas forem digitada|
|opcao|int|Guarda a opção que o usuário digitará para navegar pelo programa da maneira que ele deseja|
|emailDigitado|String|Guarda o email que o usuário digitou tanto no login quanto durante a criação de uma nova conta|
|nomeDigitado|String|Guarda o nome que o usuário digitou durante a criação de uma nova conta|
|senhaDigitada|String|Guarda a senha que o usuário digitou tanto no login quanto durante a criação de uma nova conta|
|senhaConfirmada|String|Será comparada com a variável *senhaDigitada* para conferir se as senhas que o usuário digitou são iguais durante a criação de uma nova conta|
|userLogin|User|Usada para acessar os dados de um registro já criado antes de permitir o login|
|novoUser|User|Usada apenas para criar um novo registro|

## CRUD Indexado

Este documento explica como funciona a classe CRUD Indexado com tipo genérico. Essa classe _extends_ a interface _Registro_.

## Registro

```java
import java.io.IOException;

public interface Registro {
    public int getID();
    public void setID(int n);
    public byte[] toByteArray() throws IOException;
    public void fromByteArray(byte[] ba) throws IOException;
    public String chaveSecundaria();
    public int getUserID();
}
```
Os métodos acima devem estar implementados nas classes de cada objeto que usarão o CRUD Indexado para manipular arquivos.

## Construtor

O construtor recebe o tipo do objeto que ele será atribuído e o nome do arquivo que será utilizado. Dentro do próprio construtor, esse arquivo é criado e inicializado com o cabeçalho com o campo indicando o último ID cadastrado. 

São criados os arquivos índice usando a string passada como parâmetro no construtor, mudando apenas sua extensão. 

|name|type|description|
|---|---|---|
|he|HashExtensivel|Estrutura de dados usado no índice direto|
|ab|ArvoreBMais_String_Int|Estrutura de dados usado no índice indireto;|
#

## create

|name|type|description|
|---|---|---|
|ba|byte[]|Vetor de bytes em que o objeto recebido por parâmetro será armazenado e convertido até a criação do registro;
|id|int| **1º** - usado para guardar atribuir a ID real do objeto; **2º** - usado para atualizar o cabeçalho e o índice direto;|
|pos|long|**1º** - usado para armazenar a posição do registro; **2º** - usado para atualizar o índice direto;|
|chaveSecundaria|String|Armazena o valor da chave secundária para ser usado no índice indireto;|
#

## read(int)

|name|type|description|
|---|---|---|
|pos|long|usado para armazenar a posição do registro que foi retornado pelo índice direto|
|ba|byte[]|Vetor de bytes em que o registro lido será armazenado;|
|id|int|ID do objeto procurado (recebido por parâmetro);|
|found|boolean|Verifica se o objeto foi encontrado;|
#

## read(String)

|name|type|description|
|---|---|---|
|id|int|usado para armazenar o id do registro que foi retornado pelo índice indireto e então é usado como parâmetro pela a função read(int)|
#

## update

|name|type|description|
|---|---|---|
|ba|byte[]|Vetor de bytes em que o objeto recebido por parâmetro será armazenado;|
|ba2|byte[]|Vetor de bytes em que o registro já existente será armazenado;|
|id|int|ID do objeto procurado (recebido por parâmetro);|
|ok|boolean|Verifica se o objeto foi atualizado|
|pos|long|utilizado para movimentar o ponteiro do arquivo;|
#

## delete

|name|type|description|
|---|---|---|
|ba|byte[]|Vetor de bytes em que o objeto recebido por parâmetro será convertido e armazenado;|
|id|int|ID do objeto procurado (recebido por parâmetro);|
|ok|boolean|Verifica se o objeto foi excluído com sucesso;|
|pos|long| Usado para armazenar a posição do registro que foi retornado pelo índice direto;|

#

Criado por Marco Aurélio de Noronha Santos
