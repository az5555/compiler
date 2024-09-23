package MyPackage.IR.Instruction;

import MyPackage.IR.ArrayLlvm;
import MyPackage.IR.IRModule;
import MyPackage.IR.Type;
import MyPackage.IR.User;
import MyPackage.MIPS.Instruction.Lw;
import MyPackage.MIPS.Mips;
import MyPackage.MIPS.MipsDecl;
import MyPackage.MIPS.MipsModule;
import MyPackage.OutPut;
import MyPackage.Symbol.MyValSymbol;
import MyPackage.Symbol.Symbol;

public class LoadLlvm extends User {
    MyValSymbol symbol;
    ArrayLlvm arrayLlvm;
    public LoadLlvm(Type type, int value, Symbol symbol) {
        super(type, value);
        this.symbol = (MyValSymbol) symbol;
    }


    @Override
    public void print() {
        OutPut.printLlvm(String.format("    %s = load %s, %s * ", super.printValue(), getOperands().get(1).printType(),
                    getOperands().get(1).printType()));
         if (symbol.isGlobal() && getOperands().get(1) == symbol.getReg()) {
            OutPut.printLlvm(String.format("@%s\n", symbol.getName()));
            }
        else {
            OutPut.printLlvm(String.format("%s\n", getOperands().get(1).printValue()));
        }
    }

    public void setArrayLlvm(ArrayLlvm arrayLlvm) {
        this.arrayLlvm = arrayLlvm;
    }

    @Override
    public String printType() {
        if (getType().equals(Type.Array)) {
            return arrayLlvm.printType();
        }
        else {
            return super.printType();
        }
    }

    public ArrayLlvm getArrayLlvm() {
        return arrayLlvm;
    }

    public void generateMips() {
        String op1;
        if (symbol.isGlobal() && getOperands().get(1) == symbol.getReg()) {
            op1 = MipsModule.getReg();
            MipsModule.addText(new Lw(op1, "$0", symbol.getName()));
        }
        else {
            if (MipsModule.alloc.containsKey(getOperands().get(1).getValue())) {
                String op = MipsModule.containLongTime(getOperands().get(1).getValue());
                if (op == null) {
                    MipsModule.addLongTime(0);
                    op1 = MipsModule.getReg();
                    int off = MipsModule.getAddress(getOperands().get(1).getValue());
                    MipsModule.addText(new Lw(op1, "$sp", off));
                }
                else {
                    int off = MipsModule.getAddress(getOperands().get(1).getValue());
                    op1 = op;
                    MipsModule.addAddress(getValue(), off);
                }
            }
            else {
                op1 = MipsModule.getReg();
                String op = MipsModule.searchReg(getOperands().get(1).getValue());
                MipsModule.addText(new Lw(op1, op, 0));
            }
        }
        MipsModule.addReg(getValue(), op1);
    }
}
