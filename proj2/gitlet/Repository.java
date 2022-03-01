package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

/** Represents a gitlet repository.
 *  @author Heliannl
 */

public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The HEAD pointer. */
    public static Commit head;
    /** The Master pointer. */
    public static Commit master;

    /** Structure:
     * .gitlet/ top level folder for all persistent data
     *  - blobs/ -- folder containing all the blobs
     *  - commits/ -- folder containing all the commits
     */

    public static void setupPersistence() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } {
            GITLET_DIR.mkdir();
            Commit initial_commit = new Commit("initial commit", null);
            head = initial_commit;
            master = initial_commit;
        }
    }

    public static void add() {
        sha1();
    }

}
