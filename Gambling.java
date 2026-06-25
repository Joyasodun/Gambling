import java.util.*;

public class Gambling {

    // Standard roulette red numbers (everything else 1-36 is black, 0 is green)
    private static final Set<Integer> RED_NUMBERS = new HashSet<>(Arrays.asList(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36));

    // Keeps the last 10 blackjack outcomes (WIN / LOSE / TIE), oldest first.
    private static final int HISTORY_LIMIT = 10;
    private static final Deque<String> blackjackHistory = new ArrayDeque<>();

    // Adds an outcome to the history, dropping the oldest once we exceed 10.
    private static void recordBlackjackResult(String outcome) {
        blackjackHistory.addLast(outcome);
        while (blackjackHistory.size() > HISTORY_LIMIT) {
            blackjackHistory.removeFirst();
        }
    }

    private static void printBlackjackHistory() {
        if (blackjackHistory.isEmpty()) {
            System.out.println("History: (no hands played yet)");
        } else {
            System.out.println("History: " + blackjackHistory);
        }
    }

    // =========================
    // INPUT HELPERS
    // =========================

    // Reads an int, re-prompting until the user actually types one.
    public static int readInt(Scanner scan, String prompt) {
        System.out.println(prompt);
        while (!scan.hasNextInt()) {
            scan.next(); // discard the bad token
            System.out.println("Please enter a whole number.");
            System.out.println(prompt);
        }
        int value = scan.nextInt();
        scan.nextLine(); // consume the rest of the line
        return value;
    }

    public static int readPositiveInt(Scanner scan, String prompt) {
        int value;
        do {
            value = readInt(scan, prompt);
            if (value <= 0) {
                System.out.println("Amount must be greater than 0.");
            }
        } while (value <= 0);
        return value;
    }

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
        System.out.println("\nFinal Balance: $" + balance);

