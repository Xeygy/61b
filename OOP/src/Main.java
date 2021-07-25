import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        LinkedList l = new LinkedList(1);
        l.addFirst(2);
        l.addFirst(3);
        System.out.println(l.getFirst()); //3
        System.out.println(l.removeFirst()); //3
        System.out.println(l.getFirst()); //2

        Map m = new HashMap();
        Map n = new TreeMap();

    }
}
