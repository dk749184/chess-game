package Gaming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class ChessGameGUI extends JFrame {
    JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    JPanel controlPanel = new JPanel();
    JButton[][] cells = new JButton[8][8];
    String[][] board = new String[8][8];
    Point selected = null;
    boolean whiteTurn = true;
    String difficulty = "Medium";

    public ChessGameGUI() {
        setTitle("Chess: Human vs Computer");
        setLayout(new BorderLayout());
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();      // Buttons and top controls
        initBoard();   // Piece setup
        drawBoard();   // Draw grid

        setLocationRelativeTo(null);
        setVisible(true);
    }

    void initUI() {
        controlPanel.setLayout(new FlowLayout());

        String[] levels = {"Beginner", "Medium", "Hard"};
        JComboBox<String> difficultyBox = new JComboBox<>(levels);
        difficultyBox.setSelectedItem("Medium");
        difficultyBox.addActionListener(e -> difficulty = (String) difficultyBox.getSelectedItem());

        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(e -> {
            initBoard();
            drawBoard();
        });

        JButton restartBtn = new JButton("Restart");
        restartBtn.addActionListener(e -> {
            selected = null;
            whiteTurn = true;
            initBoard();
            drawBoard();
        });

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose(); // Close chess window
            GameLauncher.main(null); // Re-open launcher
        });

        controlPanel.add(new JLabel("Difficulty:"));
        controlPanel.add(difficultyBox);
        controlPanel.add(startBtn);
        controlPanel.add(restartBtn);
        controlPanel.add(backBtn);

        add(controlPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
    }

    void initBoard() {
        boardPanel.removeAll();
        boardPanel.revalidate();
        boardPanel.repaint();

        String[] backRow = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int i = 0; i < 8; i++) {
            board[0][i] = "b" + backRow[i];
            board[1][i] = "bP";
            board[6][i] = "wP";
            board[7][i] = "w" + backRow[i];
            for (int j = 2; j < 6; j++) board[j][i] = null;
        }
    }

    void drawBoard() {
        boardPanel.removeAll();
        Font font = new Font("Segoe UI Symbol", Font.PLAIN, 32);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = new JButton();
                btn.setFont(font);
                int row = i, col = j;
                btn.addActionListener(e -> handleClick(row, col));
                cells[i][j] = btn;
                boardPanel.add(btn);
            }

        updateUI();
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    void updateUI() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                JButton btn = cells[i][j];
                btn.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                if (board[i][j] == null) {
                    btn.setText("");
                } else {
                    char color = board[i][j].charAt(0);
                    char type = board[i][j].charAt(1);
                    btn.setText(getSymbol(color, type));
                }
            }
    }

    String getSymbol(char color, char type) {
        return switch ("" + color + type) {
            case "wP" -> "♙";
            case "wR" -> "♖";
            case "wN" -> "♘";
            case "wB" -> "♗";
            case "wQ" -> "♕";
            case "wK" -> "♔";
            case "bP" -> "♟";
            case "bR" -> "♜";
            case "bN" -> "♞";
            case "bB" -> "♝";
            case "bQ" -> "♛";
            case "bK" -> "♚";
            default -> "";
        };
    }

    void handleClick(int row, int col) {
        if (!whiteTurn) return;

        if (selected == null) {
            if (board[row][col] != null && board[row][col].startsWith("w")) {
                selected = new Point(row, col);
                cells[row][col].setBackground(Color.YELLOW);
            }
        } else {
            int sr = selected.x, sc = selected.y;
            String piece = board[sr][sc];

            if (isLegalMove(piece, sr, sc, row, col)) {
                board[row][col] = piece;
                board[sr][sc] = null;
                selected = null;
                whiteTurn = false;
                updateUI();
                checkWinner();

                new Timer().schedule(new TimerTask() {
                    public void run() {
                        computerMove();
                    }
                }, 500);
            } else {
                selected = null;
                updateUI();
            }
        }
    }

    boolean isLegalMove(String piece, int sr, int sc, int r, int c) {
        if (piece == null || (board[r][c] != null && board[r][c].charAt(0) == piece.charAt(0))) return false;

        char color = piece.charAt(0);
        char type = piece.charAt(1);
        int dir = color == 'w' ? -1 : 1;

        return switch (type) {
            case 'P' -> (sc == c && board[r][c] == null &&
                    ((r == sr + dir) || ((color == 'w' && sr == 6 || color == 'b' && sr == 1) &&
                            r == sr + 2 * dir && board[sr + dir][c] == null))) ||
                    (Math.abs(c - sc) == 1 && r == sr + dir && board[r][c] != null &&
                            board[r][c].charAt(0) != color);
            case 'N' -> Math.abs(r - sr) * Math.abs(c - sc) == 2;
            case 'R' -> (sr == r || sc == c) && isPathClear(sr, sc, r, c);
            case 'B' -> Math.abs(sr - r) == Math.abs(sc - c) && isPathClear(sr, sc, r, c);
            case 'Q' -> (sr == r || sc == c || Math.abs(sr - r) == Math.abs(sc - c)) &&
                    isPathClear(sr, sc, r, c);
            case 'K' -> Math.abs(sr - r) <= 1 && Math.abs(sc - c) <= 1;
            default -> false;
        };
    }

    boolean isPathClear(int sr, int sc, int r, int c) {
        int dr = Integer.compare(r, sr);
        int dc = Integer.compare(c, sc);
        int i = sr + dr, j = sc + dc;
        while (i != r || j != c) {
            if (board[i][j] != null) return false;
            i += dr;
            j += dc;
        }
        return true;
    }

    void computerMove() {
        List<Point[]> moves = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if (piece != null && piece.startsWith("b")) {
                    for (int r = 0; r < 8; r++)
                        for (int c = 0; c < 8; c++)
                            if (isLegalMove(piece, i, j, r, c))
                                moves.add(new Point[]{new Point(i, j), new Point(r, c)});
                }
            }

        if (!moves.isEmpty()) {
            Point[] move;
            if ("Beginner".equals(difficulty)) {
                move = moves.get(0);
            } else if ("Hard".equals(difficulty)) {
                move = Collections.max(moves, Comparator.comparingInt(m ->
                        board[m[1].x][m[1].y] != null ? 10 : 0));
            } else {
                move = moves.get(new Random().nextInt(moves.size()));
            }
            board[move[1].x][move[1].y] = board[move[0].x][move[0].y];
            board[move[0].x][move[0].y] = null;
        }

        whiteTurn = true;
        updateUI();
        checkWinner();
    }

    void checkWinner() {
        boolean whiteKing = false, blackKing = false;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if ("wK".equals(piece)) whiteKing = true;
                if ("bK".equals(piece)) blackKing = true;
            }

        if (!whiteKing) {
            JOptionPane.showMessageDialog(this, "Computer wins! ♚");
            dispose();
            GameLauncher.main(null);
        } else if (!blackKing) {
            JOptionPane.showMessageDialog(this, "You win! ♔");
            dispose();
            GameLauncher.main(null);
        }
    }
}
