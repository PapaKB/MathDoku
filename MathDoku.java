import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.Stage;

/**
 * The MathDuko class creates allows the user to play the MathDuko game.
 */
public class MathDoku extends Application {
	Stage stage;
	BorderPane borderpane;
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	ArrayList<ArrayList<String>> cageCells = new ArrayList<ArrayList<String>>();
	GridPane playingGrid;
	Button undo;
	public String[] changedCellValues = new String[2];
	ChangeListener<String> changeListener;
	Cell lastCellChanged;
	Cell lastCellFocused;
	Node lastVisitedButton;
	int gridSizeNumber = 0;
	
	/**
	 * Creates the Stage and BorderPane for the game..
	 * @param stage the Stage on which the game is played.
	 */
	public void start(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("MathDoku");
		this.borderpane = new BorderPane();
		Scene scene = new Scene(borderpane);
		this.stage.setScene(scene);
		this.stage.setMinHeight(500);
		this.stage.setMinWidth(560);
		getUserRequest();
	}
	
	/**
	 * Creates the interface for MathDuko.
	 */
	public void makeGrid() {
		this.playingGrid = new GridPane();
		this.stage.show();

		HBox buttonContainer = new HBox();
		buttonContainer.setAlignment(Pos.CENTER);
		
		this.undo = new Button("Undo");
		this.undo.setOnAction(e -> undo());
		
		Button redo = new Button("Redo");
		redo.setOnAction(e -> redo());

		Button clear = new Button("Clear");
		Alert confirmClear = new Alert(AlertType.CONFIRMATION,"Are you sure you want to clear the grid?");
		clear.setOnAction(e -> {
			confirmClear.showAndWait();
			if(confirmClear.getResult() == ButtonType.OK) {
				clear();
			}
		});
		
		Button showMistakes = new Button("Show Mistakes");
		showMistakes.setOnAction(e -> showMistakes());
		
		Button load = new Button("Load");
		load.setOnAction(e -> getUserRequest());
		
		Button solver = new Button("Solve");
		solver.setOnAction(e -> solver());
		solver.setPadding(new Insets(10));
		((Button)solver).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		Button complete = new Button("Complete");
		complete.setOnAction(e -> complete());
		complete.setStyle("-fx-base: #AEFFB0;");
		complete.setPadding(new Insets(10));
		((Button)complete).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		complete.setOnKeyPressed(e -> keyPressedOnComplete(e, solver));
		solver.setOnKeyPressed(e -> keyPressedOnSolver(e, complete));

		Slider fontSizeSlider = new Slider(0, 3, 0);
		fontSizeSlider.setShowTickMarks(true);
		fontSizeSlider.setMajorTickUnit(1);
		fontSizeSlider.setBlockIncrement(0.25);
		fontSizeSlider.setSnapToTicks(true);
		fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			for(Cell cell : this.cellList) {
				cell.getCenterLabel().setFont(Font.font(15 + 5*(double)newValue));
				cell.getTopLabel().setFont(Font.font(10 + (double)newValue));
			}
		});
		fontSizeSlider.setMaxSize(200, 200);
		HBox buttonContainer2 = new HBox(20);
		HBox.setHgrow(fontSizeSlider, Priority.ALWAYS);
		HBox.setHgrow(complete, Priority.ALWAYS);
		HBox.setHgrow(solver, Priority.ALWAYS);
		buttonContainer2.setAlignment(Pos.CENTER);
		buttonContainer2.getChildren().addAll(fontSizeSlider, solver, complete);
		
		this.undo.setOnKeyPressed(e -> keyPressedOnButtonTopRow(e, redo, load, solver));
		redo.setOnKeyPressed(e -> keyPressedOnButtonTopRow(e, clear, load, solver));
		clear.setOnKeyPressed(e -> keyPressedOnButtonTopRow(e, showMistakes, this.undo, solver));
		showMistakes.setOnKeyPressed(e -> keyPressedOnButtonTopRow(e, load, clear, solver));
		load.setOnKeyPressed(e -> keyPressedOnButtonTopRow(e, this.undo, showMistakes, solver));
		
		buttonContainer.getChildren().addAll(undo, redo, clear, showMistakes, load);
		
		for(Node button : buttonContainer.getChildren()) {
			button.setFocusTraversable(false);
			((Region) button).setPadding(new Insets(10));
			HBox.setMargin(button, new Insets(10, 20, 0, 20));
			((Button)button).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			HBox.setHgrow(button, Priority.ALWAYS);
			button.focusedProperty().addListener(e -> { 
				if(button.isFocused()) {
					this.lastVisitedButton =  button; 
				}	
			});
		}
		
		VBox buttonContainerVBox = new VBox(20);
		buttonContainerVBox.setFillWidth(true);
		buttonContainerVBox.setAlignment(Pos.CENTER);	
		buttonContainerVBox.getChildren().addAll(buttonContainer, buttonContainer2);
		this.borderpane.setBottom(buttonContainerVBox);
		BorderPane.setMargin(this.borderpane.getBottom(), new Insets(10));
		VBox.setMargin(complete, new Insets(0,30,30,30));
		buttonContainerVBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 0, 0, 0))));
	
		
		for (int i = 0 ; i < this.gridSizeNumber ; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setFillWidth(true);
            column.setPercentWidth(100/this.gridSizeNumber);
            column.setHgrow(Priority.ALWAYS);
            this.playingGrid.getColumnConstraints().add(column);
        }
		
		for (int i = 0 ; i < this.gridSizeNumber ; i++) {
            RowConstraints row = new RowConstraints();
            row.setFillHeight(true);
            row.setPercentHeight(100/this.gridSizeNumber);
            row.setVgrow(Priority.ALWAYS);
            this.playingGrid.getRowConstraints().add(row);
        }
		
		this.playingGrid.setMaxHeight(this.stage.getHeight()-188);
		this.playingGrid.setMaxWidth(this.playingGrid.getMaxHeight());
		this.stage.heightProperty().addListener((observable, oldValue, newValue) -> {
			double difference = (double)newValue - (double)oldValue ;
			if(playingGrid.getWidth() < this.stage.getWidth()) {
				this.playingGrid.setMaxHeight(this.playingGrid.getHeight()+difference);
				this.playingGrid.setMaxWidth(this.playingGrid.getHeight()+difference);
				this.playingGrid.setMinWidth(this.playingGrid.getHeight()+difference);
			}
		});
		
		this.stage.widthProperty().addListener((observable, oldValue, newValue) -> {
			this.playingGrid.setMinWidth(this.playingGrid.getHeight());
		});
	
		this.borderpane.setCenter(this.playingGrid);
		this.playingGrid.setAlignment(Pos.CENTER);
		BorderPane.setMargin(this.playingGrid, new Insets(20, 20, 20, 20)); 
		
		this.changeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
			this.changedCellValues[0] = oldValue;
			this.changedCellValues[1] = newValue;};
		    
		int cellID = 0;
		for (int i=0; i< this.gridSizeNumber; i++) {
			for (int j=0; j< this.gridSizeNumber; j++) {
				Cell cell = new Cell(this.playingGrid, gridSizeNumber, i, j);
				cellID += 1;
				cell.setID(cellID);
				this.cellList.add(cell);
				this.playingGrid.add(cell, j,i);
				cell.getCenterLabel().textProperty().addListener(this.changeListener);
				cell.focusedProperty().addListener(e -> {
					if(cell.isFocused()) {
						this.lastCellFocused = cell;
						cell.getCenterLabel().setBackground(new Background(new BackgroundFill (Color.web("#DBFBFF"),  CornerRadii.EMPTY , Insets.EMPTY)));
						cell.getTopLabel().setBackground(new Background(new BackgroundFill (Color.web("#DBFBFF"),  CornerRadii.EMPTY , Insets.EMPTY)));
						cell.getBottomLabel().setBackground(new Background(new BackgroundFill (Color.web("#DBFBFF"),  CornerRadii.EMPTY , Insets.EMPTY)));
					}
					else {
						cell.getCenterLabel().setBackground(new Background(new BackgroundFill (Color.LIGHTBLUE,  CornerRadii.EMPTY , Insets.EMPTY)));
						cell.getTopLabel().setBackground(new Background(new BackgroundFill (Color.LIGHTBLUE,  CornerRadii.EMPTY , Insets.EMPTY)));
						cell.getBottomLabel().setBackground(new Background(new BackgroundFill (Color.LIGHTBLUE,  CornerRadii.EMPTY , Insets.EMPTY)));
					}
				});
				
				cell.setOnKeyPressed(e -> keyPressedOnCell(e, this.gridSizeNumber));

				if(i > 0) {
					cell.setTopCellID(cell.getID()-this.gridSizeNumber);
				}
				if(i < this.gridSizeNumber-1) {
					cell.setBottomCellID(cell.getID()+this.gridSizeNumber);
				}
				if(j > 0) {
					cell.setLeftCellID(cell.getID()-1);
				}
				if (j < this.gridSizeNumber-1) {
					cell.setRightCellID(cell.getID()+1);
				}
				
				if(i==0 && j==0) {
					cell.requestFocus();
					this.lastCellChanged = cell;
				}
			}
		}

		if(!(this.cageCells.isEmpty())) {
			Cage cage = new Cage(this.cellList);
			for (ArrayList<String> groupOfCells : this.cageCells) {
				String[] cageArray = new String[groupOfCells.size()];
				cageArray = groupOfCells.toArray(cageArray);
				cage.makeCage(cageArray);
			}
		}
	}
	
	/**
	 * Gets the user's request in terms of whether they'd like to load a default grid or load a grid or enter in grid information.
	 */
	public void getUserRequest() {
		ComboBox<String> gridSizeSelection = new ComboBox<String>();
		gridSizeSelection.getItems().addAll("2x2", "3x3", "4x4", "5x5", "6x6", "7x7", "8x8", "Load a file");
		
		Label instruction = new Label("Enter the details of your MathDuko grid below:");
		TextArea gridDetails = new TextArea();
		gridDetails.setWrapText(true);
		
		GridPane expandableContent = new GridPane();
		expandableContent.setMaxWidth(Double.MAX_VALUE);
		expandableContent.add(instruction, 0, 0);
		expandableContent.add(gridDetails, 0, 1);
		
		Alert alert = new Alert(AlertType.NONE,"", ButtonType.NEXT, ButtonType.CANCEL);
		alert.setTitle("MathDoku");
		alert.setHeaderText("Please select a grid size.");
		alert.getDialogPane().setContent(gridSizeSelection);
		alert.getDialogPane().setExpandableContent(expandableContent);
		alert.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> alert.close());
		
		boolean pass = false;
		while(pass == false) {
			alert.showAndWait();
			String gridInformation = null;
			if(alert.getResult() == ButtonType.NEXT) {
				if(gridSizeSelection.getValue() == "2x2") {
					this.gridSizeNumber = 2;
					gridInformation = "4x 1,2,3\n" + 
									  "1 4";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "3x3") {
					this.gridSizeNumber = 3;
					gridInformation = "5+ 1,2\n" + 
							"2- 3,6\n" + 
							"1- 4,7\n" + 
							"4+ 5,8\n" + 
							"2 9";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "4x4") {
					this.gridSizeNumber = 4;
					gridInformation = "6+ 1,2\n" + 
							"4+ 3,7\n" + 
							"3- 4,8\n" + 
							"5+ 5,6\n" + 
							"3- 9,13\n" + 
							"6+ 10,11,12\n" + 
							"9+ 14,15,16";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "5x5") {
					this.gridSizeNumber = 5;
					gridInformation = "3+ 1,6\n" + 
							"2- 2,7\n" + 
							"8+ 3,4,5\n" + 
							"3- 8,9\n" + 
							"9+ 10,15,20\n" + 
							"9+ 11,12\n" + 
							"3+ 13,14\n" + 
							"7+ 16,21\n" + 
							"4+ 17,22,23\n" + 
							"9+ 18,19\n" + 
							"2- 24,25";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "6x6") {
					this.gridSizeNumber = 6;
					gridInformation = "10+ 1,7,13\n" + 
							"4÷ 2,3\n" + 
							"6x 4,5\n" + 
							"11+ 6,12\n" + 
							"7+ 8,9\n" + 
							"12+ 10,11,16\n" + 
							"6÷ 14,15\n" + 
							"6+ 17,18\n" + 
							"30x 19,20,21,27\n" + 
							"12x 22,28\n" + 
							"24x 23,24\n" + 
							"3÷ 25,26\n" + 
							"4- 29,30\n" + 
							"16+ 31,32,33,34\n" + 
							"6x 35,36";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "7x7") {
					this.gridSizeNumber = 7;
					gridInformation = "8+ 1,8\n" + 
							"1- 2,3\n" + 
							"3+ 4,5\n" + 
							"13+ 6,7\n" + 
							"13+ 9,10\n" + 
							"2 11\n" + 
							"6- 12,19\n" + 
							"11+ 13,14,20\n" + 
							"11+ 15,22,29\n" + 
							"8+ 16,23\n" + 
							"10+ 17,24\n" + 
							"10+ 18,25\n" + 
							"1 21\n" + 
							"4 26\n" + 
							"2- 27,34\n" + 
							"5- 28,35\n" + 
							"2 30\n" + 
							"8+ 31,38,45\n" + 
							"1- 32,39\n" + 
							"3- 33,40\n" + 
							"5- 36,43\n" + 
							"6- 37,44\n" + 
							"7+ 41,48\n" + 
							"1- 42,49\n" + 
							"9+ 47,46";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "8x8") {
					this.gridSizeNumber = 8;
					gridInformation = "14x 1,9\n" + 
							"2- 2,3\n" + 
							"1- 4,12\n" + 
							"5- 5,6\n" + 
							"15x 7,8,16\n" + 
							"7- 10,11\n" + 
							"4÷ 13,21\n" + 
							"7+ 14,22\n" + 
							"4- 15,23\n" + 
							"40x 17,25\n" + 
							"2- 18,19\n" + 
							"7+ 20,28\n" + 
							"392x 24,32,31\n" + 
							"3+ 26,27\n" + 
							"10+ 29,30\n" + 
							"3÷ 33,41\n" + 
							"15+ 34,42\n" + 
							"70x 35,36,44\n" + 
							"5 37\n" + 
							"72x 38,46,47\n" + 
							"5+ 39,40\n" + 
							"1- 43,51\n" + 
							"112x 45,53,61\n" + 
							"30x 48,56\n" + 
							"2- 49,50\n" + 
							"7- 52,60\n" + 
							"15+ 55,54\n" + 
							"6 57\n" + 
							"9+ 58,59\n" + 
							"6+ 64,63,62";
					pass = true;
				}
				else if(gridSizeSelection.getValue() == "Load a file"){
					if(load() == true) {
						pass = true;
					}
					else {
						alert.setOnCloseRequest(e -> System.exit(0));
					}
				}
				else if(!(gridDetails.getText().equals(""))) {
					gridInformation = gridDetails.getText();
					pass = true;
				}
				if(gridInformation != null) {
					this.cellList.clear();
					this.cageCells.clear();
					String[] line = (gridInformation.split("\n"));
					int cellNumberTotal = 0;
					for(String cage : line) {
						ArrayList<String> oneCage = new ArrayList<String>();
						String[] line2 = cage.split(",");
						for(String cellValue : line2) {
							if(cellValue == line2[0]) {
								oneCage.add(line2[0].split(" ")[0]);
								oneCage.add(line2[0].split(" ")[1]);
							}
							else {
								oneCage.add(cellValue);
							}
							cellNumberTotal += 1;
						}	
						this.cageCells.add(oneCage);
					}
					this.gridSizeNumber = (int) Math.pow(cellNumberTotal, 0.5);
					makeGrid();
				}
			}
			else {
				pass = true;
			}
		}
	}
	
	/**
	 * Handles keyboard input on buttons excluding the complete button.
	 * @param event The event which calls the method.
	 * @param rightButton The button to the right of the button which is the source of the event.
	 * @param leftButton The button to the left of the button which is the source of the event.
	 * @param bottomButton The button beneath the button which is the source of the event.
	 */
	public void keyPressedOnButtonTopRow(KeyEvent event, Button rightButton, Button leftButton, Button bottomButton) {
		if(event.getCode().equals(KeyCode.TAB)) {
			this.lastCellFocused.requestFocus();
		}
		else if(event.getCode() == KeyCode.RIGHT) {
			rightButton.requestFocus();
		}
		else if(event.getCode() == KeyCode.LEFT) {
			leftButton.requestFocus();
		}
		else if(event.getCode() == KeyCode.DOWN) {
			bottomButton.requestFocus();
		}
		event.consume();
	}
	
	/**
	 * Handles keyboard input on the complete button.
	 * @param event The event which calls the method.
	 */
	public void keyPressedOnComplete(KeyEvent event, Button leftButton) {
		if(event.getCode() == KeyCode.UP) {
			this.lastVisitedButton.requestFocus();
		}
		else if(event.getCode().equals(KeyCode.TAB)) {
			this.lastCellFocused.requestFocus();
		}
		else if (event.getCode().equals(KeyCode.LEFT)) {
			leftButton.requestFocus();
		}
	}
	
	/**
	 * Handles keyboard input on the solver button.
	 * @param event The event which calls the method.
	 */
	public void keyPressedOnSolver(KeyEvent event, Button rightButton) {
		if(event.getCode() == KeyCode.UP) {
			this.lastVisitedButton.requestFocus();
		}
		else if(event.getCode().equals(KeyCode.TAB)) {
			this.lastCellFocused.requestFocus();
		}
		else if (event.getCode().equals(KeyCode.RIGHT)) {
			rightButton.requestFocus();
		}
	}
	
	/**
	 * Handles keyboard input on a Cell.
	 * @param event The event which calls the method.
	 * @param maximumValue The maximum integer value that can be entered.
	 */
	public void keyPressedOnCell(KeyEvent event, int maximumValue) {
		
		Labeled label = ((Labeled) ((Cell) event.getSource()).getCenterLabel());

		for(int i = 1; i <= maximumValue; i++) {
			if(event.getText().equals(Integer.toString(i))) {
				this.lastCellChanged = (Cell) event.getSource();
				label.setText(event.getText());
			}
		}
		
		if(event.getCode().equals(KeyCode.TAB)) {
			if(this.lastVisitedButton != null) {
				this.lastVisitedButton.requestFocus();
			}
			else {
				this.undo.requestFocus();
			}
		}
			
		else if(event.getCode().equals(KeyCode.BACK_SPACE)) {
			label.setText("");
		}
		
		else if(event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.RIGHT)) {
			int eventSourceRowIndex = ((Cell) event.getSource()).getIndexArray()[0];
			int eventSourceColumnIndex = ((Cell) event.getSource()).getIndexArray()[1];
			int newFocusRowIndex = 0;
			int newFocusColumnIndex = 0;
			
			if(event.getCode().equals(KeyCode.UP)) {
				newFocusRowIndex = eventSourceRowIndex-1;
				newFocusColumnIndex = eventSourceColumnIndex;
			}
			else if(event.getCode().equals(KeyCode.DOWN)) {
				newFocusRowIndex = eventSourceRowIndex+1;
				newFocusColumnIndex = eventSourceColumnIndex;
			}
			else if(event.getCode().equals(KeyCode.LEFT)) {
				newFocusRowIndex = eventSourceRowIndex;
				newFocusColumnIndex = eventSourceColumnIndex-1;
			}
			else if(event.getCode().equals(KeyCode.RIGHT)) {
				newFocusRowIndex = eventSourceRowIndex;
				newFocusColumnIndex = eventSourceColumnIndex+1;
			}

			for(Cell cell : this.cellList) {
				if(cell.getIndexArray()[0] == newFocusRowIndex && cell.getIndexArray()[1] == newFocusColumnIndex) {
					cell.requestFocus();
				}
			}
		}
		event.consume();
	}
	
	/**
	 * Undoes the most recent user action within the MathDuko grid.
	 */
	public void undo() {
		this.lastCellChanged.getCenterLabel().textProperty().removeListener(this.changeListener);
		this.lastCellChanged.getCenterLabel().setText(this.changedCellValues[0]);
		this.lastCellChanged.getCenterLabel().textProperty().addListener(this.changeListener);
	}
	
	/**
	 * The redo() method will re-do a recently undone user action within the MathDuko grid.
	 */
	public void redo() {
		this.lastCellChanged.getCenterLabel().textProperty().removeListener(this.changeListener);
		this.lastCellChanged.getCenterLabel().setText(this.changedCellValues[1]);
		this.lastCellChanged.getCenterLabel().textProperty().addListener(this.changeListener);
	}
	
	/**
	 * Clears user input in the MathDuko grid.
	 */
	public void clear() {
		for(Cell cell : this.cellList) {
			cell.getCenterLabel().setText("");
		}
	}
	
	private int isInRowShowMistakes(int row, int number) {
		int repeatCount = 0;
		for(Cell cell : this.cellList) {
			if(cell.getIndexArray()[0] == row) {
				if(cell.getCenterLabel().getText().equals(Integer.toString(number))) {
					repeatCount += 1;
				}
			}
		}
		return repeatCount;
	}
	
	private int isInColumnShowMistakes(int column, int number) {
		int repeatCount = 0;
		for(Cell cell : this.cellList) {
			if(cell.getIndexArray()[1] == column) {
				if(cell.getCenterLabel().getText().equals(Integer.toString(number))) {
					repeatCount += 1;
				}
			}
		}
		return repeatCount;
	}
	/**
	 * Shows the mistakes in a MathDuko grid out of what the user has already filled.
	 */
	public void showMistakes() {
		for(int row = 0; row < this.gridSizeNumber; row++) {
			for(int column = 0; column < this.gridSizeNumber; column++) {
				for(Cell cell : this.cellList) {
					if(cell.getIndexArray()[0] == row && cell.getIndexArray()[1] == column && cell.getCenterLabel().getText()!= "") {
						if(isInRowShowMistakes(row, Integer.parseInt(cell.getCenterLabel().getText()))>1 ||  isInColumnShowMistakes(column, Integer.parseInt(cell.getCenterLabel().getText()))>1) {
							cell.getCenterLabel().setTextFill(Color.RED);
							cell.getCenterLabel().textProperty().addListener(e -> cell.getCenterLabel().setTextFill(Color.BLACK));

						}
						else {
							cell.getCenterLabel().setTextFill(Color.BLACK);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Loads a file from the user's device.
	 * @return a boolean indicating if a file can be successfully loaded.
	 */
	public boolean load() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Open File to Load");
		ExtensionFilter txtFilter = new ExtensionFilter("Text files",
				"*.txt");
		fileChooser.getExtensionFilters().add(txtFilter);

		File file = fileChooser.showOpenDialog(this.stage);

		if (file != null && file.exists() && file.canRead()) {
			try {
				BufferedReader buffered = new BufferedReader(new FileReader(file));
				String line;
				this.cellList.clear();
				this.cageCells.clear();
				int cellNumberTotal = 0;
				while ((line = buffered.readLine()) != null) {
					ArrayList<String> oneCage = new ArrayList<String>();
					String[] firstSplit = line.split(",");
					for(String cellValue : firstSplit) {
						if(cellValue == firstSplit[0]) {
							oneCage.add(((firstSplit[0].split(" "))[0]));
							oneCage.add((firstSplit[0].split(" "))[1]);
						}
						else {
							oneCage.add(cellValue);
						}
						cellNumberTotal += 1;
					};
					this.cageCells.add(oneCage);		
				}
				this.gridSizeNumber = (int) Math.pow(cellNumberTotal, 0.5);
				buffered.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			makeGrid();
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isInRow(int row, int number) {
		for(Cell cell : this.cellList) {
			if(cell.getIndexArray()[0] == row) {
				if(cell.getCenterLabel().getText().equals(Integer.toString(number))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isInColumn(int column, int number) {
		for(Cell cell : this.cellList) {
			if(cell.getIndexArray()[1] == column) {
				if(cell.getCenterLabel().getText().equals(Integer.toString(number))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean correctCageTotal(Cell cell, int number) {
		int cageTargetTotal = 0;
		int total = 0;
		int highestCellID = 0;
		boolean cellFound = false;
		String[] cageArray = null;
		int emptyCellCount = 0;
		
		while(true) {
			for(ArrayList<String> groupOfCells : this.cageCells) {
				emptyCellCount = 0;
				cellFound = false;
				highestCellID = 0;
				
				cageArray = new String[groupOfCells.size()];
				cageArray = groupOfCells.toArray(cageArray);
	
				for(int i = 1; i < cageArray.length; i++) {
					for(Cell cellInList : this.cellList) {
						
						if(Integer.toString(cellInList.getID()).equals(cageArray[i])) {
							if(cellInList.getCenterLabel().getText().equals("")) {
								emptyCellCount += 1;
							}
							if(Integer.parseInt(cageArray[i]) > highestCellID) {
								highestCellID = Integer.parseInt(cageArray[i]);
							}
						}
						if(cageArray[i].equals(Integer.toString(cell.getID()))) {
							cellFound = true;
						}
					}
				}
				if(cellFound) {
					break;
				}
			}
			break;
		}
		
		
		if(emptyCellCount == 1 && cellFound == true && cell.getID() == highestCellID) {

			if(Character.isDigit(cageArray[0].charAt(0))) {
				cageTargetTotal = Character.getNumericValue(cageArray[0].charAt(0));
			}

			ArrayList<Integer> cageCellValues = new ArrayList<Integer>() ;
			cageCellValues.add(number);
			for(int i = 1; i < cageArray.length; i++) {

				for(Cell cellInList : this.cellList) {
					if(Integer.toString(cellInList.getID()).equals(cageArray[i]) && !(cellInList.getID() == highestCellID)) {
						if(cageArray[0].contains("+")) {
							total += Integer.parseInt(cellInList.getCenterLabel().getText());
						}
						else if(cageArray[0].contains("x")) {
							if(total == 0) {
								total = 1;
							}
							total *= Integer.parseInt(cellInList.getCenterLabel().getText());
						}
						else if(cageArray[0].contains("÷")) {
							cageCellValues.add(Integer.parseInt(cellInList.getCenterLabel().getText()));

							if(cageCellValues.size() == cageArray.length-1) {
								total += Collections.max(cageCellValues);
								cageCellValues.remove(Integer.valueOf(total));
								for(int cellValue : cageCellValues) {
									total /= cellValue;
								}
							}
						}
						else if(cageArray[0].contains("-")) {
							cageCellValues.add(Integer.parseInt(cellInList.getCenterLabel().getText()));

							if(cageCellValues.size() == cageArray.length-1) {
								total += Collections.max(cageCellValues);
								cageCellValues.remove(Integer.valueOf(total));
								for(int cellValue : cageCellValues) {
									total -= cellValue;
								}
							}
						}
					}
				}
			}

			if(cageArray[0].contains("+") || cageArray.length == 2) {
				total += number;
			}
			else if(cageArray[0].contains("x")) {
				total *= number;
			}

			if(total == cageTargetTotal) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}	

		
	private boolean isOk(int row, int column, int number, Cell cell) {
		return !isInRow(row, number)  &&  !isInColumn(column, number) && correctCageTotal(cell, number);
	}	
	
	public boolean solve() {
		for(int row = 0; row < this.gridSizeNumber; row++) {
			for(int column = 0; column < this.gridSizeNumber; column++) {
				for(Cell cell : this.cellList) {
					if(cell.getIndexArray()[0] == row && cell.getIndexArray()[1] == column && cell.getCenterLabel().getText()== "") {
						for(int number = 1; number <= this.gridSizeNumber; number++) {
							if(isOk(row, column, number, cell)) {
								cell.getCenterLabel().setText(Integer.toString(number));
								if(solve()) { 
									return true;
								} 
								else { 
									cell.getCenterLabel().setText("");
								}
							}
						}
						return false; 
					}
				}
			}
		}

		return true; 
	}
	
	private void solver() {
		if(!solve()) {
			Text unsolvable = new Text("Unsolvable");
			unsolvable.setFont(Font.font("Arial", this.stage.getWidth()/15));
			unsolvable.setFill(Color.RED);
			unsolvable.setLayoutX(this.stage.getWidth()*0.35);
			unsolvable.setLayoutY(this.stage.getHeight()*0.45);
			this.borderpane.getChildren().add(unsolvable);
			
			FadeTransition fade = new FadeTransition(Duration.seconds(1), unsolvable);
			fade.setFromValue(1);
		    fade.setToValue(0);
		    fade.play();
		}

	}
	/**
	 * Produces the animation when the user completes the MathDuko game.
	 */
	public void complete() {
		
		double width = 0;
		for(int i=0 ; i<4; i++) {
			Text youWin = new Text("YOU WIN!");
			youWin.setFont(Font.font("Arial", this.stage.getWidth()/20));
			youWin.setFill(Color.MEDIUMPURPLE);
			youWin.setLayoutX(width+5);
			width = width + this.stage.getWidth()/4;
			youWin.setLayoutY(this.stage.getHeight());
			this.borderpane.getChildren().add(youWin);
			
			TranslateTransition translation1 = new TranslateTransition(Duration.seconds(0.5), youWin);
			translation1.setToY(-1*(this.stage.getHeight()*(0.75)));

			RotateTransition rotate = new RotateTransition(Duration.seconds(1), youWin);
			rotate.setToAngle(360);
			rotate.setAutoReverse(true);
			
			FadeTransition fade = new FadeTransition(Duration.seconds(1), youWin);
			fade.setFromValue(1);
		    fade.setToValue(0);
			
			ParallelTransition parallel = new ParallelTransition(rotate, translation1);
			
			SequentialTransition sequence = new SequentialTransition(parallel, fade);
			sequence.play();
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
}

