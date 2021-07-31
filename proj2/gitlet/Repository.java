package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    /** The Set of all hashes of commits. Commits are stored with their hash name in .gitlet/commits/  */
    private HashSet commits;
    /** The Set of all files staged for commits. Stores filenames*/
    private HashSet stagingArea;
    /** The Set of all files staged for removal. Stores filenames*/
    private HashSet removalArea;
    /** The pointer to the current commit, is a String representing the hash of the commit */
    private String head;
    /** The name of the current branch */
    private String currBranch;
    /** Key branchname, Value branchhash: a map of all branches */
    private HashMap<String, String> branches;

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
        branches = new HashMap();
        currBranch = "master";

        Commit firstCommit = new Commit("initial commit");
        head = commitHash(firstCommit);
        branches.put(currBranch, head);
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
            if(stagingArea.contains(filename)) {
                File f = Utils.join(STAGING_DIR, filename);
                f.delete();
                stagingArea.remove(filename);
            }
            return;
        }
        byte[] fileContents = readContents(file);
        stagingArea.add(filename);
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
        branches.put(currBranch, head);
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
        //Is the file staged, if so, remove from stagingArea
        if (stagingArea.contains(filename)) {
            stagingArea.remove(filename);
            reasonToRemoveFile = true;
        }
        //Is the file in the head commit, if so, stage for removal
        HashMap filesInCurrCommit = getCommit(head).getFiles();
        File file = join(CWD, filename);
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

    /** displays all commits with a given message */
    public void find(String message) {
        boolean foundMessage = false;
        for(String filename : plainFilenamesIn(COMMITS_DIR)) {
            Commit currCommit = readObject(join(COMMITS_DIR, filename), Commit.class);
            if (currCommit.getMessage().equals(message)) {
                printCommitInfo(currCommit);
                foundMessage = true;
            }
        }
        if (!foundMessage) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** displays branches, the current branch, staged files
     * Modifications not staged and untracked files are optional
     */
    public void status() {
        System.out.println("=== Branches ===");
        for(String branchName : branches.keySet()) {
            if (branchName.equals(currBranch)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println("=== Staged Files ===");
        for (String filename : plainFilenamesIn(STAGING_DIR)) {
            System.out.println(filename);
        }
        System.out.println("\n=== Removed Files ===");
        for (String filename : plainFilenamesIn(REMOVAL_DIR)) {
            System.out.println(filename);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ==="); //TODO: this
        Commit currCommit = getCommit(head);
        for (String filename : currCommit.getFilenames()) {
            //different from currCommit
            if (sha1(readContents(currCommit.getFile(filename))).equals(sha1())) {

            }
        }
        System.out.println("\n=== Untracked Files ===");
        for (String filename : plainFilenamesIn(CWD)) {
            if (!getCommit(head).getFiles().containsKey(filename) && !stagingArea.contains(filename)) {
                System.out.println(filename);
            }
        }
    }

    public void checkout(String commitId, String filename) {
        //TODO: support shortened commitIDs
        if (!commits.contains(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = getCommit(commitId);
        File commitedFile = c.getFile(filename);
        byte[] fileContents = readContents(commitedFile);
        writeContents(join(CWD, filename), fileContents);
    }
    public void checkout(String filename) {
        checkout(head, filename);
    }

    /**
     *  takes files in a commit at a given branch and puts them in the working directory
     *  new branch becomes the head
     *  files tracked in the current branch but not the checked out branch are cleared
     *  staging area is cleared unless checked-out is the current branch
     */
    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(currBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit checkoutCommit = getCommit(branches.get(branchName));
        Commit currCommit = getCommit(head);
        Set<String> currFiles = currCommit.getFilenames();
        Set<String> checkoutFiles = checkoutCommit.getFilenames();
        //if a working file is untracked in the current branch that would be overwritten, stop and notify the user
        for (String filename : plainFilenamesIn(CWD)) {
            if (!currFiles.contains(filename) && checkoutFiles.contains(filename)) {
                //TODO: check that the file would actually be overwitten
                System.out.println("There is an untracked file in the way; delete it or add and commit it first.");
                return;
            }
        }
        for (String filename : checkoutFiles) {
            if (currFiles.contains(filename)) {
                currFiles.remove(filename);
            }
            //TODO: method this?
            File currFile = checkoutCommit.getFile(filename);
            writeContents(join(CWD, filename), readContents(currFile));
        }
        for (String filename : currFiles) {
            remove(filename);
        }
        emptyStagingAreas();
        currBranch = branchName;
        head = branches.get(branchName);
        return;
    }

    /** creates a branch with a given name and points it at the head commit */
    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.put(branchName, head);
    }

}
