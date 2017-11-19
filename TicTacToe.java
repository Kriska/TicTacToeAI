import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacToe {
    static final String AI = "AI";

    static final String PLAYER = "PLAYER";

    public static void main(String[] args) {
        Board b = new Board();
        b.displayBoard();
        while (!b.isGameOver()) {
            System.out.println("Your move: ");
            PointScore userMove = new PointScore(b.scan.nextInt(), b.scan.nextInt());

            b.placeAMove(userMove, 1);
            b.displayBoard();
            if (b.isGameOver())
                break;

            b.alphaBetaMinimax(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, TicTacToe.AI);
            b.placeAMove(b.returnAiMove(), 2);
            b.displayBoard();
        }
        if (b.isWinner(TicTacToe.PLAYER)) {
            System.out.println("WIN!");
        } else if (b.isWinner(TicTacToe.AI)) {
            System.out.println("GAME OVER!");
        } else {
            System.out.println("DRAW!");
        }
    }
}

class PointScore {

    int score;

    int x;

    int y;

    PointScore(int x, int y) {
        this.x = x;
        this.y = y;
    }

    PointScore(int score, int x, int y) {
        this.score = score;
        this.x = x;
        this.y = y;
    }
}

class Board {
    Scanner scan = new Scanner(System.in);

    private int[][] board = new int[3][3];

    private List<PointScore> childrenPointsAndScore = new ArrayList<>();

    private int evaluateBoard() {
        return evaluateBoardBy('-') + evaluateBoardBy('|') + evaluateBoardBy('/') + evaluateBoardBy('\\');
    }

    private int evaluateBoardBy(char direction) { //| by column, - by row, /  by diagonal,\ by second diagonal
        int score = 0;
        for (int i = 0; i < 3; ++i) {
            int X = 0;
            int O = 0;
            for (int j = 0; j < 3; ++j) {
                if (direction == '-') {
                    if (board[i][j] == 1) {
                        X++;
                    } else if (board[i][j] == 2) {
                        O++;
                    }
                } else if (direction == '|') {
                    if (board[j][i] == 1) {
                        X++;
                    } else if (board[j][i] == 2) {
                        O++;
                    }
                }
            }
            score += changeInScore(X, O);
        }
        int X = 0;
        int O = 0;
        if (direction == '/') {
            for (int i = 0; i < 3; i++) {
                if (board[i][i] == 1) {
                    X++;
                } else if (board[i][i] == 2) {
                    O++;
                }
            }
            score += changeInScore(X, O);
        } else if (direction == '\\') {
            for (int i = 2, j = 0; i > -1; --i, ++j) {
                if (board[i][j] == 1) {
                    X++;
                } else if (board[i][j] == 2) {
                    O++;
                }
            }
            score += changeInScore(X, O);
        }
        return score;
    }

    private int changeInScore(int X, int O) {
        int change;
        if (X == 3) {
            change = 100;
        } else if (X == 2 && O == 0) {
            change = 10;
        } else if (X == 1 && O == 0) {
            change = 1;
        } else if (O == 3) {
            change = -100;
        } else if (O == 2 && X == 0) {
            change = -10;
        } else if (O == 1 && X == 0) {
            change = -1;
        } else {
            change = 0;
        }
        return change;
    }

    int alphaBetaMinimax(int alpha, int beta, int depth, String turn) {

        if (beta <= alpha) {
            if (turn.equals(TicTacToe.AI)) {
                return Integer.MIN_VALUE;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        if (isGameOver()) {
            return evaluateBoard();
        }
        if (depth == 0) {
            childrenPointsAndScore.clear();
        }
        List<PointScore> availablePoints = getAvailablePoints();

        if (availablePoints.isEmpty()) {
            return 0;
        }

        int maxValue = Integer.MIN_VALUE;
        int minValue = Integer.MAX_VALUE;

        for (PointScore point : availablePoints) {
            int currentScore = 0;

            if (turn.equals(TicTacToe.PLAYER)) {
                placeAMove(point, 1);
                currentScore = alphaBetaMinimax(alpha, beta, depth + 1, TicTacToe.AI);
                maxValue = Math.max(maxValue, currentScore);
                alpha = Math.max(currentScore, alpha);
            } else if (turn.equals(TicTacToe.AI)) {
                placeAMove(point, 2);
                currentScore = alphaBetaMinimax(alpha, beta, depth + 1, TicTacToe.PLAYER);
                minValue = Math.min(minValue, currentScore);
                beta = Math.min(currentScore, beta);

                if (depth == 0) {
                    childrenPointsAndScore.add(new PointScore(currentScore, point.x, point.y));
                }
            }
            board[point.x][point.y] = 0;

            if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE)
                break;
        }
        return turn.equals(TicTacToe.PLAYER) ? maxValue : minValue;
    }

    boolean isGameOver() {
        return (isWinner(TicTacToe.PLAYER) || isWinner(TicTacToe.AI) || getAvailablePoints().isEmpty());
    }

    private boolean winner(int position) {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == position) || (
                        board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == position)) {
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == position)
                            || (board[0][i] == board[1][i] && board[0][i] == board[2][i]
                            && board[0][i] == position))) {
                return true;
            }
        }
        return false;
    }

    boolean isWinner(String turn) {
        if (turn.equals(TicTacToe.PLAYER)) {
            return winner(1);
        }
        return winner(2);
    }

    private List<PointScore> getAvailablePoints() {
        ArrayList<PointScore> availablePoints = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 0) {
                    availablePoints.add(new PointScore(i, j));
                }
            }
        }
        return availablePoints;
    }

    void placeAMove(PointScore point, int player) {
        board[point.x][point.y] = player;   //PLAYER = 1 for X, AI = 2 for O
    }

    PointScore returnAiMove() {
        int MIN = 100000;
        int best = 1;

        for (int i = 0; i < childrenPointsAndScore.size(); ++i) {
            if (MIN > childrenPointsAndScore.get(i).score) {
                MIN = childrenPointsAndScore.get(i).score;
                best = i;
            }
        }

        return childrenPointsAndScore.get(best);
    }

    void displayBoard() {
        System.out.println();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 1) {
                    System.out.print("X ");
                } else if (board[i][j] == 2) {
                    System.out.print("O ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }
}
