import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

// TODO
public class Semester implements Runnable {
    private Timer timer = new Timer();
    private int currentSemester = 1;

    @Override
    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Semester " + currentSemester + " is running.");
                currentSemester++;
                if(currentSemester == 9) {
                    timer.cancel();
                }
            }
        }, 10000, 10000);
    }

    private int getCommandTypeInput() {
        System.out.println("1. Pause semester");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
