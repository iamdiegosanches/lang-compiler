# Makefile para o Compilador da Linguagem Lang

# --- Variáveis de Diretório ---
SRC_DIR = src/main/java
JFLEX_SPEC = src/main/jflex/lang.jflex
GEN_SRC_DIR = src/main/java/lang
BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes

# --- Variáveis de Java ---
JC = javac --release 17
JAVA = java
MAIN_CLASS = lang.Main

# --- Bibliotecas ---
LIB_DIR = lib
JFLEX_JAR = $(LIB_DIR)/jflex.jar # Recomendo usar a versão full para ter todas as dependências
CLASSPATH = $(CLASSES_DIR):$(JFLEX_JAR)

# --- Alvo Principal (Default) ---
# Compila tudo por padrão quando 'make' é executado.
all: compile

# --- Alvo para Gerar o Analisador Léxico ---
# Regra para criar o Lexer.java a partir do lang.jflex.
# O arquivo Lexer.java é uma dependência para a compilação.
$(GEN_SRC_DIR)/Lexer.java: $(JFLEX_SPEC)
	@echo "Gerando o analisador léxico com JFlex..."
	@java -jar $(JFLEX_JAR) -d $(GEN_SRC_DIR) $(JFLEX_SPEC)

# --- Alvo para Compilar o Código Fonte ---
# Depende da geração do lexer.
# Cria o diretório de build e compila todos os arquivos .java.
compile: $(GEN_SRC_DIR)/Lexer.java
	@echo "Compilando o código fonte Java..."
	@mkdir -p $(CLASSES_DIR)
	@$(JC) -d $(CLASSES_DIR) -cp $(CLASSPATH) $(SRC_DIR)/lang/*.java

# --- Alvo para Executar o Compilador ---
# Permite especificar o arquivo de teste via argumento.
# Exemplo de uso: make run FILE=examples/test1.lang
run: compile
	@if [ -z "$(FILE)" ]; then \
		echo "Erro: Especifique o arquivo de entrada. Uso: make run FILE=<caminho_do_arquivo>"; \
		exit 1; \
	fi
	@echo "Executando o analisador no arquivo: $(FILE)"
	@$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS) $(FILE)

# --- Alvo para Limpar o Projeto ---
# Remove os arquivos gerados (diretório de build e o Lexer.java).
clean:
	@echo "Limpando o projeto..."
	@rm -rf $(BUILD_DIR)
	@rm -f $(GEN_SRC_DIR)/Lexer.java

# --- Phony Targets ---
# Declara alvos que não representam arquivos.
.PHONY: all compile run clean
