<p align="center">
  <img src="ucubadge.jpeg" alt="Uganda Christian University" width="360">
</p>

# CSC2107 Algorithm Explorer

**Group 9** · CSC2107 Design and Analysis of Algorithms · Uganda Christian University · Advent Semester 2026

Algorithm Explorer is a single desktop application. The user types their own input, the algorithm runs in the back end,
and the screen shows the result, the steps taken, the number of operations and the running time.

- **Repository (public):** <https://github.com/kiconcopreciouskellen/GROUP9>
- **Language:** Java (JDK 11 or newer), graphical interface built with Swing
- **Dependencies:** none. No external libraries, no package manager, no build tool.

---

## 1. Requirements and installation

The only thing to install is a **Java Development Kit (JDK), version 11 or newer** (the `javac` compiler is needed,
so a JRE alone is not enough).

1. Check whether it is already installed:
   ```
   javac -version
   java -version
   ```
2. If not, install a JDK (for example Temurin/OpenJDK 17 or 21 from <https://adoptium.net>).
   On Ubuntu/Debian: `sudo apt install default-jdk`.
3. Download or clone the repository, then go into the project folder (the one that contains `src`, `docs` and `Tests`):
   ```
   git clone https://github.com/kiconcopreciouskellen/GROUP9.git
   cd GROUP9
   cd "ALGORITHM EXPLORER ASSIGNMENT"
   ```
   (Adjust the folder names if your repository is named differently.)

There are no other dependencies to install. A desktop environment is needed because the program opens a window.

---

## 2. How to compile and run

**Linux / macOS**
```
javac -d out $(find src -name '*.java')
java -cp out ui.Main
```

**Windows (Command Prompt)**
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out ui.Main
```

**From an IDE (IntelliJ IDEA, Eclipse, VS Code):** open the project folder, mark `src` as the sources root, and run the
`main` method in `ui.Main`.

The main window opens with four tabs (A to D, described below).

### Running the tests

The back end contains no Swing code, so it is tested without the window. After compiling as above:

**Linux / macOS and Windows**
```
javac -cp out -d out Tests/AlgorithmTests.java
java -cp out AlgorithmTests
```
A successful run ends with `ALL TESTS PASSED`. The tests compare every sort with the JDK sort, the stack, queues and
lists with JDK collections, and replay every Tower of Hanoi solution.

---

## 3. The four modules

| Tab | Contents |
|---|---|
| **A: Recursion Lab** | Fibonacci (naive, memoized, bottom-up), factorial (recursive, iterative), animated Tower of Hanoi |
| **B: Data Structures Lab** | Stack, linear queue, circular queue, singly linked list, doubly linked list, bracket checker |
| **C: Sorting Lab** | Bubble, selection, insertion, merge, quicksort (first pivot and random pivot); step view, auto-play, compare all |
| **D: Time Complexity Analysis** | Timing experiment, tables, charts, CSV export and PNG chart export |

All algorithms and data structures are written by us from first principles. The application code uses no built-in sort
or search functions and no ready-made stack, queue or list classes. Data structures use plain arrays and our own `Node`
classes.

### Using the application

1. **Module A:** type `n`, press the compute button, read the result, call count and time in the table. In the Tower of
   Hanoi screen choose the number of disks and the source, destination and auxiliary pegs, then use Play, Pause or Step.
2. **Module B:** choose a structure, set the capacity, then use the buttons (push, pop, enqueue, dequeue, insert, delete,
   search, display). Error messages (for example stack overflow or underflow) are shown on screen.
3. **Module C:** type numbers separated by commas, or generate random ones. Choose an algorithm and press *Run sort*.
   Use Step, Back, Auto-play and Reset to replay the steps. *Compare all* runs the six algorithms on the same input.
4. **Module D:** press the run button. The experiment runs in the background with a progress bar, then fills the
   tables and charts. *Export CSV* saves all numbers and *Save charts* saves the charts as PNG files.

---

## 4. Architecture

The code is split into a **front end** and a **back end**.

- **Front end** (`src/ui`): all screens. It draws buttons, tables and pictures and contains no algorithm logic.
- **Back end** (`src/recursion`, `src/structures`, `src/sorting`, `src/analysis`, `src/core`): the algorithms and data
  structures. It contains no Swing code.

```
GROUP9/
  ALGORITHM EXPLORER ASSIGNMENT/
    src/
      core/        Metrics (operation counters), ArrayUtil (input checks), Timing, BigStack (large-stack thread)
      recursion/   FibonacciAlgorithms, FactorialAlgorithms, TowerOfHanoi, HanoiMove               (Module A back end)
      structures/  CustomStack, ArrayQueue, LinearQueue, CircularQueue, SinglyLinkedList,
                   DoublyLinkedList, BracketChecker                                                (Module B back end)
      sorting/     BubbleSort, SelectionSort, InsertionSort, MergeSort, QuickSort,
                   SortTracer, SortRegistry                                                        (Module C back end)
      analysis/    InputGenerator, Benchmark                                                       (Module D back end)
      ui/          Main, MainPanel, UiUtil and one panel per lab                                   (front end)
    Tests/         AlgorithmTests.java (back-end tests that run without the interface)
    docs/          One text file per team member describing their work
```

**What happens when a button is clicked** (example: Fibonacci):
the panel reads the text → `ArrayUtil` validates it → the panel calls the back end with a `Metrics` object → the
algorithm updates the counters while it runs → the panel reads the result and counters, measures time with `Timing`, and
shows them in a table.

### What is counted

| Module | Operation counters |
|---|---|
| Recursion | Function calls, or loop iterations for the iterative and bottom-up versions |
| Sorting | Comparisons, and swaps or moves (swaps for bubble, selection, insertion and quicksort; array writes for merge sort) |
| Timing | `System.nanoTime()`, average of repeated runs, measured with step recording switched off |

---

## 5. Input limits

Invalid input shows a message on screen instead of crashing.

| Area | Limit |
|---|---|
| Fibonacci | n from 0 to 90 (naive recursion only up to 30, because it makes about 1.6^n calls) |
| Factorial | n from 0 to 20 (21! does not fit in a 64-bit integer) |
| Tower of Hanoi | 1 to 10 disks |
| Stack / queues | Capacity 1 to 12 (stack), 1 to 10 (queues) |
| Sorting | Up to 100,000 numbers (step view up to 40 numbers) |
| Module D experiment | n = 100 to 10,000 for all six sorts; n = 50,000 and 100,000 for merge sort and both quicksorts |

---

## 6. Team and contributions

| Member | Name | Reg. No. | GitHub username | Area | Notes file (in `docs/`) |
|---|---|---|---|---|---|
| 1 | Kiconco Precious Kellen | M25B23/053 | `kiconcopreciouskellen` | Core utilities, Module A back end, Fibonacci and Factorial screens | `member-1-core-and-recursion.txt` |
| 2 | Winyi Alvin Mugisha | M25B23/089 | `alvinwinyi2025` | Module B: stack, queues, bracket checker | `member-2-stack-queues-brackets.txt` |
| 3 | Muduwa Angel | M25B23/015 | `angelmuduwa` | Module B: linked lists, Data Structures tab | `member-3-linked-lists.txt` |
| 4 | Muloosi Amon | M25B23/023 | `amonrafe94-lang` | Module C: Sorting Lab; animated Tower of Hanoi screen (`HanoiCanvas`, `HanoiPanel`) and `RecursionTab` | `MULOOSIAMONSORTING.txt` |
| 5 | Among Sherinah | M25B23/045 | `amongsherinah-netizen` | Module D: analysis, main window, tests | `member-5-analysis-and-main.txt` |

Each member committed their own files from their own GitHub account, so every member's contribution is visible in the
repository's commit history (the **Commits** page, or **Insights > Contributors**). Each notes file in `docs/`
describes that member's files and ideas.

## 7. AI tools

AI assistance was used for parts of this project. See the "Use of AI tools" section of the project report.

## 8. References

Cormen, T. H., Leiserson, C. E., Rivest, R. L. and Stein, C., *Introduction to Algorithms*, MIT Press.
