package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

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
    //Files

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
        hash = hash();
    }

    /** returns a sha1 hash for the object */
    //TODO: sha1 only takes in byte arrays and strings, figure out how to get unique hashes
    private String hash() {
        return Utils.sha1(message);
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
}
