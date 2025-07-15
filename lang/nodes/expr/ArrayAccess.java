package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class ArrayAccess extends Exp implements LValue {
    private LValue arrayVar; // A variável do vetor (ex: 'meuArray')
    private Exp indexExp;   // A expressão do índice (ex: '0', 'i+1')

    public ArrayAccess(int line, int col, LValue arrayVar, Exp indexExp) {
        super(line, col);
        this.arrayVar = arrayVar;
        this.indexExp = indexExp;
    }

    public LValue getArrayVar() {
        return arrayVar;
    }

    public Exp getIndexExp() {
        return indexExp;
    }

    @Override
    public String getName() {
        return arrayVar.getName() + "[" + indexExp.toString() + "]";
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }

}