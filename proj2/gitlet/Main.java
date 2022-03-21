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
        int argsLength = args.length;
        if (argsLength == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                if (argsLength == 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    Repository.commit(args[1]);
                }
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (argsLength == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (argsLength == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                    } else {
                        Repository.checkoutFile(args[2]);
                    }
                } else if (argsLength == 4){
                    if (!args[2].equals("--"))
                        System.out.println("Incorrect operands.");
                    else
                        Repository.checkout(args[1], args[3]);
                }
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

}
