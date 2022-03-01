package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Heliannl
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  init -- Creates a new Gitlet version-control system in the current directory.
     *  add [file name] -- Adds a copy of the file as it currently exists to the
     *                     staging area.
     *  commit [message] -- Saves a snapshot of tracked files in the current commit
     *                      and staging area, so they can be restored at a later time,
     *                      creating a new commit.
     * @param args arguments from the command line
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        String text;
        switch(firstArg) {
            case "init":
                Repository.setupPersistence();
                break;
            case "add":
                validateNumArgs(args, 2);
                text = args[1];
                Repository.add();
                break;
            case "commit":
                validateNumArgs(args, 2);
                text = args[1];
                break;
            case "rm":
            case "log":
            case "global-log":
            case "find":
            case "status":
            case "checkout":
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
