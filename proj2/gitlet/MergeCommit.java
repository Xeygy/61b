package gitlet;

public class MergeCommit extends Commit{
    String parent2;
    public MergeCommit(String message, String parent1, String parent2) {
        super(message, parent1);
        this.parent2 = parent2;
    }

    public void printInfo(String hash) {
        String shortPar1 = parent.substring(0, 7);
        String shortPar2 = parent2.substring(0, 7);
        System.out.println("===");
        System.out.println("commit " + hash);
        //TODO: same format as gitlet date
        System.out.println("Merge: " + shortPar1 + " " + shortPar2);
        System.out.println("Date: " + this.getDate());
        System.out.println(this.getMessage());
        System.out.print("\n");
    }
}
