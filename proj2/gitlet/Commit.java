package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  @author Heliannl
 */

public class Commit implements Serializable {
    /** The message of this Commit. */
    private String parent;
    private Date timestamp;
    private String message;
    private Map<String, String> files;

    public Commit(String message, String parent, Date timestamp) {
        this.parent = parent;
        this.message = message;
        this.files = new HashMap<>();
        this.timestamp = timestamp;
    }

    public String getParent() {
        return this.parent;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getFiles() {
        return this.files;
    }

    public void update(String parent, String message, Date timestamp) {
        this.parent = parent;
        this.timestamp = timestamp;
        this.message = message;
    }

    public void addCommit(String fileName, String sha1) {
        files.put(fileName, sha1);
    }

    public String getSha(String fileName) {
        if (files.containsKey(fileName)) {
            return files.get(fileName);
        }
        return null;
    }
}
