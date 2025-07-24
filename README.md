# lang-compiler

Compilador para a linguagem lang

---
**Autores:**
* Álvaro Braz Cunha - 21.1.8163
* Diego Sanches Nere dos Santos - 21.1.8003
---
**Compilar:**

Para compilar o projeto, execute o seguinte comando na raiz do repositório:

```
make
```

Este comando irá gerar os arquivos do analisador léxico (`LangLexer.java`), analisador sintático (`LangParser.java`, `LangParserSym.java`) usando JFlex e CUP, e compilar todos os arquivos `.java` do projeto, incluindo o `LangCompiler.java`.

**Limpeza do Projeto:**

Para limpar os arquivos de classes e os arquivos gerados pelo JFlex e CUP, utilize os seguintes comandos:

```
make cleanClasses    # Remove apenas os arquivos .class
make cleanParser     # Remove os arquivos gerados pelo JFlex e CUP (LangLexer.java, LangParser.java, LangParserSym.java)
make clean           # Remove todos os arquivos gerados (classes e parser)
```

---

**Execução do Analisador:**

O compilador `LangCompiler` pode ser executado a partir da linha de comando com as seguintes opções:

```
./langc.sh [opcao] <nome-do-arquivo>
```

ou

```
java -cp .:tools/java-cup-11b-runtime.jar LangCompiler [opcao] <nome-do-arquivo>
```

**Opções disponíveis:**

* `-lex`: Lista os tokens gerados pelo analisador léxico.

  * Exemplo: `java -cp .:tools/java-cup-11b-runtime.jar LangCompiler -lex ./testes/sintaxe/certo/attrADD.lan`
* `-i`: Interpreta o programa. Executa o analisador sintático, constrói a Árvore Sintática Abstrata (AST) e executa o interpretador sobre a AST construída.

  * Exemplo: `java -cp .:tools/java-cup-11b-runtime.jar LangCompiler -i ./testes/semantica/certo/simple/teste0.lan`
* `-syn`: Executa apenas o analisador sintático e imprime "accepted" se o programa estiver sintaticamente correto ou "rejected" caso contrário.

  * Exemplo: `java -cp .:tools/java-cup-11b-runtime.jar LangCompiler -syn ./testes/sintaxe/certo/attrCMD.lan`Execução do Analisador:

**Estrutura de Diretórios (para referência):**

Para que o `classpath` funcione corretamente, a estrutura de diretórios esperada é que o `LangCompiler.java` e seus arquivos de classes estejam na pasta `lang/` (ou no diretório especificado por `destdir` no `makefile` para os arquivos do parser), e as ferramentas (`java-cup-11b-runtime.jar`, `jflex.jar`, etc.) estejam em `tools/`. O comando `make` já trata dessa organização para a compilação.

```
Raiz_Do_Projeto
|
|--+ lang
|   |-- LangCompiler.java
|   |-- LangCompiler.class
|   |--+ parser/
|   |   |-- LangLexer.java
|   |   |-- LangParser.java
|   |   |-- LangParserSym.java
|   |   |-- ...
|   |--+ nodes/
|   |   |-- ...
|   |--+ tools/
|       |-- java-cup-11b-runtime.jar
|       |-- jflex.jar
|       |-- java-cup-11b.jar
|
|--+ testes/
|   |-- ... (seus arquivos de teste e o LangTester.jar)
|
|-- README.md
|-- makefile
|-- langc.sh
```
