package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiuyuan Qiu
 */
public class Repository implements Serializable {

    /** The Map of all commits and their hashes. Key hash, Value commit */
    private HashMap commits;
    /** The Map of all files staged for commits. Key hash, Value file */
    private HashMap stagingArea;

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
    /** The repository file */
    private static final File REPO = join(GITLET_DIR, "repository");
    /** The staging directory */
    public static final File STAGING_DIR = join(GITLET_DIR, "stage_add");
    /** The commits directory */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The blob directory */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");



    /* TODO: fill in the rest of this class. */

    /** initalizes gitlet directory. creates the first commit and stores it in a hashmap */
    public Repository() {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOB_DIR.mkdir();
        Commit firstCommit = new Commit("initial commit");
        commits = new HashMap();
        stagingArea = new HashMap();
        commits.put(firstCommit.getHash(), firstCommit);
    }

    /** Serializes and saves the repo in .gitlet/repository */
    public void save() {
        writeObject(REPO, this);
    }
    /** returns the current repository object in .gitlet/repository */
    public static Repository load() {
        return readObject(REPO, Repository.class);
    }

    /** puts a reference to the file in the staging Map and copies the file to STAGING_DIR
     * file name in STAGING_DIR is the hash
     * If the CWD file is the same as the file in the current commit, do not stage */
    //TODO: If the CWD file is the same as the file in the current commit, do not stage
    public void add(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (fileUnchanged(filename)) {
            System.out.println("File unchanged"); //TODO: remove when done testing
            return;
        }
        byte[] fileContents = readContents(file);
        String hash = sha1(fileContents);
        stagingArea.put(hash, file); //TODO: Maybe change hash to filename as the key?
        writeContents(join(STAGING_DIR, filename), fileContents);
    }
    /**Checks if the contents for a file in the CWD is unchanged from the one in STAGING_DIR (if both are the same)*/
    private boolean fileUnchanged(String filename) {
        File cwdFile = join(CWD, filename);
        File stagedFile = join(STAGING_DIR, filename);
        if (!stagedFile.exists()) {
            return false;
        }
        return sha1(readContents(cwdFile)).equals(sha1(readContents(stagedFile)));
    }

    //TODO: Need a head pointer to keep track of the current commit to set as parent
    private void commit() {

    }

}
