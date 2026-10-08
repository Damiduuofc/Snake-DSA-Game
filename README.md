# 🐍 Snake DSA Game — Developing a Mini Game

> **Data Structures & Algorithms (DSA) Coursework Assignment**  
> An interactive desktop Snake game built with **Java Swing**, intentionally engineered to demonstrate practical, real-world implementations of core Computer Science Data Structures and Algorithms.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Data Structures & Algorithms Implemented](#-data-structures--algorithms-implemented)
   - [1. 2D Array Grid (Array DSA)](#1-2d-array-grid-array-dsa)
   - [2. Singly Linked List (Snake Body DSA)](#2-singly-linked-list-snake-body-dsa)
   - [3. Bubble Sort Algorithm (Leaderboard Sorting)](#3-bubble-sort-algorithm-leaderboard-sorting)
   - [4. Linear Search Algorithm (Player Search)](#4-linear-search-algorithm-player-search)
   - [5. Recursion (Call-Stack Game Countdown)](#5-recursion-call-stack-game-countdown)
3. [Algorithmic Complexity Analysis](#-algorithmic-complexity-analysis)
4. [Version History & Development Milestones](#-version-history--development-milestones)
5. [Class & Architecture Overview](#-class--architecture-overview)
6. [Game Features & UI Screens](#-game-features--ui-screens)
7. [Controls & How to Play](#-controls--how-to-play)
8. [Installation & Execution Guide](#-installation--execution-guide)

---

## 📌 Project Overview

This project was developed for the **DSA Assignment — Developing a Mini Game**. The objective was not merely to construct a functional game, but to architect every game mechanic around academic DSA requirements:

- The game world is mapped to a mathematical matrix using a **2D Array**.
- The snake's anatomy and movement are handled via a **Singly Linked List** with pointer manipulation.
- High scores are processed and ranked using **Bubble Sort**.
- The leaderboard is queryable by player name using **Linear Search**.
- Round initialization utilizes genuine **Call-Stack Recursion** ($3 \to 2 \to 1 \to 0$).
- Multi-user support with a persistent **Profile Selection & Creation System**.

---

## 🧠 Data Structures & Algorithms Implemented

### 1. 2D Array Grid (Array DSA)
- **File**: [`src/GamePanel.java`](src/GamePanel.java)
- **Data Structure**: `int[][] board = new int[15][20];`
- **Specification**:
  - Grid resolution: **20 columns $\times$ 15 rows**
  - Tile dimensions: **40 $\times$ 40 pixels** ($800 \times 600\text{ px}$ playable canvas)
  - State Encoding:
    - `0` = Empty Cell (`EMPTY`)
    - `1` = Snake Body Segment (`SNAKE_CELL`)
    - `2` = Food Item (`FOOD_CELL`)
- **DSA Application**:
  - Provides $O(1)$ constant-time lookup to query whether an intended target coordinate contains food, is empty, or collides with the snake's body (`board[y][x] == 1`).
  - Synchronized dynamically on every game tick from the Linked List state.

```text
Matrix Representation (20 x 15):
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 1 1 1 0 0 0 0 2 0 0 0 0 0 0 0   <-- Snake (1) & Food (2)
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
```

---

### 2. Singly Linked List (Snake Body DSA)
- **Files**: [`src/SnakeNode.java`](src/SnakeNode.java), [`src/Snake.java`](src/Snake.java)
- **Data Structure**:
  ```text
  Head ──▶ Node ──▶ Node ──▶ Node ──▶ null
  (x,y)    (x,y)    (x,y)    (x,y)
  ```
- **DSA Operations**:
  - **Node Class (`SnakeNode`)**: Encapsulates `(int x, int y)` coordinates and a reference pointer `SnakeNode next`.
  - **Head Insertion (`addHead`)**: When moving in direction $(dx, dy)$, a new node is created at $(head.x + dx, head.y + dy)$ and linked to the head in **$O(1)$ constant time**.
  - **Tail Removal (`removeTail`)**: If food is not consumed, the last node is unlinked by traversing to the penultimate node in **$O(N)$ linear time**.
  - **Self-Collision Detection (`checkSelfCollision`)**: Traverses from `head.next` down to the tail node to detect coordinate intersections with the head ($O(N)$).

---

### 3. Bubble Sort Algorithm (Leaderboard Sorting)
- **File**: [`src/BubbleSort.java`](src/BubbleSort.java)
- **Algorithm**: Descending Bubble Sort (Highest Score First).
- **Time Complexity**: $O(N^2)$ average/worst case, optimized to $O(N)$ with an early-exit `swapped` flag.
- **Space Complexity**: $O(1)$ in-place sorting.
- **DSA Application**:
  Whenever a player completes a round, their score is submitted to [`Leaderboard.java`](src/Leaderboard.java). The sorting algorithm iterates through adjacent scores, comparing and swapping items until the entire leaderboard is ordered descending:

```java
public static void sort(List<LeaderboardEntry> list) {
    int n = list.size();
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (list.get(j).getScore() < list.get(j + 1).getScore()) {
                LeaderboardEntry temp = list.get(j);
                list.set(j, list.get(j + 1));
                list.set(j + 1, temp);
                swapped = true;
            }
        }
        if (!swapped) break; // Optimized early exit
    }
}
```

**Demonstration**:
```text
Before Bubble Sort:          After Bubble Sort (Descending):
Damidu      850              Amal        950
Kasun       400    ───▶      Damidu      850
Amal        950              Nimal       650
Nimal       650              Kasun       400
```

---

### 4. Linear Search Algorithm (Player Search)
- **File**: [`src/LinearSearch.java`](src/LinearSearch.java)
- **Algorithm**: Linear (Sequential) Search.
- **Time Complexity**: $O(N)$ worst/average, $O(1)$ best case.
- **DSA Application**:
  Allows the user to search the Leaderboard by entering a player's name (case-insensitive). The algorithm visits each entry from index $0$ to $N - 1$, printing verification diagnostics and comparison step counts to standard output:

$$\text{Player } 1 \longrightarrow \text{Player } 2 \longrightarrow \text{Player } 3 \longrightarrow \dots \longrightarrow \text{Player } N$$

**Execution Trace**:
```text
--- Starting Linear Search for: 'Damidu' ---
  Step 1: Inspecting index 0 -> Amal (950)
  Step 2: Inspecting index 1 -> Damidu (850)
  ==> MATCH FOUND at index 1! [Time: O(2) steps]
Result: Player: Damidu | Score: 850
```

---

### 5. Recursion (Call-Stack Game Countdown)
- **File**: [`src/RecursionCountdown.java`](src/RecursionCountdown.java)
- **Algorithm**: Pure Call-Stack Recursion.
- **Execution Mechanism**:
  Unlike iterative loops or timer counters, this is executed by **genuine call-stack frame recursion** on a worker thread. Each number pushes a new stack frame onto the JVM call stack until the Base Case ($n \le 0$) is triggered:

$$\text{countdown}(3) \;\longrightarrow\; \text{countdown}(2) \;\longrightarrow\; \text{countdown}(1) \;\longrightarrow\; \text{countdown}(0) \;[\text{Base Case}] \;\longrightarrow\; \text{START GAME}$$

```java
private void recursiveCountdown(int n, int depth) {
    if (n <= 0) { // Base Case
        SwingUtilities.invokeLater(() -> listener.onTick("GO!"));
        Thread.sleep(600);
        SwingUtilities.invokeLater(() -> listener.onComplete());
        return;
    }

    // Recursive Case
    SwingUtilities.invokeLater(() -> listener.onTick(String.valueOf(n)));
    Thread.sleep(850);

    // Call-Stack Recursive Invocation
    recursiveCountdown(n - 1, depth + 1);
}
```

---

## 📊 Algorithmic Complexity Analysis

| Operation / Feature | Component / Class | Data Structure / Algorithm | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :---: | :---: |
| **Grid Cell Lookup** | `GamePanel` | 2D Array (`int[][]`) | $O(1)$ | $O(W \times H)$ |
| **Snake Head Advance** | `Snake.addHead` | Singly Linked List | $O(1)$ | $O(1)$ |
| **Snake Tail Trim** | `Snake.removeTail` | Singly Linked List | $O(N)$ | $O(1)$ |
| **Self Collision Check** | `Snake.checkSelfCollision` | Linked List Traversal | $O(N)$ | $O(1)$ |
| **Spatial Self Collision** | `GamePanel.actionPerformed` | 2D Array Lookup | $O(1)$ | $O(1)$ |
| **Leaderboard Sorting** | `BubbleSort.sort` | Bubble Sort Algorithm | $O(N^2)$ | $O(1)$ in-place |
| **Player Search** | `LinearSearch.searchByName` | Linear Search Algorithm | $O(N)$ | $O(1)$ |
| **Round Start Countdown** | `RecursionCountdown` | Call-Stack Recursion | $O(K)$ | $O(K)$ stack frames |
| **Food Placement** | `Food.respawn` | Coordinate Generation + LL `contains` | $O(N)$ | $O(1)$ |

*(Where $N$ = Snake length or Leaderboard size, $W \times H$ = Grid dimensions $20 \times 15$, $K$ = Countdown start value 3).*

---

## 🚀 Version History & Development Milestones

- **Version 1 — Basic Java GUI**: Set up `JFrame` window with basic dimensions and centered title.
- **Version 3 — Create the Grid**: Introduced the $20 \times 15$ grid ($40\text{ px}$ tiles) powered by `int[][] board` 2D Array DSA.
- **Version 4 — Snake Movement**: Developed `SnakeNode` and `Snake` using a Singly Linked List with keyboard event listeners ($W/A/S/D$ and Arrow keys).
- **Version 5 — Food & Growth**: Implemented `Food.java` with randomized respawning, score increments, body segment expansion, and dynamic timer acceleration.
- **Version 6 — Collision Detection**: Integrated boundary checks for Wall Collisions and dual-method Self Collisions (Array $O(1)$ check and Linked List $O(N)$ traversal).
- **Version 7 — Bubble Sort**: Implemented `BubbleSort.java` for high scores, managing the descending leaderboard display.
- **Version 8 — Linear Search**: Implemented `LinearSearch.java` for querying players with step diagnostic logging.
- **Version 9 — Recursion**: Created `RecursionCountdown.java` for genuine recursive call-stack countdowns ($3 \to 2 \to 1 \to \text{GO!}$).
- **Version 10 — Final GUI & Profile System**: Built complete multi-screen GUI (Main Menu, Profile Selector & Creator, In-Game HUD, Game Over Screen, and How-To-Play Guide).

---

## 🏛 Class & Architecture Overview

```text
src/
├── Main.java                 # Entry point, initializes JFrame window and launches GamePanel
├── GamePanel.java            # Main controller, state machine, rendering engine, and input listener
├── SnakeNode.java            # Node structure for the Singly Linked List (x, y, next)
├── Snake.java                # Singly Linked List manager (head insertion, tail removal, body traversal)
├── Food.java                 # Randomized food coordinate generator and visual renderer
├── Profile.java              # User Profile entity (player name, personal high score, game counts)
├── LeaderboardEntry.java     # Single record on the leaderboard (player name, score)
├── Leaderboard.java          # High score repository integrating Bubble Sort and Linear Search
├── BubbleSort.java           # Descending Bubble Sort algorithm implementation
├── LinearSearch.java         # Sequential Linear Search algorithm implementation
└── RecursionCountdown.java   # Call-stack recursive countdown engine (3 -> 2 -> 1 -> GO!)
```

---

## 🎮 Game Features & UI Screens

### 1. Main Menu Screen
- Title: **`🐍 SNAKE DSA`**
- Interactive navigation buttons:
  - **`▶ PLAY GAME`**: Opens profile selector.
  - **`🏆 LEADERBOARD`**: Displays Bubble-Sorted rankings.
  - **`📖 HOW TO PLAY`**: Explains game mechanics and DSA implementations.
  - **`✕ EXIT`**: Quits the application.

### 2. Profile Selection & Creation Screen
- Displays existing player profiles with personal records:
  - **Damidu** (850 pts)
  - **Amal** (950 pts)
  - **Nimal** (650 pts)
  - **Kasun** (400 pts)
- **`+ CREATE NEW PROFILE`**: Input prompt to register a new player profile.
- Selecting a profile immediately initiates the game under their name and tracks their high score.

### 3. Gameplay Screen
- **Top HUD Header Bar**: Real-time stats display:
  - `👤 PLAYER`: Active player profile
  - `SCORE`: Current run score
  - `LEVEL`: Current difficulty tier
  - `BEST`: Active player's all-time personal high score
  - `LENGTH`: Total Linked List segments
  - `SPEED`: Current tick delay in milliseconds
- **Playing Area**: $20 \times 15$ grid rendering green snake with directional eyes and red apple food.
- **Bottom Controls**: Interactive **`[❚❚ PAUSE]`** / **`[▶ RESUME]`** and **`[⟳ RESTART]`** buttons.

### 4. Game Over Screen
- Collision diagnostic report (e.g. Wall collision coordinates or body self-collision).
- Score report and glowing **`★ NEW HIGH SCORE! ★`** badge when beating personal records.
- Action Buttons:
  - **`⟳ PLAY AGAIN`**: Immediately restarts round with current player profile.
  - **`👤 SWITCH PROFILE`**: Return to profile selector to switch players.
  - **`🏆 LEADERBOARD`**: View updated global standings.
  - **`⌂ MAIN MENU`**: Return to title screen.

---

## ⌨️ Controls & How to Play

| Action | Primary Key | Secondary Key | Mouse Option |
| :--- | :---: | :---: | :---: |
| **Move Up** | `W` | `Up Arrow (↑)` | — |
| **Move Down** | `S` | `Down Arrow (↓)` | — |
| **Move Left** | `A` | `Left Arrow (←)` | — |
| **Move Right** | `D` | `Right Arrow (→)` | — |
| **Pause / Resume** | `Space` | `P` | Click `[ PAUSE ]` button |
| **Restart Game** | `R` | `Enter` | Click `[ RESTART ]` button |
| **View Leaderboard** | `L` | — | Click `[ LEADERBOARD ]` button |
| **Search Player** | `F` | — | Click `[ SEARCH PLAYER ]` button |
| **Return to Menu** | `M` | `Escape` | Click `[ MAIN MENU ]` button |

---

## 🛠 Installation & Execution Guide

### Prerequisites
- **Java Development Kit (JDK)**: JDK 11 or higher (Tested on Java 26 SE).
- Any standard operating system (macOS, Linux, Windows).

### Compiling from Terminal
Navigate to the project root directory and run:

```bash
# Create compilation directory
mkdir -p out

# Compile all Java sources
javac -d out src/*.java
```

### Running from Terminal
```bash
# Execute the main class
java -cp out Main
```

### Running with NetBeans / Apache Ant
If using NetBeans or Ant:
```bash
ant compile
ant run
```

---

## 📝 Academic Submission Information
- **Course**: Data Structures and Algorithms (DSA)
- **Module**: Developing a Mini Game
- **Project Title**: Snake DSA Game
- **Developer**: Damidu
- **Language**: Java (SE) with Swing GUI Framework
