package gitlet;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File MASTER = join(BRANCHES_DIR, "MASTER");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BLOBS_DIR.mkdir();
            COMMITS_DIR.mkdir();
            STAGING_DIR.mkdir();
            TEMP_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            Commit initialCommit = new Commit("initial commit", null, new Date(0));
            String name = sha1(serialize(initialCommit));
            File initialCommitFile = join(COMMITS_DIR, name);
            writeObject(initialCommitFile, initialCommit);
            writeContents(HEAD, name);
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
            File headCommit = join(COMMITS_DIR, readContentsAsString(HEAD));
            Commit currCommit = readObject(headCommit, Commit.class);
            if (currCommit.getSha(fileName)!=null && currCommit.getSha(fileName).equals(currFileSha)) {
                stageFile.delete();
            }
            writeContents(stageFile, currFileSha);
            File realCont = join(TEMP_DIR, currFileSha);
            writeContents(realCont, readContents(addFile));
        }
    }

    private static void clearDir(File dirName) {
        List<String> filesInTemp = plainFilenamesIn(dirName);
        for (String f: filesInTemp) {
            join(dirName,f).delete();
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
        clearDir(TEMP_DIR);
        String name = sha1(serialize(currCommit));
        File CommitFile = join(COMMITS_DIR, name);
        writeObject(CommitFile, currCommit);
        writeContents(HEAD, name);
    }

    public static void rm(String fileName) {
        File rmFile = join(CWD, fileName);
        File stageFile = join(STAGING_DIR,fileName);
        String rmFileSha = sha1(serialize(rmFile));
        String headCommitSha = readContentsAsString(HEAD);
        Commit currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        String fileSha = currCommit.getSha(fileName);
        if (stageFile.exists()) {
            stageFile.delete();
        } else if (fileSha != null && fileSha.equals(rmFileSha)) {
            writeContents(stageFile, rmFileSha);
            File realCont = join(TEMP_DIR, rmFileSha);
            writeContents(realCont, readContents(rmFile));
            if (rmFile.exists()) {
                restrictedDelete(rmFile);
            }
        } else {
            System.out.println("No reason to remove the file.");
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

    public static void find(String message) {

    }

    public static void status() {
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        String headSha = readContentsAsString(HEAD);
        for (String b : branches) {
            if (readContentsAsString(join(BRANCHES_DIR, b)).equals(headSha)) {
                System.out.println("*"+b);
            } else {
                System.out.println(b);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> files = plainFilenamesIn(STAGING_DIR);
        Collections.sort(files);
        for (String f : files) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutBranch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
        } else if (readContentsAsString(HEAD).equals(readContentsAsString(branch))) {
            System.out.println("No need to checkout the current branch.");
        } else {
            File branchSha = join(COMMITS_DIR, readContentsAsString(branch));
            Commit branchCommit = readObject(branchSha, Commit.class);
            for (Map.Entry<String, String> entry : branchCommit.getFiles().entrySet()) {
                File f = join(CWD, entry.getKey());
                if (f.exists()) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                writeContents(f, readContents(join(BLOBS_DIR, entry.getValue())));
            }
            clearDir(STAGING_DIR);
            writeContents(HEAD,readContentsAsString(join(GITLET_DIR, branchName)));
        }
    }

    private static void checkoutHelper(String commitSha, String fileName) {
        Commit currCommit = readObject(join(COMMITS_DIR, commitSha), Commit.class);
        String fileSha = currCommit.getSha(fileName);
        if (fileSha == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            writeContents(join(CWD, fileName),readContents(join(BLOBS_DIR,fileSha)));
        }
    }
    public static void checkoutFile(String fileName) {
        String headCommitSha = readContentsAsString(HEAD);
        checkoutHelper(headCommitSha, fileName);
    }

    public static void checkout(String commitId, String fileName) {
        if (!join(COMMITS_DIR, commitId).exists()) {
            System.out.println("No commit with that id exists.");
        } else {
            checkoutHelper(commitId, fileName);
        }
    }

    public static void branch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            writeContents(branch, readContentsAsString(HEAD));
            writeContents(MASTER, readContentsAsString(HEAD));
        }
    }
}
