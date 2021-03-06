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
        Repository repo;
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
                    repo = new Repository();
                    repo.save();
                }
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.add(args[1]);
                repo.save();
                break;
            case "commit":
                /** commits staged files with message as the second operand */
                if (args.length > 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                } if (args.length == 1) {
                System.out.println("Please enter a commit message.");
                break;
            }
                repo = Repository.load();
                repo.commit(args[1]);
                repo.save();
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.remove(args[1]);
                repo.save();
                break;
            case "log":
                repo = Repository.load();
                repo.log();
                repo.save();
                break;
            case "global-log":
                repo = Repository.load();
                repo.globalLog();
                repo.save();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands."); //TODO: implement a checkOperands(int numArgs)
                    break;
                }
                repo = Repository.load();
                repo.find(args[1]);
                repo.save();
                break;
            case "status":
                repo = Repository.load();
                repo.status();
                repo.save();
                break;
            case "checkout":
                /**
                 * 3 cases:
                 * checkout -- filename //checksout file from current commit
                 * checkout commitId -- filename //checks out file from commitId
                 * checkout branchname //checks out that branch
                 */
                repo = Repository.load();
                if (args.length == 2) {
                    repo.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    repo.checkout(args[2]);
                } else if (args.length == 4) {
                    repo.checkout(args[1], args[3]);
                } else {
                    System.out.println("Incorrect Operands.");
                }
                repo.save();
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.branch(args[1]);
                repo.save();
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.rmBranch(args[1]);
                repo.save();
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.reset(args[1]);
                repo.save();
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect Operands.");
                    break;
                }
                repo = Repository.load();
                repo.merge(args[1]);
                repo.save();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
