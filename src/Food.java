import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Represents Food in the Snake DSA Game.
 * Handles random placement on the grid avoiding snake coordinates,
 * score points, and visual rendering.
 */
public class Food {

    private int x;
    private int y;
    private int points;
    private final Random random;

    public Food(int gridWidth, int gridHeight, Snake snake) {
        this.random = new Random();
        this.points = 10;
        respawn(gridWidth, gridHeight, snake);
    }

    /**
     * Randomly positions the food on an empty cell not occupied by the snake.
     */
    public void respawn(int gridWidth, int gridHeight, Snake snake) {
        int newX;
        int newY;
        do {
            newX = random.nextInt(gridWidth);
            newY = random.nextInt(gridHeight);
        } while (snake != null && snake.contains(newX, newY));

        this.x = newX;
        this.y = newY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Renders the food item onto the game canvas.
     */
    public void draw(Graphics g, int cellSize) {
        int px = x * cellSize;
        int py = y * cellSize;

        // Outer red apple body
        g.setColor(new Color(231, 76, 60));
        g.fillOval(px + 4, py + 4, cellSize - 8, cellSize - 8);

        // Apple shine / reflection highlight
        g.setColor(new Color(241, 148, 138));
        g.fillOval(px + 10, py + 8, (cellSize - 8) / 3, (cellSize - 8) / 3);

        // Stem / Leaf (little green leaf on top)
        g.setColor(new Color(46, 204, 113));
        g.fillRect(px + (cellSize / 2) - 2, py + 2, 4, 5);
    }
}
