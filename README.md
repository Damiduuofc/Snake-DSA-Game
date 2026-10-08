# 🐍 Snake DSA Game — Developing a Mini Game

> **Data Structures & Algorithms (DSA) Coursework Assignment**  
> An interactive desktop Snake game built with **Java Swing**, intentionally engineered to demonstrate practical, real-world implementations of core Computer Science Data Structures and Algorithms.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Data Structures Implemented (Built from Scratch)](#-data-structures-implemented-built-from-scratch)
   - [1. 2D Array Grid (Array DSA)](#1-2d-array-grid-array-dsa)
   - [2. Doubly Linked List with Tail Pointer (Snake Anatomy)](#2-doubly-linked-list-with-tail-pointer-snake-anatomy)
   - [3. Hand-Built FIFO Queue (Input Buffer Queue)](#3-hand-built-fifo-queue-input-buffer-queue)
   - [4. Hand-Built LIFO Stack (Action & Event History Stack)](#4-hand-built-lifo-stack-action--event-history-stack)
   - [5. Plain Array Data Structure (Leaderboard Repository)](#5-plain-array-data-structure-leaderboard-repository)
3. [Algorithms Implemented](#-algorithms-implemented)
   - [1. Bubble Sort Algorithm (Plain Array Leaderboard Sorting)](#1-bubble-sort-algorithm-plain-array-leaderboard-sorting)
   - [2. Linear Search Algorithm (In-Window Step Trace)](#2-linear-search-algorithm-in-window-step-trace)
   - [3. Recursive Binary Search Algorithm (Sorted Array Score Query)](#3-recursive-binary-search-algorithm-sorted-array-score-query)
   - [4. Call-Stack Recursion (Round Start Countdown)](#4-call-stack-recursion-round-start-countdown)
   - [5. Mathematical Scoring Recursion (Combo Multiplier)](#5-mathematical-scoring-recursion-combo-multiplier)
4. [Algorithmic Complexity Analysis](#-algorithmic-complexity-analysis)
5. [Class & Architecture Overview](#-class--architecture-overview)
6. [Game Features & UI Screens](#-game-features--ui-screens)
7. [Controls & How to Play](#-controls--how-to-play)
8. [Installation & Execution Guide](#-installation--execution-guide)

---

## 📌 Project Overview

This project was developed for the **DSA Assignment — Developing a Mini Game**. The architecture directly addresses every marking criteria by implementing custom, hand-built data structures (without relying on Java collections) and robust algorithms:

- **No $O(N)$ Tail Walking**: Snake is a full **Doubly Linked List** with both `head` and `tail` pointers for **$O(1)$ constant-time tail removal**.
- **Hand-Built Queue**: A custom generic FIFO queue (`CustomQueue<Direction>`) provides responsive **Input Buffering**.
- **Hand-Built Stack**: A custom generic LIFO stack (`CustomStack<String>`) tracks real-time **Game Event History**.
- **Pure Plain Arrays**: The leaderboard operates on `LeaderboardEntry[]` with manual array resizing and **in-place Bubble Sort**.
- **Two Rigorous Recursive Functions**:
  1. Call-stack recursive countdown ($3 \to 2 \to 1 \to 0$ [Base Case]).
  2. Recursive Binary Search by score ($O(\log N)$) and recursive combo scoring.
- **In-Window GUI Search Trace**: The search results and step-by-step comparisons render visually inside the GUI window for presentation and video demonstration.
- **Safe Deterministic Food Placement**: Scans free cells to guarantee no infinite loops when the snake fills the board, handling the 100% grid victory state.

---

## 🧠 Data Structures Implemented (Built from Scratch)

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
  - Provides $O(1)$ constant-time spatial lookup to query whether a coordinate contains food, is empty, or collides with the snake's body (`board[y][x] == 1`).

---

### 2. Doubly Linked List with Tail Pointer (Snake Anatomy)
- **Files**: [`src/SnakeNode.java`](src/SnakeNode.java), [`src/Snake.java`](src/Snake.java)
- **Data Structure**:
  ```text
  null <──▶ Head ◀──▶ Node ◀──▶ Node ◀──▶ Tail ──▶ null
            (x,y)     (x,y)     (x,y)     (x,y)
  ```
- **DSA Upgrade (Solving $O(N)$ Tail Removal)**:
  - Standard singly linked lists require walking all $N$ nodes to find the node before the tail.
  - Upgraded to a **Doubly Linked List** with a direct `tail` pointer and `prev` pointers.
  - **`addHead(x, y)`**: Links new head in **$O(1)$ constant time**.
  - **`removeTail()`**: Unlinks tail in **$O(1)$ constant time** using `tail = tail.prev; tail.next = null;`.

---

### 3. Hand-Built FIFO Queue (Input Buffer Queue)
- **File**: [`src/CustomQueue.java`](src/CustomQueue.java)
- **Data Structure**: Linked-node generic FIFO Queue (`Node<T> front, rear`).
- **Performance**:
  - `enqueue(item)`: $O(1)$
  - `dequeue()`: $O(1)$
  - `peek()`: $O(1)$
- **Game Application**:
  Buffers user directional keystrokes. When a player rapidly inputs sequential turns (e.g., `Right` then `Down` within a single $140\text{ ms}$ tick), inputs are safely buffered in the queue and processed consecutively, preventing missed turns or self-collision glitches.

---

### 4. Hand-Built LIFO Stack (Action & Event History Stack)
- **File**: [`src/CustomStack.java`](src/CustomStack.java)
- **Data Structure**: Linked-node generic LIFO Stack (`Node<T> top`).
- **Performance**:
  - `push(item)`: $O(1)$
  - `pop()`: $O(1)$
  - `peek()`: $O(1)$
- **Game Application**:
  Maintains a real-time event and milestone history stack (e.g. food eaten, recursive combo bonuses, level-ups, and crash diagnostics).

---

### 5. Plain Array Data Structure (Leaderboard Repository)
- **Files**: [`src/Leaderboard.java`](src/Leaderboard.java), [`src/LeaderboardEntry.java`](src/LeaderboardEntry.java)
- **Data Structure**: `LeaderboardEntry[] entries = new LeaderboardEntry[capacity];`
- **DSA Application**:
  Strictly operates on primitive Java plain arrays with manual capacity management (`resizeArray`), eliminating reliance on high-level collection wrappers like `java.util.List`.

---

## ⚡ Algorithms Implemented

### 1. Bubble Sort Algorithm (Plain Array Leaderboard Sorting)
- **File**: [`src/BubbleSort.java`](src/BubbleSort.java)
- **Time Complexity**: $O(N^2)$ average/worst, $O(N)$ best case (optimized with early-exit flag).
- **Space Complexity**: $O(1)$ in-place.
- **Specification**: Sorts the plain array `LeaderboardEntry[]` in descending order by score.

```java
public static void sort(LeaderboardEntry[] entries, int count) {
    for (int i = 0; i < count - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < count - i - 1; j++) {
            if (entries[j].getScore() < entries[j + 1].getScore()) {
                LeaderboardEntry temp = entries[j];
                entries[j] = entries[j + 1];
                entries[j + 1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break; // Optimized early exit
    }
}
```

---

### 2. Linear Search Algorithm (In-Window Step Trace)
- **File**: [`src/LinearSearch.java`](src/LinearSearch.java)
- **Time Complexity**: $O(N)$
- **GUI Integration**:
  Unlike console-only outputs, query results and comparison step traces are **visually rendered directly inside the GUI window modal**, ensuring they are fully visible during presentation and video demonstrations.

---

### 3. Recursive Binary Search Algorithm (Sorted Array Score Query)
- **File**: [`src/BinarySearch.java`](src/BinarySearch.java)
- **Time Complexity**: $O(\log N)$
- **Space Complexity**: $O(\log N)$ call-stack memory.
- **Precondition**: Operates on the Bubble-Sorted descending array.
- **Specification**: Divides the search partition in half on each recursive invocation, locating scores with logarithmic efficiency.

---

### 4. Call-Stack Recursion (Round Start Countdown)
- **File**: [`src/RecursionCountdown.java`](src/RecursionCountdown.java)
- **Algorithm**: True JVM Call-Stack Recursion.
- **Call-Stack Flow**:
  $$\text{countdown}(3) \;\longrightarrow\; \text{countdown}(2) \;\longrightarrow\; \text{countdown}(1) \;\longrightarrow\; \text{countdown}(0) \;[\text{Base Case}] \;\longrightarrow\; \text{START GAME}$$

---

### 5. Mathematical Scoring Recursion (Combo Multiplier)
- **File**: [`src/RecursionMath.java`](src/RecursionMath.java)
- **Time Complexity**: $O(N)$
- **Specification**: Dynamically computes food bonus points using mathematical recursion:
  $$\text{calculateScoreBonus}(L) = 2 + \text{calculateScoreBonus}(L - 1) \quad (\text{Base Case: } L \le 3 \implies 10)$$

---

## 📊 Algorithmic Complexity Analysis

| Operation / Feature | Class | Data Structure / Algorithm | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :---: | :---: |
| **Grid Cell Verification** | `GamePanel` | 2D Array (`int[][]`) | **$O(1)$** | $O(W \times H)$ |
| **Snake Head Advance** | `Snake` | Doubly Linked List (`addHead`) | **$O(1)$** | $O(1)$ |
| **Snake Tail Removal** | `Snake` | Doubly Linked List with Tail Pointer | **$O(1)$** | $O(1)$ |
| **Self Collision Traversal** | `Snake` | Doubly Linked List Traversal | **$O(N)$** | $O(1)$ |
| **Input Buffering** | `CustomQueue` | Hand-Built FIFO Queue | **$O(1)$** | $O(1)$ |
| **Milestone Logging** | `CustomStack` | Hand-Built LIFO Stack | **$O(1)$** | $O(1)$ |
| **Leaderboard Sorting** | `BubbleSort` | Bubble Sort on Plain Array | **$O(N^2)$** | $O(1)$ in-place |
| **Player Name Query** | `LinearSearch` | Linear Search with GUI Trace | **$O(N)$** | $O(1)$ |
| **Score Binary Query** | `BinarySearch` | Recursive Binary Search | **$O(\log N)$** | $O(\log N)$ stack |
| **Round Start Countdown**| `RecursionCountdown`| Call-Stack Recursion ($3 \to 0$) | **$O(K)$** | $O(K)$ stack |
| **Bonus Calculation** | `RecursionMath` | Mathematical Recursion | **$O(L)$** | $O(L)$ stack |
| **Safe Food Placement** | `Food` | Deterministic Array Scan | **$O(W \times H)$** | $O(W \times H)$ |

---

## 🏛 Class & Architecture Overview

```text
src/
├── Main.java                 # Entry point, initializes JFrame window and launches GamePanel
├── GamePanel.java            # Main controller, state machine, rendering engine, and input listener
├── SnakeNode.java            # Doubly Linked List Node (x, y, next, prev)
├── Snake.java                # Doubly Linked List with Head & Tail pointers (O(1) tail removal)
├── CustomQueue.java          # Hand-built FIFO Queue used for responsive input buffering
├── CustomStack.java          # Hand-built LIFO Stack used for game event and milestone history
├── Food.java                 # Safe deterministic food placement preventing infinite loops
├── Profile.java              # User Profile entity (player name, personal high score, game counts)
├── LeaderboardEntry.java     # Single record on the leaderboard (player name, score)
├── Leaderboard.java          # Plain array leaderboard repository
├── BubbleSort.java           # Descending Bubble Sort algorithm on plain arrays
├── LinearSearch.java         # Linear search algorithm with GUI in-window step trace
├── BinarySearch.java         # Recursive Binary Search algorithm on sorted array
├── RecursionCountdown.java   # Call-stack recursive countdown engine (3 -> 2 -> 1 -> GO!)
└── RecursionMath.java        # Recursive mathematical combo and score calculation
```

---

## 🎮 Game Features & UI Screens

1. **Main Menu**: Interactive navigation for `PLAY GAME`, `LEADERBOARD`, `HOW TO PLAY`, and `EXIT`.
2. **Profile Selection Screen**: Profile list (`Damidu`, `Amal`, `Nimal`, `Kasun`) with personal best scores and a **`+ CREATE NEW PROFILE`** prompt.
3. **Gameplay View**:
   - **Top HUD**: Displays `👤 PLAYER`, `SCORE`, `LEVEL`, `BEST`, `LENGTH`, and `SPEED`.
   - **Play Area**: $20 \times 15$ grid rendering green snake with directional eyes and red apple.
   - **Bottom Control Bar**: Interactive **`[❚❚ PAUSE]`** / **`[▶ RESUME]`** and **`[⟳ RESTART]`** buttons with Queue buffer status.
4. **In-Window Search Modal**: Renders complete comparison step traces on-screen for Linear Search and Recursive Binary Search.
5. **Game Over Screen**: Impact diagnostics, personal best alerts, and buttons for `PLAY AGAIN`, `SWITCH PROFILE`, `LEADERBOARD`, and `MAIN MENU`.

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
| **Linear Search** | `F` | — | Click `[ LINEAR SEARCH ]` button |
| **Recursive Binary Search** | `B` | — | Click `[ BINARY SEARCH ]` button |
| **Return to Menu** | `M` | `Escape` | Click `[ MAIN MENU ]` button |

---

## 🛠 Installation & Execution Guide

### Compiling from Terminal
```bash
mkdir -p out
javac -d out src/*.java
```

### Running from Terminal
```bash
java -cp out Main
```

### Running with NetBeans / Apache Ant
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
