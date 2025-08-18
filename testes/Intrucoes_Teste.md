# Programa de Testes Automatizados para Lang

A imagem a seguir mostra a tela principal do programa de testes da linguagem Lang.  
Este programa tem por objetivo testar diferentes estágios do compilador da Lang sobre um conjunto de instâncias pré-determinadas.  
Os resultados obtidos são automaticamente comparados com os resultados esperados, e um relatório de erros é gerado indicando quais instâncias falharam.

![image](figs/principal.png =550x)

A aplicação precisa ser configurada para executar o compilador a partir do diretório **onde o programa de testes está localizado**, como será explicado a seguir.  
Os arquivos de teste estão localizados nas pastas *sintaxe*, *types* e *semantica*, correspondendo aos testes dos estágios de análise sintática, verificação de tipos e interpretação, respectivamente.

---

## Selecionando os Testes

Na seção "Testes disponíveis", marque os testes que deseja executar.  
Na figura a seguir, todos os testes estão selecionados:

![image](figs/testes.png =550x)

---

## Configurando o Programa de Testes

Para que o programa de testes funcione corretamente, o compilador deve aceitar as seguintes opções por linha de comando:

- `-syn` : Realiza a verificação sintática e deve imprimir **apenas** `accepted` ou `rejected`.  
- `-i`   : Interpreta o programa, imprimindo **apenas** a saída do programa.  
- `-t`   : Realiza a verificação de tipos e deve imprimir **apenas** `well-typed` ou `ill-typed`.  
- `-v`   : Imprime a versão do compilador.

É fundamental que essas opções estejam implementadas **exatamente como descrito** e que os resultados produzidos sejam estritamente compatíveis com as expectativas.  
Os argumentos passados ao compilador podem ser configurados, mas **qualquer discrepância nos resultados será interpretada como erro**.

Suponha que sua classe `main` esteja em `LangCompiler.java`, localizada dentro da pasta `lang`, na raiz do seu projeto.  
Suponha ainda que a pasta do programa de testes tenha sido copiada para a raiz do projeto do compilador, como mostrado na estrutura de diretórios abaixo:

```
Raiz_Do_Projeto
|
|--+ lang
|   |-- LangCompiler.java
|   |-- LangCompiler.class
|   |--+ tools
|       |-- java-cup-11b-runtime.jar
|       |-- ...
|
|--+ testes
    |-- langtester
    |-- semantica
    |-- sintaxe
    |-- cont.txt
    |-- Makefile
    |-- Instrucoes_Teste.md
```

Nesse caso:

- No campo **Arquivo executável do compilador**, informe o nome do programa que será executado, incluindo o caminho relativo.
- No campo **Comando base**, informe o comando necessário para executar o programa. Neste exemplo, utilizamos o interpretador Java.
- No campo **Parâmetros adicionais**, informe quaisquer parâmetros extras necessários, como o *classpath* e bibliotecas adicionais que o compilador da Lang utiliza.

> Observação: o *classpath* deve ser referenciado a partir do diretório onde está localizado o programa de testes.

Nos campos seguintes, informe os parâmetros correspondentes para cada etapa do compilador da Lang.  
A imagem a seguir mostra a configuração finalizada. Você pode clicar em **Salvar** para guardar a configuração atual, que será carregada automaticamente na próxima execução do programa.

Quando estiver tudo pronto, utilize o botão **Testar Conf.** para executar o compilador com a opção `-v` e verificar se ele responde corretamente.  
Se tudo estiver certo, clique em **Executar** para iniciar os testes.

![image](figs/coonfigurando.png =550x)

---

## Resultados dos Testes

Após a execução, o programa exibirá, para cada tipo de teste realizado, o número de instâncias que falharam.  
Basta dar um duplo clique na linha correspondente da tabela para visualizar a lista de arquivos que falharam.

![image](figs/running.png =550x)
