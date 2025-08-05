package Gaming;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;

public class LudoGame extends JPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Classic Ludo");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            LudoGame panel = new LudoGame();
            f.add(panel);
            f.setSize(600, 650);
            f.setResizable(false);
            f.setVisible(true);
        });
    }

    protected static final int CELL = 40, BOARD = 15;
    private GameState state = new GameState();
    private JButton rollButton;

    public LudoGame() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(BOARD*CELL, BOARD*CELL + 50));

        // Create roll dice button once
        rollButton = new JButton("Roll Dice");
        rollButton.setBounds(260, 590, 100, 30);
        rollButton.addActionListener(e -> {
            if (state.roll >= 0) {
                JOptionPane.showMessageDialog(this, "You already rolled! Move your token.");
                return;
            }
            state.rollDice();
            repaint();
        });
        setLayout(null);
        add(rollButton);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (state.roll < 0) {
                    JOptionPane.showMessageDialog(LudoGame.this, "Roll dice first!");
                    return;
                }
                state.onClick(e.getX(), e.getY());
                repaint();
            }
        });

        Timer t = new Timer(100, e -> repaint());
        t.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        state.drawTokens((Graphics2D)g);
        state.drawDiceAndTurn(g);
    }

    // Basic board layout: cross-shaped cells
    private void drawBoard(Graphics g) {
        for(int r=0; r<BOARD; r++) for(int c=0; c<BOARD; c++) {
            int x=c*CELL, y=r*CELL;
            if(isPlayableCell(r,c)) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, CELL, CELL);
                if(isStar(r,c)) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(x+CELL/4, y+CELL/4, CELL/2, CELL/2);
                }
                g.setColor(Color.BLACK);
                g.drawRect(x, y, CELL, CELL);
            }
        }
    }

    private boolean isPlayableCell(int r,int c){
        // cross shape playable cells
        return (r>=6 && r<=8) || (c>=6 && c<=8) ||
            (r<6 && c<6 && r==c) ||
            (r>8 && c>8 && r==c) ||
            (r<6 && c>8 && r+c==14) ||
            (r>8 && c<6 && r+c==14);
    }
    private boolean isStar(int r,int c){
        int[][] stars={{1,1},{1,13},{13,1},{13,13},{7,1},{1,7},{13,7},{7,13}};
        for(int[] p:stars) if(p[0]==r&&p[1]==c) return true;
        return false;
    }

    class GameState {
        int current = 0, roll = -1;
        int[] turnsLeftOnSix = {0,0,0,0};
        List<Token> tokens = new ArrayList<>();

        GameState(){
            for(int p=0;p<4;p++) for(int t=0;t<4;t++) tokens.add(new Token(p,t));
        }

        public void onClick(int mx,int my){
            int tx = -1;
            for(int i=0;i<tokens.size();i++){
                Token t=tokens.get(i);
                if(t.contains(mx,my)) tx=i;
            }
            if(tx<0) return;
            if(tokens.get(tx).player != current){
                JOptionPane.showMessageDialog(null,"Not your token!");
                return;
            }
            if(tokens.get(tx).move(roll,this)){
                if(roll==6) turnsLeftOnSix[current]++;
                else {
                    nextTurn();
                }
                roll = -1;
                checkWin();
            }
        }

        public void rollDice(){
            roll = new Random().nextInt(6) + 1;
        }

        private void nextTurn(){
            turnsLeftOnSix[current]=0;
            current=(current+1)%4;
        }

        private void checkWin(){
            int count=0;
            for(Token t:tokens)
                if(t.player==current && t.isHome()) count++;
            if(count==4){
                JOptionPane.showMessageDialog(null,
                    "Player "+(current+1)+" wins!");
                System.exit(0);
            }
        }

        public void drawTokens(Graphics2D g){
            for(Token t: tokens) t.draw(g);
        }

        public void drawDiceAndTurn(Graphics g){
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD,16));
            g.drawString("Player "+(current+1)+"'s turn",10,620);
            if(roll>=0)
                g.drawString("Rolled: "+roll, 200, 620);
            else
                g.drawString("Click 'Roll Dice' to start", 200, 620);
        }
    }

    class Token {
        int player, idx;
        int pos = -1; // -1=home, 0-51=main path, 52-57=home stretch (6 positions per player)

        Token(int p, int i) { player = p; idx = i; }

        public boolean move(int steps, GameState s) {
            if (pos == -1) {
                if (steps == 6) {
                    pos = player * 13; // Starting pos for each player (0,13,26,39)
                    return true;
                }
                return false;
            }

            int newPos = pos + steps;

            if (pos < 52) {
                if (newPos < 52) {
                    pos = newPos;
                } else {
                    // Enter home stretch only if allowed
                    int stepsIntoHome = newPos - 52;
                    if (stepsIntoHome >= 0 && stepsIntoHome < 6) {
                        // Only if the token is at its own home entry point
                        int homeEntry = (player * 13 + 51) % 52;
                        if (pos == homeEntry) {
                            pos = 52 + stepsIntoHome; // home stretch positions 52-57
                        } else {
                            return false; // can't enter home stretch early
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                // Already in home stretch, move only if within 6 steps
                int homeIndex = pos - 52 + steps;
                if (homeIndex < 6) {
                    pos += steps;
                } else {
                    return false;
                }
            }

            capture(s);
            return true;
        }

        private void capture(GameState s) {
            if (pos < 52 && !isStarPos(pos)) {
                for (Token t : s.tokens) {
                    if (t != this && t.pos == pos) {
                        t.pos = -1; // send opponent home
                    }
                }
            }
        }

        public boolean contains(int x, int y) {
            Point p = coord();
            return new Rectangle(p.x, p.y, CELL, CELL).contains(x, y);
        }

        public void draw(Graphics2D g) {
            Point p = coord();
            Color c = switch (player) {
                case 0 -> Color.RED;
                case 1 -> Color.GREEN;
                case 2 -> Color.BLUE;
                default -> Color.YELLOW;
            };
            g.setColor(c);
            g.fillOval(p.x + 5, p.y + 5, CELL - 10, CELL - 10);
            g.setColor(Color.BLACK);
            g.drawOval(p.x + 5, p.y + 5, CELL - 10, CELL - 10);
        }

        public boolean isHome() {
            return pos >= 52 + 5; // last home stretch position = home
        }

        private boolean isStarPos(int pos) {
            Point p = coordForPos(pos);
            return isStar(p.y / CELL, p.x / CELL); // convert pixel to board cell row,col
        }

        private Point coord() {
            if (pos == -1) {
                // Token is at home base (off board)
                int baseX = 0, baseY = 0;
                switch (player) {
                    case 0 -> { baseX = CELL; baseY = CELL; }
                    case 1 -> { baseX = 13 * CELL; baseY = CELL; }
                    case 2 -> { baseX = 13 * CELL; baseY = 13 * CELL; }
                    case 3 -> { baseX = CELL; baseY = 13 * CELL; }
                }
                int offsetX = (idx % 2) * (CELL / 2);
                int offsetY = (idx / 2) * (CELL / 2);
                return new Point(baseX + offsetX, baseY + offsetY);
            }

            return coordForPos(pos);
        }

        // Map pos (0-51) and home stretch pos (52-57) to board pixel coordinates
        private Point coordForPos(int pos) {
            if (pos < 52) {
                // 52 main track positions on the board, clockwise starting from Red start at (6,0)
                // Explicit hardcoded mapping for all 52 positions (row, col):
                // Using your 15x15 board cells.

                int[][] path = {
                    {6,0},{6,1},{6,2},{6,3},{6,4},{6,5},
                    {5,6},{4,6},{3,6},{2,6},{1,6},{0,6},
                    {0,7},{0,8},{1,8},{2,8},{3,8},{4,8},
                    {5,8},{6,9},{6,10},{6,11},{6,12},{6,13},
                    {7,14},{8,14},{8,13},{8,12},{8,11},{8,10},
                    {9,8},{10,8},{11,8},{12,8},{13,8},{14,8},
                    {14,7},{13,7},{12,7},{11,7},{10,7},{9,7},
                    {8,6},{8,5},{8,4},{8,3},{8,2},{8,1}
                };
                int r = path[pos][0];
                int c = path[pos][1];
                return new Point(c * CELL, r * CELL);
            }

            // Home stretch positions 52-57 for each player (6 positions)
            int idx = pos - 52;
            int baseR = 0, baseC = 0;
            switch (player) {
                case 0 -> { baseR = 7; baseC = 1; return new Point(baseC * CELL, (baseR + idx) * CELL); } // Red vertical downwards
                case 1 -> { baseR = 1; baseC = 7; return new Point((baseC + idx) * CELL, baseR * CELL); } // Green horizontal rightwards
                case 2 -> { baseR = 7; baseC = 13; return new Point(baseC * CELL, (baseR - idx) * CELL); } // Blue vertical upwards
                default -> { baseR = 13; baseC = 7; return new Point((baseC - idx) * CELL, baseR * CELL); } // Yellow horizontal leftwards
            }
        }
    }

}
