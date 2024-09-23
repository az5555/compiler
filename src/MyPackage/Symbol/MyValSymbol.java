package MyPackage.Symbol;

import MyPackage.IR.DeclLlvm;
import MyPackage.IR.Value;

import java.util.ArrayList;

public class MyValSymbol extends Symbol{
    private int level;
    private ArrayList<Integer> levels;
    private ArrayList<Integer> values;
    private boolean isGlobal;
    private Value reg;

    private DeclLlvm declLlvm;

    public MyValSymbol(String name, int line, boolean isConst, String type, int level) {
        super(name, line, isConst, type);
        this.level = level;
        values = new ArrayList<>();
        isGlobal = false;
        declLlvm = null;
    }

    public int getLevel() {
        return level;
    }

    public int getLevel(int index) {
        return levels.get(index);
    }

    public void addValue(int n) {
        values.add(n);
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public Value getReg() {
        return reg;
    }

    public void setReg(Value reg) {
        this.reg = reg;
    }

    public void setDeclLlvm(DeclLlvm declLlvm) {
        this.declLlvm = declLlvm;
    }

    public DeclLlvm getDeclLlvm() {
        return declLlvm;
    }
}
