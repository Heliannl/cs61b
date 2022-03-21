package gitlet;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;
import static gitlet.Utils.readContents;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.readObject;
import static gitlet.Utils.restrictedDelete;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeContents;
import static gitlet.Utils.writeObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Represents a gitlet repository.
 *  @author Heliannl
 */

public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File STAGED_DIR = join(GITLET_DIR, "staged");
    public static final File REMOVED_DIR = join(GITLET_DIR, "removed");
    public static final File UNTRACKED_DIR = join(GITLET_DIR, "untracked");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File ACTIVE_B = join(GITLET_DIR, "activeB");
    public static final List<String> IGNORE_FILES =
            Arrays.asList("Makefile", "gitlet-design.md", "pom.xml");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system"
                    + " already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BLOBS_DIR.mkdir();
            COMMITS_DIR.mkdir();
            TEMP_DIR.mkdir();
            STAGED_DIR.mkdir();
            REMOVED_DIR.mkdir();
            UNTRACKED_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            Commit initialCommit = new Commit("initial commit", null, new Date(0));
            String commitSha = sha1(serialize(initialCommit));
            writeObject(join(COMMITS_DIR, commitSha), initialCommit);
            writeContents(HEAD, commitSha);
            writeContents(ACTIVE_B, "master");
            writeContents(join(BRANCHES_DIR, readContentsAsString(ACTIVE_B)), commitSha);
        }
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            String currFileSha = sha1(readContents(addFile));
            File headCommitFile = join(COMMITS_DIR, readContentsAsString(HEAD));
            Commit headCommit = readObject(headCommitFile, Commit.class);
            File sFile = join(STAGED_DIR, fileName);
            String temp = headCommit.getSha(fileName);
            if (temp != null && temp.equals(currFileSha)) {
                if (sFile.exists() && sFile.isFile()) {
                    sFile.delete();
                }
                File rFile = join(REMOVED_DIR, fileName);
                if (rFile.exists() && rFile.isFile()) {
                    rFile.delete();
                }
            } else {
                writeContents(sFile, currFileSha);
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

    private static Commit commitHelper(String message) {
        String headCommitSha = readContentsAsString(HEAD);
        File headCommitFile = join(COMMITS_DIR, headCommitSha);
        Commit headCommit = readObject(headCommitFile, Commit.class);
        List<String> files = plainFilenamesIn(STAGED_DIR);
        for (String f : files) {
            String fSha = readContentsAsString(join(STAGED_DIR, f));
            headCommit.addCommit(f, fSha);
            File b = join(BLOBS_DIR, fSha);
            if (!b.exists()) {
                writeContents(b, readContents(join(TEMP_DIR, fSha)));
            }
        }
        files = plainFilenamesIn(REMOVED_DIR);
        for (String f : files) {
            String fSha = readContentsAsString(join(REMOVED_DIR, f));
            headCommit.removeCommit(f, fSha);
        }
        clearDir(STAGED_DIR);
        clearDir(REMOVED_DIR);
        clearDir(TEMP_DIR);
        headCommit.update(headCommitSha, message, new Date());
        return headCommit;
    }

    private static void commit(String message, String mergeParent) {
        if (STAGED_DIR.list().length == 0 && REMOVED_DIR.list().length == 0) {
            System.out.println("No changes added to the commit.");
        } else {
            Commit headCommit = commitHelper(message);
            headCommit.addMergeParent(mergeParent);
            String curCommitSha = sha1(serialize(headCommit));
            writeObject(join(COMMITS_DIR, curCommitSha), headCommit);
            writeContents(HEAD, curCommitSha);
            writeContents(join(BRANCHES_DIR, readContentsAsString(ACTIVE_B)), curCommitSha);
        }
    }

    public static void commit(String message) {
        if (STAGED_DIR.list().length == 0 && REMOVED_DIR.list().length == 0) {
            System.out.println("No changes added to the commit.");
        } else {
            Commit headCommit = commitHelper(message);
            String curCommitSha = sha1(serialize(headCommit));
            writeObject(join(COMMITS_DIR, curCommitSha), headCommit);
            writeContents(HEAD, curCommitSha);
            writeContents(join(BRANCHES_DIR, readContentsAsString(ACTIVE_B)), curCommitSha);
        }
    }

    public static void rm(String fileName) {
        File stageFile = join(STAGED_DIR, fileName);
        if (stageFile.exists() && stageFile.isFile()) {
            stageFile.delete();
        } else {
            String headCommitSha = readContentsAsString(HEAD);
            Commit currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
            String fileSha = currCommit.getSha(fileName);
            if (fileSha != null) {
                writeContents(join(REMOVED_DIR, fileName), fileSha);
                if (join(CWD, fileName).exists()) {
                    restrictedDelete(join(CWD, fileName));
                }
            } else {
                System.out.println("No reason to remove the file.");
            }
        }
    }

    public static void log() {
        if (!HEAD.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String headCommitSha = readContentsAsString(HEAD);
        Commit currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        while (true) {
            System.out.println("===");
            System.out.println("commit " + headCommitSha);
            String mergeParSha = currCommit.getMergeParent();
            headCommitSha = currCommit.getParent();
            withMergeParent(currCommit, mergeParSha, headCommitSha);
            if (headCommitSha == null) {
                break;
            }
            currCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        }
    }

    public static void globalLog() {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String c : commits) {
            System.out.println("===");
            System.out.println("commit " + c);
            Commit currCommit = readObject(join(COMMITS_DIR, c), Commit.class);
            String mergeParSha = currCommit.getMergeParent();
            String headCommitSha = currCommit.getParent();
            withMergeParent(currCommit, mergeParSha, headCommitSha);
        }
    }

    private static void withMergeParent(Commit currC, String mParSha, String hCommitSha) {
        if (mParSha != null) {
            System.out.println("Merge: " + mParSha.substring(0, 7)
                    + " " + hCommitSha.substring(0, 7));
        }
        System.out.println("Date: " + currC.getTimestamp());
        System.out.println(currC.getMessage());
        System.out.println();
    }

    public static void find(String message) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        boolean flag = false;
        for (String c : commits) {
            Commit currCommit = readObject(join(COMMITS_DIR, c), Commit.class);
            if (currCommit.getMessage().equals(message)) {
                flag = true;
                System.out.println(c);
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message");
        }
    }

    private static void printFilenamesIn(File dirName) {
        List<String> names = plainFilenamesIn(dirName);
        Collections.sort(names);
        for (String n : names) {
            System.out.println(n);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> listRem(List<String> listA, List<String> listB) {
        HashSet hs1 = new HashSet(listA);
        HashSet hs2 = new HashSet(listB);
        hs1.removeAll(hs2);
        List<String> listC = new ArrayList<>();
        listC.addAll(hs1);
        return listC;
    }

    private static boolean isStaged(String n, String fileSha) {
        File a = join(STAGED_DIR, n);
        if (a.exists()) {
            return true;
        }
        return false;
    }

    private static boolean isTracked(String n, Commit headCommit, String fileSha) {
        String hSha = headCommit.getSha(n);
        if (hSha != null) {
            return true;
        }
        return false;
    }

    private static void untrackedFiles() {
        String headCommitSha = readContentsAsString(HEAD);
        Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        List<String> names = listRem(plainFilenamesIn(CWD), IGNORE_FILES);
        Collections.sort(names);
        for (String n : names) {
            String fileSha = sha1(readContents(join(CWD, n)));
            if (!isStaged(n, fileSha) && !isTracked(n, headCommit, fileSha)) {
                System.out.println(n);
            }
        }
    }

    private static void modifiedFile(String n, String fileSha, Commit headCommit) {
        File a = join(STAGED_DIR, n);
        if (a.exists() && !fileSha.equals(readContentsAsString(a))) {
            System.out.println(n + "(modified)");
        }
        String hSha = headCommit.getSha(n);
        if (!a.exists() && hSha != null && !hSha.equals(fileSha)) {
            System.out.println(n + "(modified)");
        }
    }

    private static void deletedFile(Commit headCommit) {
        List<String> names = plainFilenamesIn(STAGED_DIR);
        Collections.sort(names);
        for (String n : names) {
            if (!join(CWD, n).exists()) {
                System.out.println(n + "(deleted)");
            }
        }
        names = headCommit.getFileNames();
        Collections.sort(names);
        for (String n : names) {
            if (!join(REMOVED_DIR, n).exists() && !join(CWD, n).exists()) {
                System.out.println(n + "(deleted)");
            }
        }
    }


    private static void modificationFiles() {
        String headCommitSha = readContentsAsString(HEAD);
        Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        List<String> names = listRem(plainFilenamesIn(CWD), IGNORE_FILES);
        Collections.sort(names);
        for (String n : names) {
            String fileSha = sha1(readContents(join(CWD, n)));
            modifiedFile(n, fileSha, headCommit);
        }
        deletedFile(headCommit);
    }

    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            System.out.println("=== Branches ===");
            String actBran = readContentsAsString(ACTIVE_B);
            System.out.println("*" + actBran);
            List<String> branches = plainFilenamesIn(join(BRANCHES_DIR));
            Collections.sort(branches);
            for (String b : branches) {
                if (!b.equals(actBran)) {
                    System.out.println(b);
                }
            }
            System.out.println();
            System.out.println("=== Staged Files ===");
            printFilenamesIn(join(STAGED_DIR));
            System.out.println();
            System.out.println("=== Removed Files ===");
            printFilenamesIn(join(REMOVED_DIR));
            System.out.println();
            System.out.println("=== Modifications Not Staged For Commit ===");
            modificationFiles();
            System.out.println();
            System.out.println("=== Untracked Files ===");
            untrackedFiles();
            System.out.println();
        }
    }

    public static void checkoutBranch(String branchName) {
        File b = join(BRANCHES_DIR, branchName);
        if (!b.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (readContentsAsString(ACTIVE_B).equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        untrackedTest();
        String headCommitSha = readContentsAsString(HEAD);
        Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        String branchCommitSha = readContentsAsString(b);
        Commit branchCommit = readObject(join(COMMITS_DIR, branchCommitSha), Commit.class);
        Map<String, String> fInHead = headCommit.getFiles();
        Map<String, String> fInBranch = branchCommit.getFiles();
        List<String> fInHN = headCommit.getFileNames();
        List<String> fInBN = branchCommit.getFileNames();
        for (String file : fInBN) {
            writeContents(join(CWD, file), readContents(join(BLOBS_DIR, fInBranch.get(file))));
        }
        for (String file : fInHN) {
            if (!fInBN.contains(file)) {
                join(CWD, file).delete();
            }
        }
        clearDir(STAGED_DIR);
        writeContents(ACTIVE_B, branchName);
        writeContents(HEAD, branchCommitSha);
    }

    private static void checkoutHelper(String commitSha, String fileName) {
        Commit currCommit = readObject(join(COMMITS_DIR, commitSha), Commit.class);
        String fileSha = currCommit.getSha(fileName);
        if (fileSha == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            writeContents(join(CWD, fileName), readContents(join(BLOBS_DIR, fileSha)));
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
        File b = join(BRANCHES_DIR, branchName);
        if (b.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            String headSha = readContentsAsString(HEAD);
            writeContents(b, headSha);
        }
    }

    public static void rmBranch(String branchName) {
        File b = join(BRANCHES_DIR, branchName);
        if (!b.exists()) {
            System.out.println("A branch with that name does not exists.");
        } else if (readContentsAsString(ACTIVE_B).equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            if (!b.isDirectory()) {
                b.delete();
            }
        }
    }

    private static void untrackedTest() {
        String headCommitSha = readContentsAsString(HEAD);
        Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        List<String> files = plainFilenamesIn(CWD);
        for (String file: files) {
            if (IGNORE_FILES.contains(file)) {
                continue;
            }
            File f = join(CWD, file);
            String hSha = headCommit.getSha(file);
            if (f.exists() && (hSha == null || !(hSha.equals(sha1(readContents(f)))))) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    public static void reset(String commitId) {
        if (!join(COMMITS_DIR, commitId).exists()) {
            System.out.println("No commit with that id exists.");
        }
        untrackedTest();
    }

    private static Set<String> commitParentChain(Commit c) {
        Set<String> parCommitId = new HashSet<>();
        Commit temp = c;
        String id = c.getParent();
        while (id != null) {
            parCommitId.add(id);
            c = readObject(join(COMMITS_DIR, id), Commit.class);
            id = c.getParent();
        }
        id = temp.getMergeParent();
        while (id != null) {
            parCommitId.add(id);
            c = readObject(join(COMMITS_DIR, id), Commit.class);
            id = c.getParent();
        }
        return parCommitId;
    }

    private static String findSplitCommitHelper(Commit head, Commit other) {
        Set<String> headParId = commitParentChain(head);
        String id = other.getParent();
        while (id != null) {
            if (headParId.contains(id)) {
                break;
            }
            other = readObject(join(COMMITS_DIR, id), Commit.class);
            id = other.getParent();
        }
        return id;
    }

    private static List<String> receiveUnionList(List<String> a, List<String> b) {
        List<String> resultList = new ArrayList<>();
        Set<String> aSet = new TreeSet<>(a);
        aSet.addAll(b);
        resultList = new ArrayList<>(aSet);
        return resultList;
    }

    private static boolean isEmpty(File dir) {
        List<String> names = plainFilenamesIn(dir);
        if (names.size() == 0) {
            return true;
        }
        return false;
    }

    private static void mergeConflictedFile(String n, String c, String b) {
        File realCont = join(CWD, n);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(realCont));
            writer.write("<<<<<<< HEAD\n");
            writeFrom(c, writer);
            writer.write("=======\n");
            writeFrom(b, writer);
            writer.write(">>>>>>>\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        add(n);
    }

    private static void writeFrom(String b, BufferedWriter writer) throws IOException {
        if (b != null) {
            BufferedReader rb = new BufferedReader(new FileReader(join(BLOBS_DIR, b)));
            String line = rb.readLine();
            while (line != null) {
                writer.write(line + "\n");
                line = rb.readLine();
            }
        }
    }

    public static void merge(String branchN) {
        boolean flag = false;
        if (!isEmpty(STAGED_DIR) || !isEmpty(REMOVED_DIR)) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!join(BRANCHES_DIR, branchN).exists()) {
            System.out.println("A branch with that name does not exists.");
            System.exit(0);
        }
        if (readContentsAsString(ACTIVE_B).equals(branchN)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        untrackedTest();
        String headCommitSha = readContentsAsString(HEAD);
        Commit headCommit = readObject(join(COMMITS_DIR, headCommitSha), Commit.class);
        String branchCommitSha = readContentsAsString(join(BRANCHES_DIR, branchN));
        Commit branchCommit = readObject(join(COMMITS_DIR, branchCommitSha), Commit.class);
        String splitCommitSha = findSplitCommitHelper(headCommit, branchCommit);
        if (splitCommitSha.equals(branchCommitSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitCommitSha.equals(headCommitSha)) {
            checkoutBranch(branchN);
            System.out.println("Current branch fast-forward.");
            System.exit(0);
        } else {
            Commit splitCommit = readObject(join(COMMITS_DIR, splitCommitSha), Commit.class);
            Map<String, String> fInHead = headCommit.getFiles();
            Map<String, String> fInBranch = branchCommit.getFiles();
            Map<String, String> fInSplit = splitCommit.getFiles();
            List<String> fInHN = headCommit.getFileNames();
            List<String> fInBN = branchCommit.getFileNames();
            List<String> fInSN = splitCommit.getFileNames();
            List<String> allNames = receiveUnionList(receiveUnionList(fInHN, fInBN), fInSN);
            for (String n : allNames) {
                String c = fInHead.get(n);
                String b = fInBranch.get(n);
                String s = fInSplit.get(n);
                if (fInSN.contains(n) && fInBN.contains(n) && fInHN.contains(n)
                        && !b.equals(s) && c.equals(s)) {
                    checkout(branchCommitSha, n);
                    writeContents(join(STAGED_DIR, n), b);
                } else if ((fInSN.contains(n) && fInBN.contains(n) && fInHN.contains(n)
                        && b.equals(s) && !c.equals(s))
                        || (fInSN.contains(n) && fInBN.contains(n) && fInHN.contains(n)
                        && !b.equals(s) && !c.equals(s) && b.equals(c))
                        || ((fInSN.contains(n) && !fInBN.contains(n) && !fInHN.contains(n))
                        || (!fInSN.contains(n) && !fInBN.contains(n) && fInHN.contains(n)))
                        || (fInSN.contains(n) && fInBN.contains(n) && !fInHN.contains(n)
                        && b.equals(s))) {
                    continue;
                } else if (!fInSN.contains(n) && fInBN.contains(n) && !fInHN.contains(n)) {
                    checkout(branchCommitSha, n);
                    writeContents(join(STAGED_DIR, n), b);
                } else if (fInSN.contains(n) && !fInBN.contains(n) && fInHN.contains(n)
                        && c.equals(s)) {
                    writeContents(join(REMOVED_DIR, n), c);
                    join(CWD, n).delete();
                } else if (fInSN.contains(n) && fInBN.contains(n) && fInHN.contains(n)
                    && !b.equals(s) && !c.equals(s) && !b.equals(c)) {
                    mergeConflictedFile(n, c, b);
                    flag = true;
                } else if ((fInSN.contains(n) && fInBN.contains(n) && !fInHN.contains(n)
                        && !b.equals(s))
                        || (fInSN.contains(n) && !fInBN.contains(n) && fInHN.contains(n)
                        && !c.equals(s))
                        || (!fInSN.contains(n) && fInBN.contains(n) && fInHN.contains(n)
                        && !b.equals(c))) {
                    mergeConflictedFile(n, c, b);
                    flag = true;
                }
            }
        }
        commit("Merged " + branchN + " into " + readContentsAsString(ACTIVE_B) + ".");
        if (flag) {
            System.out.println("Encountered a merge conflict.");
        }
    }
}
