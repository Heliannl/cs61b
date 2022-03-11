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
    private String mergeParent;
    private String timestamp;
    private String message;
    private Map<String, String> files;

    private String utcDateFormat(Date t) {
        String patternStr = "EEE MMM d HH:mm:ss YYYY Z";
        return new SimpleDateFormat(patternStr, Locale.US).format(t);
    }

    public Commit(String message, String parent, Date timestamp) {
        this.parent = parent;
        this.message = message;
        this.mergeParent = null;
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

    public String getMergeParent() {
        return this.mergeParent;
    }

    public List<String> getFileNames() {
        List<String> names = new ArrayList<>();
        for (String key : files.keySet()) {
            names.add(key);
        }
        return names;
    }

    public void update(String p, String m, Date t) {
        this.parent = p;
        this.timestamp = utcDateFormat(t);
        this.message = m;
    }

    public void addMergeParent(String mp) {
        this.mergeParent = mp;
    }

    public void addCommit(String fileName, String sha1) {
        files.put(fileName, sha1);
    }

    public void removeCommit(String fileName, String sha1) {
        files.remove(fileName, sha1);
    }

    public String getSha(String fileName) {
        if (files.containsKey(fileName)) {
            return files.get(fileName);
        }
        return null;
    }
}
