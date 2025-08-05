package Gaming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLauncher {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Game Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel label = new JLabel("Select a Game to Play", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(label);

        // Game buttons
        JButton chessButton = new JButton("Chess");
        JButton ludoButton = new JButton("Ludo");
        JButton snakeButton = new JButton("Snake");
        JButton ticTacToeButton = new JButton("Tic Tac Toe");

        // Add buttons to the frame
        frame.add(chessButton);
        frame.add(ludoButton);
        frame.add(snakeButton);
        frame.add(ticTacToeButton);

        // Footer
        JLabel footer = new JLabel("© 2025 FunZone Inc", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        frame.add(footer);
       
        chessButton.addActionListener(e -> {
            new ChessGameGUI();  // constructor now works
            frame.dispose();     // close main menu
        });
        
        

//        chessButton.addActionListener(e -> {
//            new ChessGameGUI(); // Call the Chess GUI class
//        });
        // Button actions
       // chessButton.addActionListener(e -> ("Chess"));
        ludoButton.addActionListener(e -> openGameWindow("Ludo"));
        snakeButton.addActionListener(e -> openGameWindow("Snake"));
        ticTacToeButton.addActionListener(e -> openGameWindow("Tic Tac Toe"));

        // Show the main window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to open a new window for a game
    public static void openGameWindow(String gameName) {
        JFrame gameFrame = new JFrame(gameName);
        gameFrame.setSize(300, 200);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel message = new JLabel("Welcome to " + gameName + "!", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 16));
        gameFrame.add(message);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }
}

