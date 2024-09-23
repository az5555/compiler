package MyPackage.MIPS.Instruction;

import MyPackage.MIPS.Mips;
import MyPackage.OutPut;

public class And implements Mips {
    String op1;
    String op2;
    String op3;

    public And(String op1, String op2, String op3) {
        this.op1 = op1;
        this.op2 = op2;
        this.op3 = op3;
    }

    @Override
    public void print() {
        OutPut.printMips(String.format("    and %s, %s, %s", op1, op2, op3));
    }
}
