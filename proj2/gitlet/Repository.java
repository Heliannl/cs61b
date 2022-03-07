package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author Heliannl
 */

public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static Map<String, String> stagedF;
    public static Map<String, String> removedF;
    public static Map<String, String> branches;
    public static String activeB;

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BLOBS_DIR.mkdir();
            COMMITS_DIR.mkdir();
            TEMP_DIR.mkdir();
            stagedF = new HashMap<>();
            removedF = new HashMap<>();
            branches = new HashMap<>();
            Commit initialCommit = new Commit("initial commit", null, new Date(0));
            String name = sha1(serialize(initialCommit));
            File initialCommitFile = join(COMMITS_DIR, name);
            writeObject(initialCommitFile, initialCommit);
            writeContents(HEAD, name);
            branches.put("master", name);
            activeB = "master";
        }
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            String currFileSha = sha1(serialize(addFile));
            File headCommit = join(COMMITS_DIR, readContentsAsString(HEAD));
            Commit currCommit = readObject(headCommit, Commit.class);
            if (currCommit.getSha(fileName)!=null && currCommit.getSha(fileName).equals(currFileSha)) {
                if (stagedF.containsKey(fileName)) {
                    stagedF.remove(fileName);
                }
            } else {
                stagedF.put(fileName, currFileSha);
                File realCont = join(TEMP_DIR, currFileSha);
                writeContents(realCont, readContents(addFile));
            }
        }
    }

    private static void clearDir(File dirName) {
        List<String> filesInTemp = plainFilenamesIn(dirName);
        for (String f: filesInTemp) {
            File file = join(dirName, f);
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    public static void commit(String message) {
        if (stagedF.isEmpty()) {
            System.out.println("No changes added to the commit.");
        } else {
            String headCommitSha = readContentsAsString(HEAD);
            File headCommitFile = join(COMMITS_DIR, headCommitSha);
            Commit headCommit = readObject(headCommitFile, Commit.class);
            headCommit.update(headCommitSha, message, new Date());
            for (Map.Entry<String, String> entry : stagedF.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                headCommit.addCommit(key, value);
                File blobFile = join(BLOBS_DIR, value);
                writeContents(blobFile, readContents(join(TEMP_DIR,value)));
            }
            for (Map.Entry<String, String> entry : removedF.entrySet()) {
                headCommit.removeCommit(entry.getKey(), entry.getValue());
            }
            stagedF.clear();
            clearDir(TEMP_DIR);
            String curCommitSha = sha1(serialize(headCommit));
            File CommitFile = join(COMMITS_DIR, curCommitSha);
            writeObject(CommitFile, curCommitSha);
            writeContents(HEAD, curCommitSha);
            branches.put(activeB, curCommitSha);
        }
    }

    public static void rm(String fileName) {
        if (stagedF.containsKey(fileName)) {
            stagedF.remove(fileName);
        } else {
            String headCommitSha = readContentsAsString(HEAD);
            Commit currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
            String fileSha = currCommit.getSha(fileName);
            if (fileSha != null) {
                removedF.put(fileName, fileSha);
                if (join(CWD, fileName).exists()) {
                    restrictedDelete(join(CWD, fileName));
                }
            } else {
                System.out.println("No reason to remove the file.");
            }
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

    private static void sortKey(Map map) {
        Set set = map.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr);
        for(Object key: arr) {
            System.out.println(key);
        }
    }
    public static void status() {
        System.out.println("=== Branches ===");
        String activeBSha = branches.remove(activeB);
        sortKey(branches);
        branches.put(activeB, activeBSha);
        System.out.println();
        System.out.println("=== Staged Files ===");
        sortKey(stagedF);
        System.out.println();
        System.out.println("=== Removed Files ===");
        sortKey(removedF);
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else {
            if (activeB == branchName) {
                System.out.println("No need to checkout the current branch.");
            } else {
                String headCommitSha = readContentsAsString(HEAD);
                Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
                String branchCommitSha = branches.get(branchName);
                Commit branchCommit = readObject(join(COMMITS_DIR, branchCommitSha), Commit.class);
                for (Map.Entry<String, String> entry : branchCommit.getFiles().entrySet()) {
                    String FileName = entry.getKey();
                    File f = join(CWD, FileName);
                    if (f.exists() && (headCommit.getSha(FileName) != sha1(f))) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                    writeContents(f, readContents(join(BLOBS_DIR, entry.getValue())));
                }
                stagedF.clear();
                activeB = branchName;
                writeContents(HEAD,branchCommitSha);
            }
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
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            String headSha = readContentsAsString(HEAD);
            branches.put(branchName, headSha);
        }
    }

    public static void rm_branch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exists.");
        } else if (activeB == branchName) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branches.remove(branchName);
        }
    }
}
