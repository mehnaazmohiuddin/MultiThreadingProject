import java.util.Scanner;

public class Main {
    static TroubleMaker t ;
    public static void main(String[] args) {
        long pid = ProcessHandle.current().pid();
        System.out.println("PID: " + pid);

        t = new TroubleMaker();
        t.makeDeadLock();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Press something to exit main");
        // Read the entire line of input from the user
        String name = scanner.nextLine();
    }
}
