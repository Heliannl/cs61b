package gitlet;

import java.io.Serializable;

/** Represents a gitlet commit object.
 *  @author Heliannl
 */

public class Commit {
    public class Blobs implements Serializable {
    }

    /** The message of this Commit. */
    private Commit parent;
    private String timestamp;
    private String message;
    private Blobs[] files;

    public Commit(String message, Commit parent) {
        this.parent = parent;
        this.message = message;
        if (this.parent == null) {
            this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        }
    }

    public Commit getParent() {
        return this.parent;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

}
