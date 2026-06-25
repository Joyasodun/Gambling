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

            // Fresh shuffled deck each round, dealt one card at a time.
            // Re-deal the opening hand until it totals 20 or less (no instant 21).
            Deck deck = new Deck(rand);
            List<Card> player = new ArrayList<>();
            do {
                player.clear();
                player.add(deck.deal());
                player.add(deck.deal());
            } while (handTotal(player) > 20);

            List<Card> cpu = new ArrayList<>();
            cpu.add(deck.deal());
            cpu.add(deck.deal());

            System.out.print("\nDealing cards");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println();

            System.out.println("Your hand: " + handToString(player)
                    + "  (total " + handTotal(player) + ")");
            // Dealer shows one card; the other stays face down until your turn ends.
            System.out.println("Dealer shows: " + cpu.get(0).name() + " and a face-down card");

            boolean busted = false;
            while (handTotal(player) < 21) {
                System.out.println("Type HIT or STAND:");
                String choice = scan.nextLine().trim().toUpperCase();

                if (choice.equals("HIT")) {
                    Card card = deck.deal();
                    player.add(card);

                    System.out.println("You drew: " + card.name());
                    System.out.println("Your hand: " + handToString(player)
                            + "  (total " + handTotal(player) + ")");

                    if (handTotal(player) > 21) {
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
                // Reveal the dealer's hand, then hit until reaching at least 17.
                System.out.println("\nDealer's hand: " + handToString(cpu)
                        + "  (total " + handTotal(cpu) + ")");
                while (handTotal(cpu) < 17) {
                    Card card = deck.deal();
                    cpu.add(card);
                    System.out.println("Dealer draws: " + card.name()
                            + "  (total " + handTotal(cpu) + ")");
                }

                System.out.println("Dealer total: " + handTotal(cpu));

                String result = determineWinner(handTotal(player), handTotal(cpu));
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

    // A single playing card: a rank (1=Ace, 11=Jack, 12=Queen, 13=King) and a suit.
    private static class Card {
        final int rank; // 1..13
        final String suit;

        Card(int rank, String suit) {
            this.rank = rank;
            this.suit = suit;
        }

        // Blackjack value: face cards are 10, Ace counts as 11 (demoted later if needed).
        int value() {
            if (rank == 1) {
                return 11; // Ace
            }
            if (rank >= 10) {
                return 10; // 10, Jack, Queen, King
            }
            return rank;
        }

        // e.g. "King of Spades", "7 of Hearts", "Ace of Clubs"
        String name() {
            String rankName;
            switch (rank) {
                case 1:  rankName = "Ace";   break;
                case 11: rankName = "Jack";  break;
                case 12: rankName = "Queen"; break;
                case 13: rankName = "King";  break;
                default: rankName = String.valueOf(rank);
            }
            return rankName + " of " + suit;
        }
    }

    // A real 52-card deck you deal from, so the same card can't appear twice.
    private static class Deck {
        private final List<Card> cards = new ArrayList<>();
        private int next = 0; // index of the next card to deal

        Deck(Random rand) {
            String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
            for (String suit : suits) {
                for (int rank = 1; rank <= 13; rank++) {
                    cards.add(new Card(rank, suit));
                }
            }
            Collections.shuffle(cards, rand);
        }

        // Deal the next card off the top of the deck.
        Card deal() {
            return cards.get(next++);
        }
    }

    // Adds the cards' values, then demotes aces from 11 to 1 as needed to avoid busting.
    private static int handTotal(List<Card> hand) {
        int total = 0;
        int aces = 0;
        for (Card c : hand) {
            total += c.value();
            if (c.rank == 1) {
                aces++;
            }
        }
        while (total > 21 && aces > 0) {
            total -= 10; // count one ace as 1 instead of 11
            aces--;
        }
        return total;
    }

    // e.g. "King of Spades, 7 of Hearts"
    private static String handToString(List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(hand.get(i).name());
        }
        return sb.toString();
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
