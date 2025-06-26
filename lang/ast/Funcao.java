package lang.ast;

public class Funcao extends NoAST {
    private String id;
    private Block body;
    
    public Funcao(String id, Block body) {
        this.id = id;
        this.body = body;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Funcao(" + id + ")");
        if (body != null) {
            body.print(prefix + "  ");
        }
    }
}
