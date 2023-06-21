package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exit("Please enter a command.");
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !Repository.GITLET_DIR.exists()) {
            Utils.exit("Not in an initialized Gitlet directory.");
        }
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            case "log":
                Repository.log();
                break;
            // TODO: FILL THE REST IN
            default:
                Utils.exit("No command with that name exists.");
        }
    }
}
