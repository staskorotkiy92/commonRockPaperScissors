import java.util.Random;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RockPaperScissors {
    private User user;
    private Computer computer;
    static String[] gameMoves;
    static String hexKey;
    static String hexHmac;

    public int compareMoves(int userMove, int compMove) {
        int movesCount = gameMoves.length;
        int middle = (movesCount) / 2;

        if (userMove == compMove)
            return 0;
        if (compMove > userMove) {
            if (compMove - userMove > middle) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (userMove - compMove > middle) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private class User {
        private Scanner inputScanner;

        public User() {
            inputScanner = new Scanner(System.in);
        }

        public int getMove() {
            System.out.println("Available moves:");
            for (int i = 0; i < gameMoves.length; i++) {
                System.out.println(i + 1 + " - " + gameMoves[i]);
            }
            System.out.println(0 + " - " + "exit");
            System.out.print("ENTER YOUR MOVE! ");
            if (!inputScanner.hasNextInt()) {
                inputScanner = new Scanner(System.in);
                System.out.println("Please enter one of the move numbers shown in the menu!");
                return getMove();
            } else {
                int userInput = Integer.parseInt(inputScanner.nextLine());
                if (userInput == 0)
                    System.exit(0);

                if (userInput > gameMoves.length) {
                    return getMove();
                }
                return userInput - 1;
            }
        }

        public boolean playAgain() {
            System.out.print("Do you want to play again? Y/N: ");
            String userInput = inputScanner.nextLine();
            userInput = userInput.toUpperCase();
            return userInput.charAt(0) == 'Y';
        }
    }

    private class Computer {
        public int getMove() throws NoSuchAlgorithmException {
            String[] moves = gameMoves;
            Random random = new Random();
            int computerMove = random.nextInt(moves.length);
            String mac = hexKey + gameMoves[computerMove];
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] HMAC = digest.digest(mac.getBytes(StandardCharsets.UTF_8));
            hexHmac = bytesToHex(HMAC);
            System.out.println("HMAC:");
            System.out.println(hexHmac);
            return computerMove;
        }
    }

    public RockPaperScissors() {
        user = new User();
        computer = new Computer();
    }

    public void startGame() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        hexKey = bytesToHex(bytes);
        int computerMove = computer.getMove();
        int userMove = user.getMove();
        System.out.println("\nYour move " + gameMoves[userMove] + ".");
        System.out.println("Computer move " + gameMoves[computerMove] + ".\n");
        int compareMoves = compareMoves(userMove, computerMove);
        switch (compareMoves) {
            case 0:
                System.out.println("Tie!");
                break;
            case 1:
                System.out.println(gameMoves[userMove] + " beats " + gameMoves[computerMove] + ". You win!");
                break;
            case -1:
                System.out.println(gameMoves[computerMove] + " beats " + gameMoves[userMove] + ". You loose!");
                break;
        }
        System.out.println("HMAC key: " + hexKey);
        if (user.playAgain())
            startGame();
    }

    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static boolean findDuplicateInArray(String[] arr) {
        int count = 0;
        for (int j = 0; j < arr.length; j++) {
            for (int k = j + 1; k < arr.length; k++) {
                if (arr[j].equals(arr[k])) {
                    count++;
                    if (count != 0)
                        return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        if (args.length < 3) {
            System.out.println("Please enter more game strategies");
            System.exit(0);
        }
        if (args.length % 2 == 0) {
            System.out.println(
                    "The number of game strategies cannot be even. Please, enter odd number of game strategies.");
            System.exit(0);
        }
        if (findDuplicateInArray(args)) {
            System.out.println("The strategies cannot be duplicated");
            System.exit(0);
        }
        RockPaperScissors game = new RockPaperScissors();
        gameMoves = args;
        game.startGame();
    }
}
