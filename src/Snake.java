/**
 * Snake linked list implementation.
 * 
 * DSA Structure: Singly Linked List
 * Head -> Node -> Node -> Node -> null
 */
public class Snake {

    private SnakeNode head;
    private int length;

    public Snake(int startX, int startY, int initialLength) {
        this.head = new SnakeNode(startX, startY);
        this.length = 1;

        // Build initial body segments extending to the left
        SnakeNode current = this.head;
        for (int i = 1; i < initialLength; i++) {
            current.next = new SnakeNode(startX - i, startY);
            current = current.next;
            this.length++;
        }
    }

    public SnakeNode getHead() {
        return head;
    }

    public int getLength() {
        return length;
    }

    /**
     * Inserts a new head node at the front of the linked list.
     * Time Complexity: O(1)
     */
    public void addHead(int x, int y) {
        SnakeNode newHead = new SnakeNode(x, y);
        newHead.next = head;
        head = newHead;
        length++;
    }

    /**
     * Removes the last node (tail) of the linked list.
     * Time Complexity: O(N)
     * 
     * @return The removed SnakeNode (coordinates can be used to clear board cell)
     */
    public SnakeNode removeTail() {
        if (head == null) {
            return null;
        }

        if (head.next == null) {
            SnakeNode removed = head;
            head = null;
            length = 0;
            return removed;
        }

        SnakeNode current = head;
        while (current.next.next != null) {
            current = current.next;
        }

        SnakeNode removed = current.next;
        current.next = null;
        length--;
        return removed;
    }

    /**
     * Moves the snake by prepending a new head at (head.x + dx, head.y + dy).
     * If the snake did not eat food, the tail is removed.
     * 
     * @param dx change in x
     * @param dy change in y
     * @param grow whether the snake should grow (eaten food)
     * @return The removed tail node if not growing, or null if growing
     */
    public SnakeNode move(int dx, int dy, boolean grow) {
        int newX = head.x + dx;
        int newY = head.y + dy;

        addHead(newX, newY);

        if (!grow) {
            return removeTail();
        }
        return null;
    }

    /**
     * Checks if the snake's head collides with any part of its body.
     */
    public boolean checkSelfCollision() {
        if (head == null || head.next == null) {
            return false;
        }

        SnakeNode current = head.next;
        while (current != null) {
            if (current.x == head.x && current.y == head.y) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Checks if any node in the snake occupies the given coordinates.
     */
    public boolean contains(int x, int y) {
        SnakeNode current = head;
        while (current != null) {
            if (current.x == x && current.y == y) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Traverses the linked list to find the tail node.
     * Time Complexity: O(N)
     */
    public SnakeNode getTail() {
        if (head == null) {
            return null;
        }
        SnakeNode current = head;
        while (current.next != null) {
            current = current.next;
        }
        return current;
    }

    /**
     * Visualizes the Linked List state in console.
     */
    public void printLinkedList() {
        System.out.print("Snake Linked List: Head");
        SnakeNode current = head;
        while (current != null) {
            System.out.print(" -> (" + current.x + "," + current.y + ")");
            current = current.next;
        }
        System.out.println(" -> null (Length: " + length + ")");
    }
}
