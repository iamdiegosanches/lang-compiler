package lang.nodes.visitors.tychkvisitor;

public class VTyUser extends VType {
    private String name;

    public VTyUser(String name) {
        super(CLTypes.UNDETERMINED);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean match(VType t) {
        if (t instanceof VTyUser) {
            return this.name.equals(((VTyUser) t).name) || t.getTypeValue() == CLTypes.NULL;
        }
        return t.getTypeValue() == CLTypes.NULL;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
