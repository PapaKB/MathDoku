# MathDoku
A math based version of Sudoku written in Java in which the elements in a cage must sum, multiply, divide or subtract, to equal a given number. The given number and the operation is given in the corner of the cage. The grid can be loaded from a text file or the user can enter an input.

## What I Learned
* Further uses of classes
* Constructing UIs in JavaFX

## How to Run
***Full explantation of running code with javafx jars through termnal: https://openjfx.io/openjfx-docs/#install-javafx***

To run the program from a terminal on a Windows computer first change the directory to that containing the MathDoku.java, Cell.java and Cage.java files. 

**Export the path of the folder holding the javafx jars to a variable**

On Windows:

```set PATH_TO_FX=[path to javafx lib folder]```

On Linux/Mac:

```export PATH_TO_FX=[path to javafx lib folder]```

**Compilation**

On Windows:

```javac --module-path %PATH_TO_FX% --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web MathDoku.java Cell.java Cage.java```

On Linux/Mac:

```javac --module-path $PATH_TO_FX --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web MathDoku.java Cell.java Cage.java```

**Run the program**

On Windows:

```java --module-path %PATH_TO_FX% --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web MathDoku.java```

On Linux/Mac:

```java --module-path $PATH_TO_FX --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web MathDoku.java Cell.java Cage.java```

Please note that to enter a string input for a grid, you must expand the load alert and enter the details in the expandable dialog pane.

## Demonstration
https://user-images.githubusercontent.com/81168517/178467379-07379901-3cb6-4c4f-93dd-8f07200d71fa.mp4
