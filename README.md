# lang-compiler
Compilador para a linguagem lang


lang-compiler/
├── src/                     # Código fonte principal
│   ├── main/
│   │   ├── java/
│   │   │   └── lang/        # Pacote base da nossa linguagem
│   │   │       ├── Main.java          # Classe principal para executar o analisador
│   │   │       ├── Token.java         # Definição da classe Token (conforme fornecido)
│   │   │       └── Lexer.java         # Classe do analisador léxico gerada pelo JFlex (ou wrapper)
│   │   └── jflex/
│   │       └── lang.flex        # Arquivo de especificação do JFlex para a linguagem lang
├── build/                   # Arquivos gerados durante a compilação (classes, etc.)
│   └── classes/
├── lib/                     # Bibliotecas externas (ex: jflex-full.jar)
├── examples/                # Arquivos de exemplo com código na linguagem lang
│   └── test1.lang
├── Makefile                 # Makefile para automatizar compilação e execução
└── README.md                # Descrição do projeto
