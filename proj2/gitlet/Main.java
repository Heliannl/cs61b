package gitlet;

import java.util.Date;

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
        System.out.println(new Date());
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        String message;
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                message = args[1];
                Repository.add(message);
                break;
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    message = args[1];
                    Repository.commit(message);
                }
                break;
            case "rm":
                message = args[1];
                Repository.rm(message);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find();
                break;
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

}
