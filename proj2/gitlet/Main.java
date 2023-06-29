package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Fei Pan
 */
public class Main {

    /**
     * Check whether the number of arguments is legal.
     * It is notable that what is checked is not length
     * of the array of arguments, but number of arguments
     * following the first argument, i.e. {@code args.length-1}.
     *
     * @param args    array of arguments
     * @param numbers array of numbers allowed
     */
    private static void checkArgs(String[] args, int[] numbers) {
        for (int i : numbers)
            if (args.length == i + 1) {
                return;
            }
        Utils.exit("Incorrect operands.");
    }

    /**
     * Check whether the number of arguments is legal.
     *
     * @param args   array of arguments
     * @param number number allowed
     * @see Main#checkArgs(String[], int[])
     */
    private static void checkArgs(String[] args, int number) {
        checkArgs(args, new int[]{number});
    }

    /**
     * Check whether an argument is as expected.
     *
     * @param arg      argument to check
     * @param expected expected argument
     */
    private static void checkArg(String arg, String expected) {
        if (!arg.equals(expected)) {
            Utils.exit("Incorrect operands.");
        }
    }

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exit("Please enter a command.");
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !Repository.GITLET_DIR.exists()) {
            Utils.exit("Not in an initialized Gitlet directory.");
        }
        /**
         * TODO: Add argument checking for commands
         */
        switch (firstArg) {
            case "init":
                checkArgs(args, 0);
                Repository.init();
                break;
            case "add":
                checkArgs(args, 1);
                Repository.add(args[1]);
                break;
            case "commit":
                checkArgs(args, 1);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArgs(args, 1);
                Repository.rm(args[1]);
                break;
            case "log":
                checkArgs(args, 0);
                Repository.log();
                break;
            case "global-log":
                checkArgs(args, 0);
                Repository.globalLog();
                break;
            case "find":
                checkArgs(args, 1);
                Repository.find(args[1]);
                break;
            case "status":
                checkArgs(args, 0);
                Repository.status();
                break;
            case "checkout":
                checkArgs(args, new int[]{1, 2, 3});
                if (args.length == 2)
                    Repository.checkoutBranch(args[1]);
                else if (args.length == 3) {
                    checkArg(args[1], "--");
                    Repository.checkoutFile(
                            Repository.getBranch(Repository.getRef("HEAD")),
                            args[2]
                    );
                } else {
                    checkArg(args[2], "--");
                    Repository.checkoutFile(args[1], args[3]);
                }
                break;
            case "branch":
                checkArgs(args, 1);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkArgs(args, 1);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                checkArgs(args, 1);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkArgs(args, 1);
                Repository.merge(args[1]);
                break;
            default:
                Utils.exit("No command with that name exists.");
        }
    }
}
