import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel - Version 10 Final GUI with Profile Management
 * 
 * Features:
 *  - Main Menu
 *  - Profile Selection Screen (Select profile / Create profile with name)
 *  - Live Player Identity tracking & High Scores per profile
 *  - Leaderboard automatically updated with player names and scores via Bubble Sort
 *  - Linear Search by player name
 *  - Game View with Top Stats Header, 20x15 Board, and Bottom Control Bar
 *  - Game Over View with [PLAY AGAIN], [SWITCH PROFILE], [LEADERBOARD], [MAIN MENU]
 *  - How To Play & DSA Guide Modal
 *  - Genuine Call-Stack Recursion Countdown
 */
public class GamePanel extends JPanel implements ActionListener {

    // Grid configuration
    public static final int GRID_WIDTH = 20;   // 20 columns
    public static final int GRID_HEIGHT = 15;  // 15 rows
    public static final int CELL_SIZE = 40;    // 40x40 pixels per cell

    // Layout offsets
    public static final int HEADER_HEIGHT = 44; // Top stats bar
    public static final int FOOTER_HEIGHT = 50; // Bottom control bar
    public static final int TOTAL_WIDTH = GRID_WIDTH * CELL_SIZE;                       // 800 px
    public static final int TOTAL_HEIGHT = HEADER_HEIGHT + (GRID_HEIGHT * CELL_SIZE) + FOOTER_HEIGHT; // 694 px

    // DSA 1: 2D Array representation
    public static final int EMPTY = 0;
    public static final int SNAKE_CELL = 1;
    public static final int FOOD_CELL = 2;

    private int[][] board;

    // DSA 2: Singly Linked List Snake
    private Snake snake;

    // Food Object
    private Food food;

    // DSA 3: Bubble Sort & Leaderboard
    private Leaderboard leaderboard;

    // DSA 4: Recursion Countdown
    private String countdownText = "";

    // Profiles Management
    private List<Profile> profiles = new ArrayList<>();
    private Profile currentProfile = null;

    // Screen States (FSM)
    public enum ScreenState {
        MAIN_MENU,
        SELECT_PROFILE,
        COUNTDOWN,
        PLAYING,
        HOW_TO_PLAY,
        LEADERBOARD,
        GAME_OVER
    }

    private ScreenState currentState = ScreenState.MAIN_MENU;

    // Movement direction
    private int dx = 1; // Initially moving RIGHT
    private int dy = 0;

    // Game stats & difficulty progression
    private static final int INITIAL_SPEED_MS = 140;
    private static final int MIN_SPEED_MS = 50;
    private static final int SPEED_DECREMENT_MS = 10;

    private int currentSpeed = INITIAL_SPEED_MS;
    private int score = 0;
    private int highScore = 0;
    private int foodsEaten = 0;
    private int level = 1;
    private boolean isNewHighScore = false;

    // Game states
    private boolean isPaused = false;
    private String collisionType = "";
    private String collisionDetails = "";

    // Timer for game loop
    private Timer gameTimer;

    // Mouse tracking for hover effects
    private Point mousePoint = null;

    // Interactive Button Bounds
    // Main Menu Buttons
    private final Rectangle menuPlayBtn = new Rectangle(270, 220, 260, 48);
    private final Rectangle menuLeaderboardBtn = new Rectangle(270, 285, 260, 48);
    private final Rectangle menuHowToPlayBtn = new Rectangle(270, 350, 260, 48);
    private final Rectangle menuExitBtn = new Rectangle(270, 415, 260, 48);

    // Profile Selection Buttons
    private final Rectangle profileCreateBtn = new Rectangle(250, 505, 300, 46);
    private final Rectangle profileBackBtn = new Rectangle(290, 565, 220, 42);

    // In-Game Bottom Bar Buttons
    private final Rectangle gamePauseBtn = new Rectangle(280, TOTAL_HEIGHT - FOOTER_HEIGHT + 7, 110, 36);
    private final Rectangle gameRestartBtn = new Rectangle(410, TOTAL_HEIGHT - FOOTER_HEIGHT + 7, 110, 36);

    // Game Over Buttons
    private final Rectangle goPlayAgainBtn = new Rectangle(270, 330, 260, 42);
    private final Rectangle goSwitchProfileBtn = new Rectangle(270, 382, 260, 42);
    private final Rectangle goLeaderboardBtn = new Rectangle(270, 434, 260, 42);
    private final Rectangle goMainMenuBtn = new Rectangle(270, 486, 260, 42);

    // How To Play Back Button
    private final Rectangle howBackBtn = new Rectangle(290, 565, 220, 44);

    // Leaderboard Screen Buttons
    private final Rectangle lbSearchBtn = new Rectangle(200, 565, 180, 44);
    private final Rectangle lbBackBtn = new Rectangle(420, 565, 180, 44);

