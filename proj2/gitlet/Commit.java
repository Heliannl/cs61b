package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  @author Heliannl
 */

public class Commit implements Serializable {
    /** The message of this Commit. */
    private String parent;
    private String timestamp;
    private String message;
    private Map<String, String> files;

    private String utcDateFormat(Date timestamp) {
        String patternStr = "EEE MMM d HH:mm:ss YYYY Z";
        return new SimpleDateFormat(patternStr, Locale.US).format(timestamp);
    }

    public Commit(String message, String parent, Date timestamp) {
        this.parent = parent;
        this.message = message;
        this.files = new HashMap<>();
        this.timestamp = utcDateFormat(timestamp);
    }

    public String getParent() {
        return this.parent;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getFiles() {
        return this.files;
    }

    public List<String> getFileNames() {
        List<String> names = new ArrayList<>();
        for (String key : files.keySet()) {
            names.add(key);
        }
        return names;
    }

    public void update(String parent, String message, Date timestamp) {
        this.parent = parent;
        this.timestamp = utcDateFormat(timestamp);
        this.message = message;
    }

    public void addCommit(String fileName, String sha1) {
        files.put(fileName, sha1);
    }

    public void removeCommit(String fileName, String sha1) { files.remove(fileName, sha1); }

    public String getSha(String fileName) {
        if (files.containsKey(fileName)) {
            return files.get(fileName);
        }
        return null;
    }
}
