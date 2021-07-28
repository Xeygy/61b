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
    /** the sha1 hash of this commit's parent.
     * access the parent commit by deserializing the file at location
     * .gitlet/commits/parent
     */
    private String parent;
    private HashMap files; //Key filename, Value hash

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
        //checks for files that aren't staged in CWD, looks at parent for a blob corresponding to that file
        Commit parentCommit = this.getParent();
        for(String filename : Utils.plainFilenamesIn(Repository.CWD)) {
            if (!files.containsKey(filename) && parentCommit.files.containsKey(filename)) {
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

    public Commit getParent() {
        Commit c = readObject(join(Repository.COMMITS_DIR, parent), Commit.class);
        return c;
    }

}
