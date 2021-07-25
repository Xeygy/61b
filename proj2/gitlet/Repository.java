package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiuyuan Qiu
 */
public class Repository implements Serializable {

    /** The Map of all commits and their hashes, Key hash, Value commit */
    private HashMap commits;
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    /* TODO: fill in the rest of this class. */

    /** initalizes gitlet directory. creates the first commit and stores it in a hashmap */
    public Repository() {
        GITLET_DIR.mkdir();
        Commit firstCommit = new Commit("initial commit");
        commits = new HashMap();
        commits.put(firstCommit.getHash(), firstCommit);
    }

    /** Serializes and saves the repo in .gitlet/repository */
    public void save() {
        File outFile = Utils.join(GITLET_DIR, "repository");
        writeObject(outFile, this);
    }
}
