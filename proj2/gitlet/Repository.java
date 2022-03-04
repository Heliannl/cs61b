package gitlet;

import java.io.File;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author Heliannl
 */

public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The .gitlet/objects directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File MASTER = join(GITLET_DIR, "MASTER");

    /** Structure:
     * .gitlet/ top level folder for all persistent data
     *  - HEAD
     *  - index
     *  - objects/
     */

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BLOBS_DIR.mkdir();
            COMMITS_DIR.mkdir();
            STAGING_DIR.mkdir();
            TEMP_DIR.mkdir();
            Commit initialCommit = new Commit("initial commit", null, new Date(0));
            String name = sha1(serialize(initialCommit));
            File initialCommitFile = join(COMMITS_DIR, name);
            writeObject(initialCommitFile, initialCommit);
            writeContents(HEAD, name);
            writeContents(MASTER, name);
        }
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            String currFileSha = sha1(serialize(addFile));
            File stageFile = join(STAGING_DIR, fileName);
            /** If the current working version of the file is identical to the version
             * in the current commit, do not stag it to be added, and remove it from the
             * staging area if it is already there. */
            File headCommit = join(COMMITS_DIR, readContentsAsString(HEAD));
            Commit currCommit = readObject(headCommit, Commit.class);
            if (currCommit.getSha(fileName)!=null && currCommit.getSha(fileName).equals(currFileSha)) {
                stageFile.delete();
            }
            /** store current file in the staging area (name: fileName, contents: sha1). */
            writeContents(stageFile, currFileSha);
            File realCont = join(TEMP_DIR, currFileSha);
            writeContents(realCont, readContents(addFile));
        }
    }

    public static void commit(String message) {
        if (plainFilenamesIn(STAGING_DIR) == null) {
            System.out.println("No changes added to the commit.");
            return;
        }
        String headCommitSha = readContentsAsString(HEAD);
        File headCommit = join(COMMITS_DIR, headCommitSha);
        Commit currCommit = readObject(headCommit, Commit.class);
        currCommit.update(headCommitSha, message, new Date());
        List<String> filesInStag = plainFilenamesIn(STAGING_DIR);
        for (String f: filesInStag ) {
            String fSha = readContentsAsString(join(STAGING_DIR,f));
            currCommit.addCommit(f, fSha);
            /** store current file's sha1 in the Blobs dir (name: sha1, contents: fileName's real contents). */
            File blobFile = join(BLOBS_DIR, fSha);
            writeContents(blobFile, readContents(join(TEMP_DIR,fSha)));
            System.out.println(readContents(join(TEMP_DIR,fSha)));
            join(STAGING_DIR,f).delete();
        }
        List<String> filesInTemp = plainFilenamesIn(TEMP_DIR);
        for (String f: filesInTemp) {
            join(TEMP_DIR,f).delete();
        }
        String name = sha1(serialize(currCommit));
        File CommitFile = join(COMMITS_DIR, name);
        writeObject(CommitFile, currCommit);
        writeContents(HEAD, name);
    }

    public static void rm(String fileName) {
        if (join(STAGING_DIR,fileName).exists()) {

        }
    }

    public static void log() {
        String headCommitSha = readContentsAsString(HEAD);
        Commit currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        while(true) {
            System.out.println("===");
            System.out.println("commit " + headCommitSha);
            System.out.println("Date: " + currCommit.getTimestamp());
            System.out.println(currCommit.getMessage());
            System.out.println();
            headCommitSha = currCommit.getParent();
            if (headCommitSha == null) {
                break;
            }
            currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        }
    }

    public static void globalLog() {

    }

    public static void find() {

    }

}
