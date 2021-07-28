package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        //TODO: if repository not initialized don't work
        switch(firstArg) {
            case "init":
                /** creates a new gitlet version control system in the current directory. The initial
                 * commit has the commit message "initial commit" with no files and a single
                 * master branch. Timestamp 00:00:00 UTC, 1/1/1970
                 */
                if(Repository.GITLET_DIR.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                } else {
                    Repository repo = new Repository();
                    repo.save();
                }
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                Repository repo = Repository.load();
                repo.add(args[1]);
                repo.save();
                break;
            case "commit":
                /** commits staged files with message as the second operand */
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                //TODO: deal with scope (how to load repo)
                Repository repo1 = Repository.load();
                repo1.commit(args[1]);
                repo1.save();
                break;
            case "rm":
                break;
            case "log":
                Repository repo3 = Repository.load();
                repo3.log();
                repo3.save();
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
