package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
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

    //TODO: add master branch
    /** The Map of all hashes of commits. Commits are stored with their hash name in .gitlet/commits/  */
    private HashSet commits;
    /** The Map of all files staged for commits. Stores filenames*/
    private HashSet stagingArea;
    /** The pointer to the current commit, is a String representing the hash of the commit */
    private String head;

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
        commits = new HashSet();
        stagingArea = new HashSet();

        Commit firstCommit = new Commit("initial commit");
        head = commitHash(firstCommit);
        writeObject(join(COMMITS_DIR, head), firstCommit);
        commits.add(head);
    }
    /** returns the sha1 hash of a commit */
    private String commitHash(Commit c) {
        return sha1(serialize(c));
    }
    /** Serializes and saves the repo in .gitlet/repository */
    public void save() {
        writeObject(REPO, this);
    }
    /** returns the current repository object in .gitlet/repository */
    public static Repository load() {
        return readObject(REPO, Repository.class);
    }

    /** adds a file to the staging area.
     * puts a reference to the file in the staging Map and copies the file to STAGING_DIR
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
            if(stagingArea.contains(file)) {
                File f = Utils.join(STAGING_DIR, filename);
                f.delete();
                stagingArea.remove(file);
            }
            return;
        }
        byte[] fileContents = readContents(file);
        stagingArea.add(file);
        writeContents(join(STAGING_DIR, filename), fileContents);
    }

    private boolean fileUnchanged(String filename) {
        File cwdFile = join(CWD, filename);
        String fileHash =  sha1(readContents(cwdFile));
        HashMap filesInCurrCommit = getCommit(head).getFiles();
        if (filesInCurrCommit.containsKey(filename) && filesInCurrCommit.get(filename).equals(fileHash)) {
            return true;
        }
        return false;
    }
    private Commit getCommit(String hash) {
        Commit c = readObject(join(Repository.COMMITS_DIR, hash), Commit.class);
        return c;
    }

    public void commit(String message) {
        if(stagingArea.isEmpty()) {
            return;
        }
        //new commit with parent as current commit
        Commit commit = new Commit(message, head);
        head = commitHash(commit);
        commits.add(head);
        writeObject(join(COMMITS_DIR, head), commit);
        emptyStagingArea();
        //TODO: update branch
    }
    private void emptyStagingArea() {
        for (String filename : plainFilenamesIn(STAGING_DIR)) {
            File f = Utils.join(STAGING_DIR, filename);
            f.delete();
        }
        stagingArea.clear();
    }

    public void log() {
        Commit currCommit = getCommit(head);
        while(currCommit != null) {
            System.out.println("===");
            System.out.println("commit " + commitHash(currCommit));
            //TODO: same format as gitlet date
            System.out.println("Date: " + currCommit.getDate());
            System.out.println(currCommit.getMessage());
            System.out.println();
            currCommit = currCommit.getParent();
        }
        //TODO: Merge logs
    }

}