        if (balance <= 0) {
            System.out.println("GAME OVER! You ran out of money.");
        }
    }

    // =========================
    // MAIN MENU
    // =========================

    public static int getMenuChoice(Scanner scan) {
        System.out.println("\n===== CASINO MENU =====");
        System.out.println("0 = Deposit");
        System.out.println("1 = Withdraw");
        System.out.println("2 = Casino Games");
        System.out.println("3 = Exit");

        return readInt(scan, "Choose an option:");
    }

    // =========================
    // GAME MENU
    // =========================

    public static int getGameChoice(Scanner scan) {
        System.out.println("\n===== CHOOSE A GAME =====");
        System.out.println("1 = Blackjack");
        System.out.println("2 = Roulette");

        return readInt(scan, "Choose a game:");
    }

    // =========================
    // BLACKJACK
    // =========================

    public static int playBlackjack(Scanner scan, Random rand, int balance)
            throws InterruptedException {

        int totalChange = 0;

        while (true) {

            // Stop if the player can no longer afford to bet.
            if (balance + totalChange <= 0) {
                System.out.println("\nYou're out of money. Leaving the blackjack table.");
                break;
            }

            int bet = readPositiveInt(scan, "\nEnter bet amount:");

            if (bet > balance + totalChange) {
                System.out.println("You can't bet more than your balance ($"
                        + (balance + totalChange) + ").");
                continue;
            }

            // A hand tracks its total plus how many aces are still counted as 11,
            // so we can demote them to 1 if the total busts.
            // Re-deal the opening hand until it totals 20 or less (no instant 21).
            int[] player;
            do {
                player = newHand();
                addCard(player, drawCard(rand));
                addCard(player, drawCard(rand));
            } while (player[0] > 20);

            int[] cpu = newHand();
            addCard(cpu, drawCard(rand));
            addCard(cpu, drawCard(rand));

            System.out.print("\nDealing cards");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println();

            System.out.println("Your starting total: " + player[0]);

            boolean busted = false;
            while (player[0] < 21) {
                System.out.println("Type HIT or STAND:");
                String choice = scan.nextLine().trim().toUpperCase();

                if (choice.equals("HIT")) {
                    int card = drawCard(rand);
                    addCard(player, card);

                    System.out.println("You drew: " + (card == 11 ? "Ace" : card));
                    System.out.println("New total: " + player[0]);

                    if (player[0] > 21) {
                        System.out.println("BUST!");
                        busted = true;
                        break;
                    }
                } else if (choice.equals("STAND")) {
                    break;
                } else if (choice.equals("HISTORY")) {
                    printBlackjackHistory();
                } else {
                    System.out.println("Invalid input.");
                }
            }

            if (busted) {
                totalChange -= bet;
                recordBlackjackResult("LOSE");
            } else {
                // Dealer draws until reaching at least 17.
                while (cpu[0] < 17) {
                    addCard(cpu, drawCard(rand));
                }

                System.out.println("\nCPU total: " + cpu[0]);

                String result = determineWinner(player[0], cpu[0]);
                System.out.println(result);

                if (result.equals("PLAYER WINS")) {
                    totalChange += bet;
                    recordBlackjackResult("WIN");
                } else if (result.equals("CPU WINS")) {
                    totalChange -= bet;
                    recordBlackjackResult("LOSE");
                } else {
                    recordBlackjackResult("TIE");
                }
            }

            System.out.println("Table balance: $" + (balance + totalChange));

            // Let the player deal again, view their history, or leave.
            String again;
            while (true) {
                System.out.println("\nWhat next?");
                System.out.println("  Type DEAL to play again");
                System.out.println("  Type HISTORY to see your past results");
                System.out.println("  Type anything else to leave the table");
                again = scan.nextLine().trim().toUpperCase();
                if (again.equals("HISTORY")) {
                    printBlackjackHistory();
                } else {
                    break;
                }
            }
            if (!again.equals("DEAL")) {
                break;
            }
        }

        return totalChange;
    }

    // hand[0] = total, hand[1] = number of aces currently counted as 11
    private static int[] newHand() {
        return new int[] {0, 0};
    }

    private static void addCard(int[] hand, int card) {
        hand[0] += card;
        if (card == 11) {
            hand[1]++;
        }
        // Demote aces from 11 to 1 while busting and we still have one to demote.
        while (hand[0] > 21 && hand[1] > 0) {
            hand[0] -= 10;
            hand[1]--;
        }
    }

    // Returns 2-10 for number/face cards, or 11 for an Ace.
    private static int drawCard(Random rand) {
        int card = rand.nextInt(13) + 1; // 1..13

        if (card == 1) {
            return 11; // Ace
        }
        if (card >= 10) {
            return 10; // 10, Jack, Queen, King
        }
        return card; // 2..9
    }

    private static String determineWinner(int player, int cpu) {
        if (player > 21)
            return "CPU WINS";
        if (cpu > 21)
            return "PLAYER WINS";
        if (player > cpu)
            return "PLAYER WINS";
        if (cpu > player)
            return "CPU WINS";
        return "TIE";
    }

    // =========================
    // ROULETTE
    // =========================

    public static int playRoulette(Scanner scan, Random rand, int balance)
            throws InterruptedException {

        int totalChange = 0;

        while (true) {

            // Stop if the player can no longer afford to bet.
            if (balance + totalChange <= 0) {
                System.out.println("\nYou're out of money. Leaving the roulette table.");
                break;
            }

            System.out.println("\nBet Types:");
            System.out.println("RED");
            System.out.println("BLACK");
            System.out.println("ODD");
            System.out.println("EVEN");

            String betType;
            while (true) {
                System.out.println("Enter bet type:");
                betType = scan.nextLine().trim().toUpperCase();
                if (betType.equals("RED") || betType.equals("BLACK")
                        || betType.equals("ODD") || betType.equals("EVEN")) {
                    break;
                }
                System.out.println("Invalid bet type.");
            }

            int amount = readPositiveInt(scan, "Enter bet amount:");

            if (amount > balance + totalChange) {
                System.out.println("You can't bet more than your balance ($"
                        + (balance + totalChange) + ").");
                continue;
            }

            System.out.print("\nSpinning");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println();

            int number = rand.nextInt(37); // 0..36

            String color;
            if (number == 0) {
                color = "GREEN";
            } else if (RED_NUMBERS.contains(number)) {
                color = "RED";
            } else {
                color = "BLACK";
            }

            System.out.println("\nResult: " + number + " " + color);

            boolean won = false;
            if (betType.equals("RED") && color.equals("RED")) {
                won = true;
            } else if (betType.equals("BLACK") && color.equals("BLACK")) {
                won = true;
            } else if (betType.equals("ODD") && number != 0 && number % 2 == 1) {
                won = true;
            } else if (betType.equals("EVEN") && number != 0 && number % 2 == 0) {
                won = true;
            }

            if (won) {
                System.out.println("YOU WIN $" + amount);
                totalChange += amount;
            } else {
                System.out.println("YOU LOSE $" + amount);
                totalChange -= amount;
            }

            System.out.println("Table balance: $" + (balance + totalChange));

            System.out.println("\nSpin again? Type YES to keep playing, anything else to leave:");
            String again = scan.nextLine().trim().toUpperCase();
            if (!again.equals("YES")) {
                break;
            }
        }

        return totalChange;
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args)
            throws InterruptedException {

        Scanner scan = new Scanner(System.in);
        Random rand = new Random();

        int balance = rand.nextInt(400) + 100;

        System.out.println("Starting Balance: $" + balance);

        while (balance > 0) {

            int choice = getMenuChoice(scan);

            // Deposit
            if (choice == 0) {
                int amount = readPositiveInt(scan, "Enter deposit amount:");
                balance = deposit(balance, amount);
                System.out.println("Deposited $" + amount);
            }

            // Withdraw
            else if (choice == 1) {
                int amount = readPositiveInt(scan, "Enter withdraw amount:");
                if (amount > balance) {
                    System.out.println("Not enough money.");
                } else {
                    balance = withdraw(balance, amount);
                    System.out.println("Withdrew $" + amount);
                }
            }

            // Casino Games
            else if (choice == 2) {
                int gameChoice = getGameChoice(scan);

                int result = 0;
                if (gameChoice == 1) {
                    result = playBlackjack(scan, rand, balance);
                } else if (gameChoice == 2) {
                    result = playRoulette(scan, rand, balance);
                } else {
                    System.out.println("Invalid game.");
                }

                balance += result;
                System.out.println("Balance Change: " + result);
            }

            // Exit
            else if (choice == 3) {
                break;
            }

            else {
                System.out.println("Invalid option.");
            }

            System.out.println("Current Balance: $" + balance);
        }

        checkBalance(balance);

        scan.close();
    }
}
