package gitlet;

import java.util.ArrayList;

public class HashTest {
    public static void main(String[] args) {
        System.out.println(Utils.sha1("a"));
        Commit a = new Commit("a");

        //System.out.println(Utils.sha1(Utils.serialize(a)));
        System.out.println(a.getDate());
    }
}
