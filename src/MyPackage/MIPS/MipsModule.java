package MyPackage.MIPS;

import MyPackage.MIPS.Instruction.Add;
import MyPackage.MIPS.Instruction.Addi;
import MyPackage.MIPS.Instruction.J;
import MyPackage.MIPS.Instruction.Lw;
import MyPackage.MIPS.Instruction.Sw;
import MyPackage.OutPut;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsModule {
    static ArrayList<MipsDecl> data = new ArrayList<>();
    static ArrayList<Mips> text = new ArrayList<>();
    static int stringCount = 0;
    static HashMap<Integer, Integer> address = new HashMap<>();
    static HashMap<Integer, String> reg = new HashMap<>();

    static public HashMap<Integer, String> param = new HashMap<>();
    static public HashMap<Integer, Integer> alloc = new HashMap<>();
    static int stack = 0;
    static int regID = -1;
    public static StringBuilder stringBuilder = null;
    public static boolean isPut = false;

    public static int clearTimes = 0;
    public static int level = 0;
    public static boolean next = false;

    public static void tableClear() {
        address.clear();
        reg.clear();
        param.clear();
        alloc.clear();
        regID = -1;
        for (int i = 0; i < longTime; i++) {
            regValue[i] = 0;
            regActive[i] = 0;
        }
        longTime = 0;
        level = 0;
        next = false;
        miss = 0;
    }

    public static void addAlloc(int value) {
        alloc.put(value, stack);
    }
    public static void addParam(int value, String re) {
        param.put(value, re);
    }

    public static String getParam(int value) {
        return param.getOrDefault(value, null);
    }

    public static void addAddress(int value, int addr) {
        address.put(value, addr);
    }

    public static int getAddress(int value) {
        return address.getOrDefault(value, -1);
    }

    public static void addDate(MipsDecl mipsDecl) {
        data.add(mipsDecl);
    }

    public static void addText(Mips mips) {
        text.add(mips);
    }

    public static void addString() {
        addDate(new MipsDecl(String.format("lhc_str%d", stringCount), stringBuilder.toString(), "asciiz"));
        stringBuilder = null;
        stringCount++;
    }

    public static boolean isMain = false;

    public static void addReg(int value, String re) {
        if (re.charAt(2) == '9' || re.charAt(2) == '8') {
            int off = MipsModule.getStack();
            MipsModule.addStack(4);
            MipsModule.addText(new Sw(re, "$sp", off));
            MipsModule.addAddress(value, off);
        }
        else {
            reg.put(value, re);
        }
    }

    public static String searchReg(int value) {
        String re = reg.getOrDefault(value, null);
        if (re == null) {
            int off = MipsModule.getAddress(value);
            if (off == -1) {
                re = param.get(value);
            }
            else {
                re = MipsModule.getReg();
                MipsModule.addText(new Lw(re, "$sp", off));
            }
        }
        return re;
    }



    public static String searchReg2(int value) {
        return reg.getOrDefault(value, null);
    }

    public static String searchReg3(int value, String op) {
        String re = reg.getOrDefault(value, null);
        if (re == null) {
            int off = MipsModule.getAddress(value);
            if (off == -1) {
                re = param.get(value);
            }
            else {
                re = op;
                MipsModule.addText(new Lw(re, "$sp", off));
            }
        }
        return re;
    }

    public static void newStack() {
        stack = 0;
    }

    public static int getStack() {
        return stack;
    }

    public static void addStack(int size) {
        stack = stack - size;
    }


    public static void regClear() {
        regID = -1;
        reg.clear();
        if (longTime == 12) {
            longTimeClear2();
        }
    }

    public static String getReg() {
        regID++;
        if (regID <= 7) {
            return String.format("$t%d", regID);
        }
        else if (regID%2 == 1){
            return "$t8";
        }
        else {
            return "$t9";
        }
    }

    public static int getStringCount() {
        return stringCount;
    }

    public static void print() {
        OutPut.printMips(".data");
        for (int i = 0; i < data.size(); i++) {
            data.get(i).print();
        }
        OutPut.printMips(".text");
        for (int i = 0; i < text.size(); i++) {
            text.get(i).print();
        }
    }

    public static void saveReg() {
        for (int value : reg.keySet()) {
            int off = MipsModule.getStack();
            String re = reg.get(value);
            if (re.charAt(1) == 't') {
                MipsModule.addStack(4);
                MipsModule.addText(new Sw(re, "$sp", off));
                MipsModule.addAddress(value, off);
            }
        }
    }

    public static void peepholeOptimization() {
        for (int i = 0; i < text.size() - 1; i++) {
            Mips mips = text.get(i);
            if (mips instanceof J && text.get(i+1) instanceof label &&
                    ((J)mips).getString().equals(((label)text.get(i+1)).getString())) {
                text.set(i, new non());
            }
            else if(mips instanceof Add && ((Add) mips).getOp3().equals("$0")) {
                Add add = (Add) mips;
                int imm = 0;
                int j = 1;
                String op1 = add.getOp1();
                String op2 = add.getOp2();
                while (j + i < text.size() && text.get(j + i) instanceof Addi && ((Addi) text.get(j + i)).equal(op1)) {
                    imm += ((Addi) text.get(j+i)).getImmediate();
                    text.set(i+j, new non());
                }
                if (imm != 0) {
                    text.set(i, new Addi(op1, op2, imm));
                }
            }
        }
    }





    public static int[] regValue = new int[32];
    public static int[] regActive = new int[32];
    public static int miss = 0;
    public static int longTime = 0;



    public static String getRegName(int regID) {
        if (regID <= 7) {
            return String.format("$s%d", regID);
        }
        else if (regID == 8) {
            return "$k0";
        }
        else if (regID == 9) {
            return "$k1";
        }
        else if (regID == 10) {
            return "$v1";
        }
        else if (regID == 11) {
            return "$fp";
        }
        return null;
    }

    public static int getRegId(String re) {
        if (re.charAt(1) == 's') {
            return re.charAt(2) - '0';
        }
        else if (re.equals("$k0")) {
            return 8;
        }
        else if (re.equals("$k1")) {
            return 9;
        }
        else if (re.equals("$v1")) {
            return 10;
        }
        else if (re.equals("$fp")) {
            return 11;
        }
        return 0;
    }

    public static void longTimeClear() {
        for (int i = 0; i < longTime; i++) {
            int off = MipsModule.getAddress(regValue[i]);
            String op = getRegName(i);
            MipsModule.addText(new Sw(op, "$sp", off));
        }
    }

    public static void longTimeClear2() {
        for (int i = 0; i < longTime; i++) {
            int off = MipsModule.getAddress(regValue[i]);
            String op = getRegName(i);
            MipsModule.addText(new Sw(op, "$sp", off));
        }
        longTime = 0;
    }

    public static void longTimeReturn() {
        for (int i = 0; i < longTime; i++) {
            int off = MipsModule.getAddress(regValue[i]);
            String op = getRegName(i);
            MipsModule.addText(new Lw(op, "$sp", off));
        }
    }

    public static void addLongTime(int value) {
        miss++;
    }

    public static String containLongTime(int value) {
        String op = null;
        for (int i = 0; i < longTime; i++) {
            if (regValue[i] == value) {
                op = getRegName(i);
            }
        }
        return op;
    }

    public static void LongTime(int value, boolean isAlloc) {
        if (longTime < 12) {
            regValue[longTime] = value;
            String op = getRegName(longTime);
            longTime++;
        }
        else if (level == 0){
            int index = 0;
            int max = -1;
            for (int i = 0; i < longTime; i++) {
                if (max < regActive[i]) {
                    index = i;
                    max = regActive[i];
                }
            }
            String op1 = getRegName(index);
            int off = getAddress(regValue[index]);
            text.add(new Sw(op1, "$sp", off));
            regActive[index] = 0;
            regValue[index] = value;
        }
    }

    public static void LongTime(int value, String op) {
        if (longTime < 12) {
            regValue[longTime] = value;
            int off = getAddress(value);
            String op1 = getRegName(longTime);
            text.add(new Sw(op1, "$sp", off));
            regActive[longTime] = 0;
            regValue[longTime] = value;
            longTime++;
        }
        else {
            int index = 0;
            int max = -1;
            for (int i = 0; i < longTime; i++) {
                if (max < regActive[i]) {
                    index = i;
                    max = regActive[i];
                }
            }
            String op1 = getRegName(index);
            int off = getAddress(regValue[index]);
            text.add(new Sw(op1, "$sp", off));
            regActive[index] = 0;
            regValue[index] = value;
            text.add(new Add(op1, op, "$0"));
        }
    }

    public static int getLongTime() {
        return longTime;
    }
}
