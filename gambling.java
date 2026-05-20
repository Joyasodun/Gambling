import java.util.*;

public class gambling {

    // =========================
    // BANK OPERATIONS
    // =========================

    public static int deposit(int balance, int amount) {
        return balance + amount;
    }

    public static int withdraw(int balance, int amount) {
        return balance - amount;
    }

    public static void checkBalance(int balance) {
        System.out.println("Current balance: " + balance);

        if (balance <= 0) {
            System.out.println("GAME OVER! You ran out of money.");
        }
    }

    // =========================
    // MENU
    // =========================

    public static int getReason(Scanner scan) {
        System.out.println("\n0 = deposit, 1 = withdraw, 2 = blackjack, 3 = exit");
        int choice = scan.nextInt();
        scan.nextLine(); // 🔥 FIX: clears newline so Blackjack works correctly
        return choice;
    }

    // =========================
    // BLACKJACK GAME
    // =========================

    public static int playBlackjack(Scanner scan, Random rand) {

        int playerSum = drawCard(rand) + drawCard(rand);
        int cpuSum = drawCard(rand) + drawCard(rand);

        // CPU plays automatically
        while (cpuSum <= 17) {
            cpuSum += drawCard(rand);
        }

        System.out.println("\nYou start off with: " + playerSum);

        // Player turn
        while (playerSum <= 21) {

            System.out.println("Hit me or Stand?");
            String choice = scan.nextLine().trim().toUpperCase();

            if (choice.equals("HIT ME")) {
                playerSum += drawCard(rand);
                System.out.println("Your sum is now: " + playerSum);
            } 
            else if (choice.equals("STAND")) {
                break;
            } 
            else {
                System.out.println("Invalid input.");
            }
        }

        System.out.println("\nCPU had: " + cpuSum);

        String result = determineWinner(playerSum, cpuSum);
        System.out.println(result);

        if (result.contains("PLAYER WINS")) return 100;
        if (result.contains("CPU WINS")) return -100;
        return 0;
    }

    // Draw card (2–11 system)
    private static int drawCard(Random rand) {
        int card = rand.nextInt(13) + 2;

        if (card >= 11 && card <= 13) return 10; // face cards
        if (card == 14) return 11;              // ace

        return card;
    }

    // Determine winner
    private static String determineWinner(int player, int cpu) {
        if (player > 21 && cpu > 21) return "BOTH LOSE";
        if (player == cpu) return "TIE";
        if (player > 21) return "CPU WINS";
        if (cpu > 21) return "PLAYER WINS";
        return player > cpu ? "PLAYER WINS" : "CPU WINS";
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        Random rand = new Random();

        int balance = rand.nextInt(400) + 1;
        System.out.println("You start with: " + balance);

        while (true) {

            int reason = getReason(scan);

            // Deposit
            if (reason == 0) {

                int amount = rand.nextInt(100) + 1;
                balance = deposit(balance, amount);

                System.out.println("Random deposit: +" + amount);
                System.out.println("New balance: " + balance);
            }

            // Withdraw
            else if (reason == 1) {

                int amount = rand.nextInt(100) + 1;

                if (amount > balance) {
                    System.out.println("Random withdraw of " + amount + " failed (not enough money)");
                } 
                else {
                    balance = withdraw(balance, amount);
                    System.out.println("Random withdraw: -" + amount);
                    System.out.println("New balance: " + balance);
                }
            }

            // Blackjack
            else if (reason == 2) {

                int result = playBlackjack(scan, rand);
                balance += result;

                System.out.println("Blackjack change: " + result);
                System.out.println("New balance: " + balance);
            }

            // Exit
            else if (reason == 3) {
                break;
            }

            else {
                System.out.println("Not an option");
            }

            // GAME OVER CHECK (after every action)
            if (balance <= 0) {
                System.out.println("\nGAME OVER! You're broke.");
                break;
            }
        }

        checkBalance(balance);
        scan.close();
    }
}
