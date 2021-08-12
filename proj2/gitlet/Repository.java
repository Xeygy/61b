package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiuyuan Qiu
 */
public class Repository implements Serializable {

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
            System.out.println("No changes added to the commit.");
            return;
        }
        //new commit with parent as current commit
        Commit commit = new Commit(message, head);
        head = commitHash(commit);
        branches.put(currBranch, head);
        commits.add(head);
        writeObject(join(COMMITS_DIR, head), commit);
        emptyStagingAreas();
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
            File stagedFile = join(STAGING_DIR, filename);
            if (stagedFile.exists()) {
                stagedFile.delete();
            }
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
    }
    private void printCommitInfo(Commit c) {
        c.printInfo(commitHash(c));
    }

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
        System.out.println("\n=== Modifications Not Staged For Commit ==="); //TODO: order files in lexographic order (nice to have)
        Commit currCommit = getCommit(head);
        HashSet modifiedFilenames = new HashSet();
        for (String filename : currCommit.getFilenames()) {
            File cwdFile = join(CWD, filename);
            if (!cwdFile.exists()) {
                //gone from commit
                if (!removalArea.contains(filename)) {
                    // not staged for removal
                    System.out.println(filename + " (deleted)");
                    modifiedFilenames.add(filename);
                }
            } else if (!sha1(readContents(currCommit.getFile(filename))).equals(sha1(readContents(cwdFile)))
                && !stagingArea.contains(filename)) {
                //changed from the commited file and not staged
                System.out.println(filename + " (modified)");
                modifiedFilenames.add(filename);
            }
        }
        for (String filename : plainFilenamesIn(STAGING_DIR)) {
            File cwdFile = join(CWD, filename);
            File stagedFile = join(STAGING_DIR, filename);
            if (!cwdFile.exists() && !modifiedFilenames.contains(filename)) {
                System.out.println(filename + " (deleted)");
            } else if (!modifiedFilenames.contains(filename) &&
                    !sha1(readContents(stagedFile)).equals(sha1(readContents(cwdFile)))) {
                System.out.println(filename + " (modified)");
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
        checkout(c, filename);
    }
    public void checkout(String filename) {
        checkout(head, filename);
    }
    private void checkout(Commit c, String filename) {
        File commitedFile = c.getFile(filename);
        byte[] fileContents = readContents(commitedFile);
        writeContents(join(CWD, filename), fileContents);
    }

    /**
     *  takes files in a commit at a given branch and puts them in the working directory
     *  new branch becomes the head
     *  files tracked in the current branch but not the checked out branch are cleared
     *  staging area is cleared unless checked-out is the current branch
     */
    public void checkoutBranch(String branchName, boolean reset) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (!reset && branchName.equals(currBranch)) {
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
                branches.put(currBranch, head);
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
    public void checkoutBranch(String branchName) {
        checkoutBranch(branchName, false);
    }

    /** creates a branch with a given name and points it at the head commit */
    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.put(branchName, head);
    }

    /** removes a branch that you're not currently on
     * does not deletes the commits under the branch
     */
    public void rmBranch(String branchName) {
        if (branchName.equals(currBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        branches.remove(branchName);
     }

     /** checks out a given commit and resets the branch head there */
     public void reset(String commitID) {
        if (!commits.contains(commitID)) {
            System.out.println("No commit with that id exists");
            return;
        }
        branches.put(currBranch, commitID);
        checkoutBranch(currBranch, true);
     }

    public void merge(String branchB) {
         merge(currBranch, branchB);
    }
    public void merge(String branchA, String branchB) {
         /** A is current branch, B is branch to merge */
         if (!stagingArea.isEmpty() || !removalArea.isEmpty()) {
             System.out.print("You have uncommitted changes.");
             return;
         }
         if (branchA.equals(branchB)) {
             System.out.println("Cannot merge a branch with itself");
             return;
         }
         if (!commits.contains(branches.get(branchA)) || !commits.contains(branches.get(branchB))) {
             System.out.print("A branch with that name does not exist.");
             return;
         }
         Commit commitA = getCommit(branches.get(branchA));
         Commit commitB = getCommit(branches.get(branchB));
         Commit splitPoint = findSplitPoint(commitA, commitB);
         if (splitPoint == commitB) {
             System.out.print("Given branch is an ancestor of the current branch.");
             return;
         }
         if (splitPoint == commitA) {
             checkoutBranch(branchB);
             System.out.print("Current branch fast-forwarded");
             return;
         }
         //Actual merging
         boolean mergeConflictOccurred = false;
         Set<String> splitFiles = splitPoint.getFilenames();
         for (String filename : splitFiles) {
             String aHash = commitA.getFileHash(filename);
             String bHash = commitB.getFileHash(filename);
             String splitHash = splitPoint.getFileHash(filename);
             //modified in only one? take the modified;
             //deleted in one/both, unmodified in the other? delete;
             boolean fileAModified = !splitHash.equals(aHash);
             boolean fileBModified = !splitHash.equals(bHash);
             if ((fileAModified && !fileBModified) || (!fileAModified && !fileBModified)) {
                 //both unmodified, or only current branch (commitA) unmodified
                 continue;
             } else if (fileBModified && !fileAModified) {
                 //only commitB unmodified
                 if (bHash == null) {
                     remove(filename);
                 } else {
                     checkout(commitB, filename);
                     add(filename);
                 }
             } else {
                 //file a & b both modified
                 if (modifiedSameWay(aHash, bHash)) {
                     continue;
                 } else {
                     conflict(filename, commitA, commitB);
                     mergeConflictOccurred = true;
                 }
             }
         }
         //added new file in only one version? add to current
         //new file in both versions? conflict
         Set<String> aFiles = commitA.getFilenames();
         Set<String> bFiles = commitB.getFilenames();
         Set<String> newFiles = new HashSet<>();
         for (String filename : aFiles) {
             if (!splitFiles.contains(filename)) {
                 newFiles.add(filename);
             }
         }
         for (String filename : bFiles) {
             if (newFiles.contains(filename)) {
                 conflict(filename, commitA, commitB);
                 mergeConflictOccurred = true;
             } else if (!splitFiles.contains(filename)) {
                 checkout(commitB, filename);
                 add(filename);
             }
         }
         if (mergeConflictOccurred) {
             System.out.println("Encountered a merge conflict");
         }
         String mergeMessage = "Merged " + branchB + " into " + branchA + ".";
         mergeCommit(mergeMessage, branches.get(branchB));
     }
     /** iterate along a and b to find a common split point */
     private Commit findSplitPoint(Commit a, Commit b) {
         HashSet aLog = new HashSet();
         while(a != null) {
             aLog.add(commitHash(a));
             a = a.getParent();
         }
         while (b != null) {
             if (aLog.contains(commitHash(b))) {
                 return b;
             }
             b = b.getParent();
         }
         return null;
     }
     private boolean modifiedSameWay(String aHash, String bHash) {
         if (aHash == null) {
             return bHash == null;
         } else {
             return aHash.equals(bHash);
         }
     }
     private void conflict(String filename, Commit a, Commit b) {
         File file = join(CWD, filename);
         writeContents(file, "<<<<<<< HEAD \n", a.getFileContents(filename), "=======\n", b.getFileContents(filename), ">>>>>>>");
         add(filename);
     }
    public void mergeCommit(String message, String parentHash2) {
        if(stagingArea.isEmpty() && removalArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        //new merge commit with parent as current commit and given second commit
        Commit commit = new MergeCommit(message, head, parentHash2);
        head = commitHash(commit);
        branches.put(currBranch, head);
        commits.add(head);
        writeObject(join(COMMITS_DIR, head), commit);
        emptyStagingAreas();
    }
}
