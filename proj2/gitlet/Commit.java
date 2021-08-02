package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date date;
    /** the sha1 hash of this commit's parent.
     * access the parent commit by deserializing the file at location
     * .gitlet/commits/parent
     */
    String parent;
    private HashMap<String, String> files; //Key filename, Value hash

    /** for creating a commit after the first one, requires a parent */
    //TODO: Remove "File stagedir" in constructor when done
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        this.files = new HashMap();
        this.date = new Date(); //current date
        List<String> stagedFileNames = Utils.plainFilenamesIn(Repository.STAGING_DIR);
        //copies the staged files to BLOB_DIR, rename as hashname
        for (String filename : stagedFileNames) {
            File stagedFile = join(Repository.STAGING_DIR, filename);
            byte[] fileContents = readContents(stagedFile);
            String hashname = sha1(fileContents);
            writeContents(join(Repository.BLOB_DIR, hashname), fileContents);
            files.put(filename, hashname);
        }
        //checks for files that are staged for removal
        HashSet removedFiles = new HashSet();
        for (String filename : Utils.plainFilenamesIn(Repository.REMOVAL_DIR)) {
            removedFiles.add(filename);
        }
        //checks for files that are in the parent commit and adds them
        //unless staged for removal
        Commit parentCommit = this.getParent();
        Set<String> parentFiles = parentCommit.getFiles().keySet();
        for(String filename : parentFiles) {
            if (!files.containsKey(filename) && !removedFiles.contains(filename) &&
                    parentCommit.files.containsKey(filename)) {
                files.put(filename, parentCommit.files.get(filename));
            }
        }
    }

    /** for creating the initial commit, which has no parent */
    public Commit(String message) {
        this.message = message;
        this.parent = null;
        this.date = new Date(0); //0 seconds after the epoch
        this.files = new HashMap();
    }

    public String getMessage() {
        return message;
    }

    /** returns parent Commit. if no parent, return null */
    public Commit getParent() {
        if(parent == null) {
            return null;
        }
        File parentLoc = join(Repository.COMMITS_DIR, parent);
        Commit c = readObject(parentLoc, Commit.class);
        return c;
    }
    //TODO: replace getFiles with getFilenames
    public HashMap getFiles() {
        return files;
    }
    public Set<String> getFilenames() {
        return files.keySet();
    }
    public Date getDate() {
        return date;
    }

    /**prints info for the gitlet log command */
    public void printInfo(String hash) {
        System.out.println("===");
        System.out.println("commit " + hash);
        //TODO: same format as gitlet date
        System.out.println("Date: " + this.getDate());
        System.out.println(this.getMessage());
        System.out.print("\n");
    }
    /** returns a File corresponding to the filename given, if it exists in the current commit
     * otherwise, exits after telling the user */
    public File getFile(String filename) {
        if (!files.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        return join(Repository.BLOB_DIR, files.get(filename));
    }

    //return null if filename doesn't exist
    public String getFileHash(String filename) {
        if (files.containsKey(filename)) {
            return files.get(filename);
        }
        return null;
    }
    //returns null if file does not exist
    public byte[] getFileContents(String filename) {
        if (files.containsKey(filename)) {
            File file = join(Repository.BLOB_DIR, files.get(filename));
            return readContents(file);
        }
        return null;
    }
}