    public GamePanel() {
        this.setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        this.setBackground(new Color(20, 22, 27));
        this.setFocusable(true);

        board = new int[GRID_HEIGHT][GRID_WIDTH];

        // Initialize Profiles with default players
        initProfiles();

        // Initialize Leaderboard & demonstrate algorithms in terminal
        leaderboard = new Leaderboard();
        leaderboard.demonstrateBubbleSort();
        leaderboard.demonstrateLinearSearch();

        // Keyboard controls
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // Mouse clicks
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        // Mouse motion for hover highlights
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                updateCursor();
                repaint();
            }
        });

        initGameData();

        // Game loop timer
        gameTimer = new Timer(currentSpeed, this);
    }

    /**
     * Initializes default user profiles matching the sample dataset.
     */
    private void initProfiles() {
        profiles.add(new Profile("Damidu", 850));
        profiles.add(new Profile("Amal", 950));
        profiles.add(new Profile("Nimal", 650));
        profiles.add(new Profile("Kasun", 400));
        currentProfile = profiles.get(0); // Default to Damidu
    }

    /**
     * Initializes gameplay structures without launching movement.
     */
    public void initGameData() {
        snake = new Snake(6, 7, 3);
        dx = 1;
        dy = 0;
        isPaused = false;
        collisionType = "";
        collisionDetails = "";
        score = 0;
        foodsEaten = 0;
        level = 1;
        isNewHighScore = false;
        currentSpeed = INITIAL_SPEED_MS;

        if (currentProfile != null) {
            highScore = currentProfile.getHighScore();
        }

        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(currentSpeed);
        }

        food = new Food(GRID_WIDTH, GRID_HEIGHT, snake);
        syncBoard();
    }

    /**
     * Selects a profile and initiates the recursive countdown into gameplay.
     */
    public void selectProfileAndPlay(Profile profile) {
        this.currentProfile = profile;
        this.highScore = profile.getHighScore();
        System.out.println(">> Active Profile Selected: " + currentProfile.getName()
                + " (Personal Best: " + currentProfile.getHighScore() + " pts)");
        startWithRecursiveCountdown();
    }

    /**
     * Triggers the genuine Recursive Countdown (3 -> 2 -> 1 -> GO!) and then starts playing.
     */
    public void startWithRecursiveCountdown() {
        initGameData();
        currentState = ScreenState.COUNTDOWN;
        countdownText = "3";
        repaint();

        RecursionCountdown countdown = new RecursionCountdown(new RecursionCountdown.CountdownListener() {
            @Override
            public void onTick(String text) {
                countdownText = text;
                repaint();
            }

            @Override
            public void onComplete() {
                countdownText = "";
                currentState = ScreenState.PLAYING;
                gameTimer.start();
                System.out.println("--- Round Started for: " + currentProfile.getName() + " ---");
                snake.printLinkedList();
                repaint();
            }
        });

        countdown.start(3);
    }

    /**
     * Synchronizes 2D Array DSA from Linked List & Food.
     */
    private void syncBoard() {
        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {
                board[r][c] = EMPTY;
            }
        }

        if (food != null && food.getY() >= 0 && food.getY() < GRID_HEIGHT
                && food.getX() >= 0 && food.getX() < GRID_WIDTH) {
            board[food.getY()][food.getX()] = FOOD_CELL;
        }

        SnakeNode current = snake.getHead();
        while (current != null) {
            if (current.x >= 0 && current.x < GRID_WIDTH && current.y >= 0 && current.y < GRID_HEIGHT) {
                board[current.y][current.x] = SNAKE_CELL;
            }
            current = current.next;
        }
    }

    /**
     * Updates cursor to hand if hovering over any interactive button or profile card.
     */
    private void updateCursor() {
        if (mousePoint == null) {
            setCursor(Cursor.getDefaultCursor());
            return;
        }

        boolean hoveringButton = false;
        switch (currentState) {
            case MAIN_MENU:
                hoveringButton = menuPlayBtn.contains(mousePoint)
                        || menuLeaderboardBtn.contains(mousePoint)
                        || menuHowToPlayBtn.contains(mousePoint)
                        || menuExitBtn.contains(mousePoint);
                break;
            case SELECT_PROFILE:
                hoveringButton = profileCreateBtn.contains(mousePoint)
                        || profileBackBtn.contains(mousePoint)
                        || getClickedProfileIndex(mousePoint) != -1;
                break;
            case PLAYING:
                hoveringButton = gamePauseBtn.contains(mousePoint) || gameRestartBtn.contains(mousePoint);
                break;
            case GAME_OVER:
                hoveringButton = goPlayAgainBtn.contains(mousePoint)
                        || goSwitchProfileBtn.contains(mousePoint)
                        || goLeaderboardBtn.contains(mousePoint)
                        || goMainMenuBtn.contains(mousePoint);
                break;
            case HOW_TO_PLAY:
                hoveringButton = howBackBtn.contains(mousePoint);
                break;
            case LEADERBOARD:
                hoveringButton = lbSearchBtn.contains(mousePoint) || lbBackBtn.contains(mousePoint);
                break;
            default:
                break;
        }

        setCursor(hoveringButton ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    /**
     * Computes index of profile card clicked on SELECT_PROFILE screen.
     */
    private int getClickedProfileIndex(Point p) {
        int startY = 140;
        int cardH = 50;
        int gap = 10;
        int cardW = 440;
        int cardX = (TOTAL_WIDTH - cardW) / 2;

        for (int i = 0; i < Math.min(profiles.size(), 6); i++) {
            Rectangle cardRect = new Rectangle(cardX, startY + i * (cardH + gap), cardW, cardH);
            if (cardRect.contains(p)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles mouse clicks across all screens.
     */
    private void handleMouseClick(Point p) {
        switch (currentState) {
            case MAIN_MENU:
                if (menuPlayBtn.contains(p)) {
                    currentState = ScreenState.SELECT_PROFILE;
                    repaint();
                } else if (menuLeaderboardBtn.contains(p)) {
                    currentState = ScreenState.LEADERBOARD;
                    repaint();
                } else if (menuHowToPlayBtn.contains(p)) {
                    currentState = ScreenState.HOW_TO_PLAY;
                    repaint();
                } else if (menuExitBtn.contains(p)) {
                    System.exit(0);
                }
                break;

            case SELECT_PROFILE:
                int clickedIndex = getClickedProfileIndex(p);
                if (clickedIndex != -1) {
                    selectProfileAndPlay(profiles.get(clickedIndex));
                } else if (profileCreateBtn.contains(p)) {
                    promptCreateProfile();
                } else if (profileBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case PLAYING:
                if (gamePauseBtn.contains(p)) {
                    isPaused = !isPaused;
                    repaint();
                } else if (gameRestartBtn.contains(p)) {
                    startWithRecursiveCountdown();
                }
                break;

            case GAME_OVER:
                if (goPlayAgainBtn.contains(p)) {
                    startWithRecursiveCountdown();
                } else if (goSwitchProfileBtn.contains(p)) {
                    currentState = ScreenState.SELECT_PROFILE;
                    repaint();
                } else if (goLeaderboardBtn.contains(p)) {
                    currentState = ScreenState.LEADERBOARD;
                    repaint();
                } else if (goMainMenuBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case HOW_TO_PLAY:
                if (howBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case LEADERBOARD:
                if (lbSearchBtn.contains(p)) {
                    performLinearSearch();
                } else if (lbBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            default:
                break;
        }
    }

    /**
     * Prompts the user to create a new profile with their name.
     */
    private void promptCreateProfile() {
        String name = JOptionPane.showInputDialog(
            this,
            "Enter Your Name to Create Profile:",
            "Create New Profile",
            JOptionPane.PLAIN_MESSAGE
        );

        if (name != null && !name.trim().isEmpty()) {
            name = name.trim();

            // Check if profile with same name exists
            for (Profile p : profiles) {
                if (p.getName().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this,
                        "Profile for '" + name + "' already exists! Selecting your profile.",
                        "Existing Profile Found",
                        JOptionPane.INFORMATION_MESSAGE);
                    selectProfileAndPlay(p);
                    return;
                }
            }

            // Create new profile
            Profile newProfile = new Profile(name, 0);
            profiles.add(newProfile);
            System.out.println(">> Created new Profile: " + name);
            selectProfileAndPlay(newProfile);
        }
    }

    /**
     * Handles keyboard controls.
     */
    private void handleKeyPress(int keyCode) {
        if (currentState == ScreenState.MAIN_MENU) {
            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                currentState = ScreenState.SELECT_PROFILE;
                repaint();
            } else if (keyCode == KeyEvent.VK_L) {
                currentState = ScreenState.LEADERBOARD;
                repaint();
            } else if (keyCode == KeyEvent.VK_H) {
                currentState = ScreenState.HOW_TO_PLAY;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.SELECT_PROFILE) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_M) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            } else if (keyCode == KeyEvent.VK_C || keyCode == KeyEvent.VK_N) {
                promptCreateProfile();
            } else if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
                int idx = keyCode - KeyEvent.VK_1;
                if (idx < profiles.size()) {
                    selectProfileAndPlay(profiles.get(idx));
                }
            }
            return;
        }

        if (currentState == ScreenState.HOW_TO_PLAY || currentState == ScreenState.LEADERBOARD) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_M) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            } else if (keyCode == KeyEvent.VK_F && currentState == ScreenState.LEADERBOARD) {
                performLinearSearch();
            }
            return;
        }

        if (currentState == ScreenState.GAME_OVER) {
            if (keyCode == KeyEvent.VK_R || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                startWithRecursiveCountdown();
            } else if (keyCode == KeyEvent.VK_P) {
                currentState = ScreenState.SELECT_PROFILE;
                repaint();
            } else if (keyCode == KeyEvent.VK_L) {
                currentState = ScreenState.LEADERBOARD;
                repaint();
            } else if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.PLAYING) {
            if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_P) {
                isPaused = !isPaused;
                repaint();
                return;
            }

            if (keyCode == KeyEvent.VK_R) {
                startWithRecursiveCountdown();
                return;
            }

            if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
                gameTimer.stop();
                currentState = ScreenState.MAIN_MENU;
                repaint();
                return;
            }

            // Movement controls
            if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) && dy != 1) {
                dx = 0;
                dy = -1;
            } else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) && dy != -1) {
                dx = 0;
                dy = 1;
            } else if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) && dx != 1) {
                dx = -1;
                dy = 0;
            } else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) && dx != -1) {
                dx = 1;
                dy = 0;
            }
        }
    }

    /**
     * Prompts for player name and runs Linear Search algorithm.
     */
    private void performLinearSearch() {
        boolean wasPlaying = (currentState == ScreenState.PLAYING && !isPaused);
        if (wasPlaying) {
            isPaused = true;
            repaint();
        }

        String query = JOptionPane.showInputDialog(
            this,
            "Enter Player Name to Search (Linear Search DSA):",
            "Linear Search Player",
            JOptionPane.QUESTION_MESSAGE
        );

        if (query != null && !query.trim().isEmpty()) {
            LeaderboardEntry found = leaderboard.searchPlayer(query.trim());
            if (found != null) {
                JOptionPane.showMessageDialog(
                    this,
                    "<html><h3 style='color:#27ae60;'>Player Found!</h3>"
                    + "<b>Player:</b> " + found.getName() + "<br>"
                    + "<b>Score:</b> " + found.getScore() + "<br><br>"
                    + "<i>Algorithm: Linear Search O(N) traversal</i></html>",
                    "Linear Search Result",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "<html><h3 style='color:#e74c3c;'>Player Not Found</h3>"
                    + "Player <b>" + query.trim() + "</b> was not found on the leaderboard.<br><br>"
                    + "<i>All entries were checked sequentially via Linear Search.</i></html>",
                    "Linear Search Result",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }

        if (wasPlaying) {
            isPaused = false;
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != ScreenState.PLAYING || isPaused) {
            return;
        }

        SnakeNode head = snake.getHead();
        int nextX = head.x + dx;
        int nextY = head.y + dy;

        // 1. Wall Collision Check
        if (nextX < 0 || nextX >= GRID_WIDTH || nextY < 0 || nextY >= GRID_HEIGHT) {
            triggerCollision("WALL COLLISION", "Out of bounds at (" + nextX + ", " + nextY + ")");
            return;
        }

        boolean willGrow = (nextX == food.getX() && nextY == food.getY());

        // 2. Self Collision Check via 2D Array
        SnakeNode tail = snake.getTail();
        boolean isSteppingOnVacatingTail = (!willGrow && tail != null && tail.x == nextX && tail.y == nextY);

        if (board[nextY][nextX] == SNAKE_CELL && !isSteppingOnVacatingTail) {
            triggerCollision("SELF COLLISION", "Array lookup board[" + nextY + "][" + nextX + "] == SNAKE");
            return;
        }

        // Move Linked List Snake
        snake.move(dx, dy, willGrow);

        // 3. Self Collision Check via Linked List
        if (snake.checkSelfCollision()) {
            triggerCollision("SELF COLLISION", "Linked List traversal detected overlap");
            return;
        }

        // Food eaten
        if (willGrow) {
            score += food.getPoints();
            if (currentProfile != null && score > currentProfile.getHighScore()) {
                isNewHighScore = true;
                highScore = score;
            }
            foodsEaten++;

            // Increase difficulty
            if (foodsEaten % 2 == 0 && currentSpeed > MIN_SPEED_MS) {
                currentSpeed = Math.max(MIN_SPEED_MS, currentSpeed - SPEED_DECREMENT_MS);
                gameTimer.setDelay(currentSpeed);
                level++;
            }

            food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
            snake.printLinkedList();
        }

        syncBoard();
        repaint();
    }

    private void triggerCollision(String type, String details) {
        currentState = ScreenState.GAME_OVER;
        collisionType = type;
        collisionDetails = details;

        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Update profile record and Leaderboard
        if (currentProfile != null) {
            currentProfile.incrementGamesPlayed();
            if (currentProfile.updateHighScore(score)) {
                isNewHighScore = true;
                highScore = currentProfile.getHighScore();
            }

            if (score > 0) {
                leaderboard.recordScore(currentProfile.getName(), score);
                System.out.println(">> Leaderboard updated with player '" + currentProfile.getName()
                        + "' and score " + score + " (Bubble Sorted)");
            }
        }

        System.out.println("\n💥 " + type + " DETECTED! " + details);
        System.out.println("Final Score: " + score + " | Length: " + snake.getLength());

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (currentState) {
            case MAIN_MENU:
                drawMainMenuScreen(g);
                break;
            case SELECT_PROFILE:
                drawProfileSelectionScreen(g);
                break;
            case COUNTDOWN:
                drawGameBoard(g);
                drawCountdownOverlay(g);
                break;
            case PLAYING:
                drawGameBoard(g);
                if (isPaused) {
                    drawPauseOverlay(g);
                }
                break;
            case GAME_OVER:
                drawGameBoard(g);
                drawGameOverScreen(g);
                break;
            case HOW_TO_PLAY:
                drawHowToPlayScreen(g);
                break;
            case LEADERBOARD:
                drawLeaderboardScreen(g);
                break;
        }
    }

    /**
     * Renders the game board, top HUD bar, and bottom control bar.
     */
    private void drawGameBoard(Graphics g) {
        // --- 1. Top HUD Header Bar ---
        g.setColor(new Color(16, 18, 23));
        g.fillRect(0, 0, TOTAL_WIDTH, HEADER_HEIGHT);
        g.setColor(new Color(36, 42, 53));
        g.drawLine(0, HEADER_HEIGHT - 1, TOTAL_WIDTH, HEADER_HEIGHT - 1);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Player Name
        g.setColor(new Color(155, 89, 182));
        String pName = (currentProfile != null) ? currentProfile.getName() : "Player";
        g.drawString("👤 " + pName, 20, 27);

        // Score
        g.setColor(new Color(240, 240, 240));
        g.drawString("SCORE: " + score, 180, 27);

        // Level
        g.setColor(new Color(52, 152, 219));
        g.drawString("LEVEL: " + level, 330, 27);

        // High Score
        g.setColor(new Color(241, 196, 15));
        g.drawString("BEST: " + highScore, 460, 27);

        // Length
        g.setColor(new Color(46, 204, 113));
        g.drawString("LENGTH: " + (snake != null ? snake.getLength() : 3), 580, 27);

        // Speed
        g.setColor(new Color(160, 170, 185));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("SPEED: " + currentSpeed + "ms", 695, 27);

        // --- 2. Board Grid Area ---
        int boardOffsetY = HEADER_HEIGHT;

        // Grid lines
        g.setColor(new Color(32, 36, 45));
        for (int r = 0; r <= GRID_HEIGHT; r++) {
            g.drawLine(0, boardOffsetY + r * CELL_SIZE, TOTAL_WIDTH, boardOffsetY + r * CELL_SIZE);
        }
        for (int c = 0; c <= GRID_WIDTH; c++) {
            g.drawLine(c * CELL_SIZE, boardOffsetY, c * CELL_SIZE, boardOffsetY + GRID_HEIGHT * CELL_SIZE);
        }

        // Food
        if (food != null) {
            int px = food.getX() * CELL_SIZE;
            int py = boardOffsetY + food.getY() * CELL_SIZE;

            g.setColor(new Color(231, 76, 60));
            g.fillOval(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
            g.setColor(new Color(241, 148, 138));
            g.fillOval(px + 10, py + 9, (CELL_SIZE - 10) / 3, (CELL_SIZE - 10) / 3);
            g.setColor(new Color(46, 204, 113));
            g.fillRect(px + (CELL_SIZE / 2) - 2, py + 3, 4, 5);
        }

        // Snake
        if (snake != null) {
            SnakeNode current = snake.getHead();
            boolean isHead = true;

            while (current != null) {
                int px = current.x * CELL_SIZE;
                int py = boardOffsetY + current.y * CELL_SIZE;

                if (isHead) {
                    g.setColor(currentState == ScreenState.GAME_OVER ? new Color(231, 76, 60) : new Color(46, 204, 113));
                    g.fillRoundRect(px + 3, py + 3, CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);

                    // Snake eyes
                    g.setColor(Color.WHITE);
                    int eyeSize = 6;
                    int eyeOffset1 = 10;
                    int eyeOffset2 = 24;

                    if (dx == 1) {
                        g.fillOval(px + 26, py + eyeOffset1, eyeSize, eyeSize);
                        g.fillOval(px + 26, py + eyeOffset2, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + 29, py + eyeOffset1 + 1, 3, 3);
                        g.fillOval(px + 29, py + eyeOffset2 + 1, 3, 3);
                    } else if (dx == -1) {
                        g.fillOval(px + 8, py + eyeOffset1, eyeSize, eyeSize);
                        g.fillOval(px + 8, py + eyeOffset2, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + 8, py + eyeOffset1 + 1, 3, 3);
                        g.fillOval(px + 8, py + eyeOffset2 + 1, 3, 3);
                    } else if (dy == -1) {
                        g.fillOval(px + eyeOffset1, py + 8, eyeSize, eyeSize);
                        g.fillOval(px + eyeOffset2, py + 8, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + eyeOffset1 + 1, py + 8, 3, 3);
                        g.fillOval(px + eyeOffset2 + 1, py + 8, 3, 3);
                    } else {
                        g.fillOval(px + eyeOffset1, py + 26, eyeSize, eyeSize);
                        g.fillOval(px + eyeOffset2, py + 26, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + eyeOffset1 + 1, py + 29, 3, 3);
                        g.fillOval(px + eyeOffset2 + 1, py + 29, 3, 3);
                    }
                    isHead = false;
                } else {
                    g.setColor(new Color(39, 174, 96));
                    g.fillRoundRect(px + 4, py + 4, CELL_SIZE - 8, CELL_SIZE - 8, 8, 8);
                }
                current = current.next;
            }
        }

        // --- 3. Bottom Control Bar ---
        int footerY = TOTAL_HEIGHT - FOOTER_HEIGHT;
        g.setColor(new Color(16, 18, 23));
        g.fillRect(0, footerY, TOTAL_WIDTH, FOOTER_HEIGHT);
        g.setColor(new Color(36, 42, 53));
        g.drawLine(0, footerY, TOTAL_WIDTH, footerY);

        g.setColor(new Color(140, 150, 165));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("W/A/S/D to Steer", 30, footerY + 31);
        g.drawString("[M] Menu", TOTAL_WIDTH - 100, footerY + 31);

        // Control Buttons
        drawButton(g, gamePauseBtn, isPaused ? "▶ RESUME" : "❚❚ PAUSE",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 13);
        drawButton(g, gameRestartBtn, "⟳ RESTART",
                new Color(192, 57, 43), new Color(231, 76, 60), Color.WHITE, 13);
    }

    /**
     * Screen 1: Main Menu
     */
    private void drawMainMenuScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        // Background subtle grid decoration
        g.setColor(new Color(25, 28, 36));
        for (int i = 0; i < TOTAL_WIDTH; i += 40) {
            g.drawLine(i, 0, i, TOTAL_HEIGHT);
        }
        for (int j = 0; j < TOTAL_HEIGHT; j += 40) {
            g.drawLine(0, j, TOTAL_WIDTH, j);
        }

        // Title Box Card
        g.setColor(new Color(22, 26, 35, 240));
        g.fillRoundRect(160, 40, 480, 135, 20, 20);
        g.setColor(new Color(45, 55, 72));
        g.drawRoundRect(160, 40, 480, 135, 20, 20);

        // Title
        g.setColor(new Color(46, 204, 113));
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String title = "🐍 SNAKE DSA";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (TOTAL_WIDTH - tw) / 2, 105);

        // Subtitle
        g.setColor(new Color(180, 190, 205));
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        String sub = "Interactive Data Structures & Algorithms Mini Game";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (TOTAL_WIDTH - sw) / 2, 145);

        // Menu Buttons
        drawButton(g, menuPlayBtn, "▶ PLAY GAME",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 17);

        drawButton(g, menuLeaderboardBtn, "🏆 LEADERBOARD",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 17);

        drawButton(g, menuHowToPlayBtn, "📖 HOW TO PLAY",
                new Color(142, 68, 173), new Color(155, 89, 182), Color.WHITE, 17);

        drawButton(g, menuExitBtn, "✕ EXIT",
                new Color(44, 62, 80), new Color(52, 73, 94), Color.WHITE, 17);

        // Active Player info if selected
        if (currentProfile != null) {
            g.setColor(new Color(155, 89, 182));
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            String ap = "Active Player: " + currentProfile.getName() + " (Personal Best: " + currentProfile.getHighScore() + " pts)";
            int apw = g.getFontMetrics().stringWidth(ap);
            g.drawString(ap, (TOTAL_WIDTH - apw) / 2, 490);
        }

        // Bottom Footer
        g.setColor(new Color(130, 140, 155));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String dsaInfo = "Powered by: 2D Array • Linked List • Bubble Sort • Linear Search • Recursion";
        int dw = g.getFontMetrics().stringWidth(dsaInfo);
        g.drawString(dsaInfo, (TOTAL_WIDTH - dw) / 2, TOTAL_HEIGHT - 35);
    }

    /**
     * Screen: Select / Create Profile
     */
    private void drawProfileSelectionScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 560;
        int cardH = 590;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 40;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Header Title
        g.setColor(new Color(46, 204, 113));
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        String title = "👤 SELECT YOUR PROFILE";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 45);

        // Subtitle
        g.setColor(new Color(170, 180, 195));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String sub = "Click your profile to start, or create a new one to join!";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, cardX + (cardW - sw) / 2, cardY + 74);

        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 88, cardX + cardW - 30, cardY + 88);

        // Profile Cards List
        int startY = 140;
        int pCardH = 50;
        int gap = 10;
        int pCardW = 440;
        int pCardX = (TOTAL_WIDTH - pCardW) / 2;

        for (int i = 0; i < Math.min(profiles.size(), 6); i++) {
            Profile prof = profiles.get(i);
            Rectangle rect = new Rectangle(pCardX, startY + i * (pCardH + gap), pCardW, pCardH);

            boolean isHovered = (mousePoint != null && rect.contains(mousePoint));
            boolean isSelected = (currentProfile != null && currentProfile.getName().equals(prof.getName()));

            // Card body
            if (isSelected) {
                g.setColor(new Color(39, 174, 96, 60));
            } else if (isHovered) {
                g.setColor(new Color(41, 128, 185, 50));
            } else {
                g.setColor(new Color(32, 38, 50));
            }
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

            // Card border
            if (isSelected) {
                g.setColor(new Color(46, 204, 113));
            } else if (isHovered) {
                g.setColor(new Color(52, 152, 219));
            } else {
                g.setColor(new Color(55, 65, 82));
            }
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

            // Avatar & Name
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.setColor(Color.WHITE);
            g.drawString("👤 " + prof.getName(), rect.x + 20, rect.y + 32);

            // High Score
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(new Color(241, 196, 15));
            String hsStr = "Best: " + prof.getHighScore() + " pts";
            int hsw = g.getFontMetrics().stringWidth(hsStr);
            g.drawString(hsStr, rect.x + rect.width - 25 - hsw, rect.y + 32);
        }

        // Action Buttons
        drawButton(g, profileCreateBtn, "+ CREATE NEW PROFILE",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 15);

        drawButton(g, profileBackBtn, "⌂ BACK TO MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 14);
    }

    /**
     * Screen 3: Game Over Screen
     */
    private void drawGameOverScreen(Graphics g) {
        // Dark backdrop
        g.setColor(new Color(0, 0, 0, 215));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 500;
        int cardH = 500;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 85;

        // Card container
        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Title
        g.setColor(new Color(231, 76, 60));
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        String title = "GAME OVER";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 50);

        // Collision diagnostics
        g.setColor(new Color(241, 196, 15));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int cdw = g.getFontMetrics().stringWidth(collisionDetails);
        g.drawString(collisionDetails, cardX + (cardW - cdw) / 2, cardY + 78);

        // Player Name & Score
        g.setColor(new Color(155, 89, 182));
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        String pName = "PLAYER: " + ((currentProfile != null) ? currentProfile.getName() : "Player");
        int pnw = g.getFontMetrics().stringWidth(pName);
        g.drawString(pName, cardX + (cardW - pnw) / 2, cardY + 115);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        String scoreStr = "SCORE: " + score;
        int scw = g.getFontMetrics().stringWidth(scoreStr);
        g.drawString(scoreStr, cardX + (cardW - scw) / 2, cardY + 155);

        // High Score indicator
        if (isNewHighScore && score > 0) {
            g.setColor(new Color(241, 196, 15));
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String nhs = "★ NEW HIGH SCORE! ★";
            int nhsw = g.getFontMetrics().stringWidth(nhs);
            g.drawString(nhs, cardX + (cardW - nhsw) / 2, cardY + 190);
        } else {
            g.setColor(new Color(170, 180, 195));
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            String hsStr = "Personal Best: " + highScore + " pts  |  Snake Length: " + (snake != null ? snake.getLength() : 0);
            int hsw = g.getFontMetrics().stringWidth(hsStr);
            g.drawString(hsStr, cardX + (cardW - hsw) / 2, cardY + 190);
        }

        // Action Buttons
        drawButton(g, goPlayAgainBtn, "⟳ PLAY AGAIN",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 15);

        drawButton(g, goSwitchProfileBtn, "👤 SWITCH PROFILE",
                new Color(142, 68, 173), new Color(155, 89, 182), Color.WHITE, 15);

        drawButton(g, goLeaderboardBtn, "🏆 LEADERBOARD",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 15);

        drawButton(g, goMainMenuBtn, "⌂ MAIN MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 15);
    }

    /**
     * How To Play & DSA Guide Screen
     */
    private void drawHowToPlayScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 600;
        int cardH = 550;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 60;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Title
        g.setColor(new Color(155, 89, 182));
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String title = "HOW TO PLAY & DSA GUIDE";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 45);

        // Divider
        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 65, cardX + cardW - 30, cardY + 65);

        // Content
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(new Color(241, 196, 15));
        g.drawString("🎮 CONTROLS & PROFILES:", cardX + 40, cardY + 98);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        g.drawString("• W / ↑ : Move Up       • S / ↓ : Move Down", cardX + 50, cardY + 125);
        g.drawString("• A / ← : Move Left     • D / → : Move Right", cardX + 50, cardY + 148);
        g.drawString("• Choose or create your profile to record personal high scores!", cardX + 50, cardY + 171);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(new Color(46, 204, 113));
        g.drawString("📚 DATA STRUCTURES & ALGORITHMS IMPLEMENTATION:", cardX + 40, cardY + 215);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(220, 225, 235));
        g.drawString("1. 2D Array Grid (int[][] board):", cardX + 50, cardY + 242);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Represents the 20x15 grid (0=Empty, 1=Snake, 2=Food) with O(1) lookups.", cardX + 65, cardY + 263);

        g.setColor(new Color(220, 225, 235));
        g.drawString("2. Singly Linked List (SnakeNode & Snake):", cardX + 50, cardY + 292);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Dynamic head insertion O(1) & tail deletion for flexible snake growth.", cardX + 65, cardY + 313);

        g.setColor(new Color(220, 225, 235));
        g.drawString("3. Bubble Sort Algorithm (BubbleSort.java):", cardX + 50, cardY + 342);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Sorts leaderboard scores in descending order (highest score first).", cardX + 65, cardY + 363);

        g.setColor(new Color(220, 225, 235));
        g.drawString("4. Linear Search Algorithm (LinearSearch.java):", cardX + 50, cardY + 392);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Searches players by name sequentially across the leaderboard in O(N).", cardX + 65, cardY + 413);

        g.setColor(new Color(220, 225, 235));
        g.drawString("5. Recursion (RecursionCountdown.java):", cardX + 50, cardY + 442);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   countdown(3) -> countdown(2) -> countdown(1) -> countdown(0) [Base Case].", cardX + 65, cardY + 463);

        // Back Button
        drawButton(g, howBackBtn, "⌂ BACK TO MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 15);
    }

    /**
     * Leaderboard Screen with Bubble Sort list and Linear Search button.
     */
    private void drawLeaderboardScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 560;
        int cardH = 550;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 60;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Title
        g.setColor(new Color(241, 196, 15));
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String title = "🏆 LEADERBOARD";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 45);

        // Subtitle
        g.setColor(new Color(170, 180, 195));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String sub = "Sorted with Bubble Sort Algorithm (Descending)";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, cardX + (cardW - sw) / 2, cardY + 72);

        // Divider
        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 85, cardX + cardW - 30, cardY + 85);

        // Scores List
        List<LeaderboardEntry> entries = leaderboard.getEntries();
        int entryY = cardY + 125;
        g.setFont(new Font("SansSerif", Font.BOLD, 17));

        for (int i = 0; i < Math.min(entries.size(), 7); i++) {
            LeaderboardEntry entry = entries.get(i);

            // Badge / colors
            if (i == 0) g.setColor(new Color(241, 196, 15)); // Gold
            else if (i == 1) g.setColor(new Color(189, 195, 199)); // Silver
            else if (i == 2) g.setColor(new Color(205, 127, 50)); // Bronze
            else g.setColor(Color.WHITE);

            String rank = (i + 1) + ".";
            g.drawString(rank, cardX + 60, entryY);
            g.drawString(entry.getName(), cardX + 115, entryY);

            g.setColor(new Color(46, 204, 113));
            String scoreStr = String.valueOf(entry.getScore());
            int scw = g.getFontMetrics().stringWidth(scoreStr);
            g.drawString(scoreStr, cardX + cardW - 80 - scw, entryY);

            entryY += 44;
        }

        // Action Buttons
        drawButton(g, lbSearchBtn, "🔍 SEARCH PLAYER",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 14);

        drawButton(g, lbBackBtn, "⌂ BACK TO MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 14);
    }

    /**
     * Recursive Countdown screen overlay (3 -> 2 -> 1 -> GO!).
     */
    private void drawCountdownOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        boolean isGo = "GO!".equals(countdownText);
        g.setFont(new Font("SansSerif", Font.BOLD, isGo ? 90 : 120));
        g.setColor(isGo ? new Color(46, 204, 113) : new Color(241, 196, 15));

        int cw = g.getFontMetrics().stringWidth(countdownText);
        g.drawString(countdownText, (TOTAL_WIDTH - cw) / 2, TOTAL_HEIGHT / 2 + 10);

        // Player Name
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(new Color(155, 89, 182));
        String pName = "Player: " + ((currentProfile != null) ? currentProfile.getName() : "Player");
        int pw = g.getFontMetrics().stringWidth(pName);
        g.drawString(pName, (TOTAL_WIDTH - pw) / 2, TOTAL_HEIGHT / 2 - 90);

        // DSA Explanation text
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.setColor(new Color(220, 225, 235));
        String dsaNote = "Algorithm: Recursive Countdown -> countdown(" + countdownText + ")";
        int dw = g.getFontMetrics().stringWidth(dsaNote);
        g.drawString(dsaNote, (TOTAL_WIDTH - dw) / 2, TOTAL_HEIGHT / 2 + 85);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(160, 170, 185));
        String stackInfo = "Call Stack: countdown(3) ↓ countdown(2) ↓ countdown(1) ↓ countdown(0) [Base Case]";
        int siw = g.getFontMetrics().stringWidth(stackInfo);
        g.drawString(stackInfo, (TOTAL_WIDTH - siw) / 2, TOTAL_HEIGHT / 2 + 115);
    }

    /**
     * Pause overlay.
     */
    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 46));
        String pText = "PAUSED";
        int pw = g.getFontMetrics().stringWidth(pText);
        g.drawString(pText, (TOTAL_WIDTH - pw) / 2, TOTAL_HEIGHT / 2 - 20);

        g.setColor(new Color(180, 190, 205));
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String sub = "Press SPACE or Click RESUME to Continue";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (TOTAL_WIDTH - sw) / 2, TOTAL_HEIGHT / 2 + 25);
    }

    /**
     * Helper to render polished interactive buttons with hover animation.
     */
    private void drawButton(Graphics g, Rectangle r, String text, Color baseColor, Color hoverColor, Color textColor, int fontSize) {
        boolean isHovered = (mousePoint != null && r.contains(mousePoint));

        // Background
        g.setColor(isHovered ? hoverColor : baseColor);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        // Border highlight
        g.setColor(isHovered ? Color.WHITE : new Color(255, 255, 255, 40));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        // Text
        g.setColor(textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getAscent();
        int textX = r.x + (r.width - tw) / 2;
        int textY = r.y + (r.height + th) / 2 - 3;
        g.drawString(text, textX, textY);
    }
}
