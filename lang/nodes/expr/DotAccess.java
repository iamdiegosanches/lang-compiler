///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class DotAccess extends Exp implements LValue {
    private LValue record;
    private String fieldName;

    public DotAccess(int line, int col, LValue record, String fieldName) {
        super(line, col);
        this.record = record;
        this.fieldName = fieldName;
    }

    public LValue getRecord() { return record; }
    public String getFieldName() { return fieldName; }

    @Override
    public String getName() {
        return record.getName() + "." + fieldName;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}