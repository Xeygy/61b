package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.List;

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
    private Commit parent;
    private String hash;
    private HashMap files; //Key filename, Value hash

    /** for creating a commit after the first one, requires a parent */
    //TODO: Remove "File stagedir" in constructor when done
    public Commit(String message, Commit parent, File stagedir) {
        this.message = message;
        this.parent = parent;
        this.date = new Date(); //current date
        List<String> stagedFileNames = Utils.plainFilenamesIn(stagedir);
        //copies the STAGED files to BLOB_DIR, rename as hashname
        for (String filename : stagedFileNames) {
            File stagedFile = join(Repository.STAGING_DIR, filename);
            byte[] fileContents = readContents(stagedFile);
            String hashname = sha1(fileContents);
            writeContents(join(Repository.BLOB_DIR, hashname), fileContents);
            files.put(filename, hashname);
        }
        //checks for files that aren't staged in CWD, looks at parent for a blob corresponding to that file
        //TODO: don't need parent != null because this is not for the first commit
        for(String filename : Utils.plainFilenamesIn(Repository.CWD)) {
            if (!files.containsKey(filename) && parent.files.containsKey(filename)) {
                files.put(filename, parent.files.get(filename));
            }
        }
        hash = Utils.sha1(this);
    }
    /** for creating a commit after the first one, requires a parent */
    public Commit(String message, Commit parent) {
        this.message = message;
        this.parent = parent;
        this.date = new Date(); //current date
        hash = Utils.sha1(this);
    }
    /** for creating the initial commit, which has no parent */
    public Commit(String message) {
        this.message = message;
        this.parent = null;
        this.date = new Date(0); //0 seconds after the epoch
        this.files = new HashMap();
        hash = hash();
    }
    /** returns a sha1 hash for the object after being serialized*/
    private String hash() {
        byte [] thisAsByte = Utils.serialize(this);
        return Utils.sha1(thisAsByte);
    }
    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        return parent;
    }

    public String getHash() {
        return hash;
    }

    public void addFile(File stagedFile) {

    }
}
