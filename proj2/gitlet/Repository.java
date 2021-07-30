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
    /** The Set of all files staged for commits. Stores files*/
    private HashSet stagingArea;
    /** The Set of all files staged for removal. Stores filenames*/
    private HashSet removalArea;
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
    /** The removal directory */
    public static final File REMOVAL_DIR = join(GITLET_DIR, "stage_rm");
    /** The commits directory */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The blob directory */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");



    /* TODO: fill in the rest of this class. */

    /** initalizes gitlet directory. creates the first commit and stores it in a hashmap */
    public Repository() {
        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        REMOVAL_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOB_DIR.mkdir();
        commits = new HashSet();
        stagingArea = new HashSet();
        removalArea = new HashSet();

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
    /** checks if file is unchanged from the one in the current head commit */
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
        if(stagingArea.isEmpty() && removalArea.isEmpty()) {
            return;
        }
        //new commit with parent as current commit
        Commit commit = new Commit(message, head);
        head = commitHash(commit);
        commits.add(head);
        writeObject(join(COMMITS_DIR, head), commit);
        emptyStagingAreas();
        //TODO: update branch
    }
    private void emptyStagingAreas() {
        for (String filename : plainFilenamesIn(STAGING_DIR)) {
            File f = Utils.join(STAGING_DIR, filename);
            f.delete();
        }
        stagingArea.clear();
        for (String filename : plainFilenamesIn(REMOVAL_DIR)) {
            File f = Utils.join(REMOVAL_DIR, filename);
            f.delete();
        }
        removalArea.clear();
    }

    public void remove(String filename) {
        boolean reasonToRemoveFile = false;
        //Is the file staged, if so, remove from staging
        File file = join(CWD, filename);
        if (stagingArea.contains(file)) {
            stagingArea.remove(file);
            reasonToRemoveFile = true;
        }
        //Is the file in the head commit, if so, stage for removal
        HashMap filesInCurrCommit = getCommit(head).getFiles();
        if (filesInCurrCommit.containsKey(filename)) {
            byte[] fileContents = readContents(file);
            removalArea.add(filename);
            writeContents(join(REMOVAL_DIR, filename), fileContents);
            file.delete();
            reasonToRemoveFile = true;
        }
        //Was there a reason to remove the file, if not, tell the user
        if (!reasonToRemoveFile) {
            System.out.println("No reason to remove the file.");
        }
    }

    /** displays commits in the current branch */
    public void log() {
        Commit currCommit = getCommit(head);
        while(currCommit != null) {
            printCommitInfo(currCommit);
            currCommit = currCommit.getParent();
        }
        //TODO: Merge logs
    }
    private void printCommitInfo(Commit c) {
        System.out.println("===");
        System.out.println("commit " + commitHash(c));
        //TODO: same format as gitlet date
        System.out.println("Date: " + c.getDate());
        System.out.println(c.getMessage());
        System.out.println();
    }
    //TODO: Merge logs
    /** displays all commits ever made in an arbitrary order*/
    public void globalLog() {
        for(String filename : plainFilenamesIn(COMMITS_DIR)) {
            Commit currCommit = readObject(join(COMMITS_DIR, filename), Commit.class);
            printCommitInfo(currCommit);
        }
    }


}
