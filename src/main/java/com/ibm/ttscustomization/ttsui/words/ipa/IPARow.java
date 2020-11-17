package com.ibm.ttscustomization.ttsui.words.ipa;

import java.util.ArrayList;

public class IPARow {

    ArrayList<String> alContent = new ArrayList<>();

    public IPARow() {
        for (int i = 0; i <  20; i++) {
            alContent.add("" + i);
        }
    }

    String get(int i) {return alContent.get(i);}
    public void set(int i, String s) {alContent.set(i, s);}

    public String getC0() { return alContent.get(0); }
    public void setC0(String s) { alContent.set(0, s); }
    public String getC1() { return alContent.get(1); }
    public void setC1(String s) { alContent.set(1, s); }
    public String getC2() { return alContent.get(2); }
    public void setC2(String s) { alContent.set(2, s); }
    public String getC3() { return alContent.get(3); }
    public void setC3(String s) { alContent.set(3, s); }
    public String getC4() { return alContent.get(4); }
    public void setC4(String s) { alContent.set(4, s); }
    public String getC5() { return alContent.get(5); }
    public void setC5(String s) { alContent.set(5, s); }
    public String getC6() { return alContent.get(6); }
    public void setC6(String s) { alContent.set(6, s); }
    public String getC7() { return alContent.get(7); }
    public void setC7(String s) { alContent.set(7, s); }
    public String getC8() { return alContent.get(8); }
    public void setC8(String s) { alContent.set(8, s); }
    public String getC9() { return alContent.get(9); }
    public void setC9(String s) { alContent.set(9, s); }

    public String getC10() { return alContent.get(10); }
    public void setC10(String s) { alContent.set(10, s); }
    public String getC11() { return alContent.get(11); }
    public void setC11(String s) { alContent.set(11, s); }
    public String getC12() { return alContent.get(12); }
    public void setC12(String s) { alContent.set(12, s); }
    public String getC13() { return alContent.get(13); }
    public void setC13(String s) { alContent.set(13, s); }
    public String getC14() { return alContent.get(14); }
    public void setC14(String s) { alContent.set(14, s); }
    public String getC15() { return alContent.get(15); }
    public void setC15(String s) { alContent.set(15, s); }
    public String getC16() { return alContent.get(16); }
    public void setC16(String s) { alContent.set(16, s); }
    public String getC17() { return alContent.get(17); }
    public void setC17(String s) { alContent.set(17, s); }
    public String getC18() { return alContent.get(18); }
    public void setC18(String s) { alContent.set(18, s); }
    public String getC19() { return alContent.get(19); }
    public void setC19(String s) { alContent.set(19, s); }
}
