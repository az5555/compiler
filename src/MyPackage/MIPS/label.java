package MyPackage.MIPS;

import MyPackage.OutPut;

public class label implements Mips{
    private String string;
    public label(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public void print() {
        OutPut.printMips(string+":");
    }
}
