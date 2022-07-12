import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The Cell class creates an object for every section in a GridPane.
 */
public class Cell extends BorderPane {
	private GridPane gridpane;
	private int[] indexArray = new int[2];
	private Label centerLabel;
	private Label topLabel;
	private Label bottomLabel;
	private int id;
	private int rightCellID;
	private int leftCellID;
	private int topCellID;
	private int bottomCellID;
	

	/**
	 * Creates a Cell object and its contents.
	 * @param gridpane The GridPane that a Cell is created for.
	 * @param maximumNumber The maximum number that can be entered
	 * @param rowIndex The row index for where a Cell is to be located in the GridPane.
	 * @param columnIndex The column index for where the cell is to be located in the GridPane.
	 */
	public Cell(GridPane gridpane, int maximumNumber, int rowIndex, int columnIndex) {
		this.gridpane = gridpane;
		this.indexArray[0] = rowIndex;
		this.indexArray[1]=columnIndex;
		this.centerLabel = new Label();
		this.topLabel = new Label();
		this.bottomLabel = new Label();
		
		this.topLabel.setFont(Font.font("Arial", 10));//300/maximumNumber));

		this.centerLabel.setFont(Font.font("Arial", 15));//300/maximumNumber));
		this.centerLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this.centerLabel.setAlignment(Pos.CENTER);
		this.centerLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY , Insets.EMPTY)));
		
		this.topLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this.bottomLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		this.topLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY , Insets.EMPTY)));
		this.bottomLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY , Insets.EMPTY)));

		this.setCenter(this.centerLabel);
		this.setTop(this.topLabel);
		this.setBottom(this.bottomLabel);
		
		this.setOnMouseMoved(e -> { 
			this.requestFocus();
		});
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseHandler(this.gridpane, maximumNumber));
	}
	
	/**
	 * Returns an array of the position of a Cell object within a GridPane.
	 * @return the index of a Cell.
	 */
	public int[] getIndexArray() {
		return this.indexArray;
	}
	
	/**
	 * Returns the centerLabel in a Cell.
	 * @return the centerLabel in a Cell.
	 */
	public Label getCenterLabel() {
		return this.centerLabel;
		
	}
	
	/**
	 * Returns the topLabel in a Cell.
	 * @return the topLabel in a Cell.
	 */
	public Label getTopLabel() {
		return this.topLabel;
		
	}
	
	/**
	 * Returns the bottomLabel in a Cell.
	 * @return the bottomLabel in a Cell.
	 */
	public Label getBottomLabel() {
		return this.bottomLabel;
		
	}
	
	/**
	 * Sets an ID number for a Cell.
	 * @param id The ID the cell should be given.
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the ID of a Cell.
	 * @return a Cell's ID number.
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Stores the ID of the Cell to the right of a Cell.
	 * @param rightCellId The ID of the Cell to the right.
	 */
	public void setRightCellID(int rightCellID) {
		this.rightCellID = rightCellID;
	}
	
	/**
	 * Stores the ID of the Cell to the left of a Cell.
	 * @param leftCellId The ID of the Cell to the left.
	 */
	public void setLeftCellID(int leftCellID) {
		this.leftCellID = leftCellID;
	}
	
	/**
	 * Stores the ID of the Cell above a Cell.
	 * @param topCellId The ID of the Cell to the right.
	 */
	public void setTopCellID(int topCellID) {
		this.topCellID = topCellID;
	}
	
	/**
	 * Stores the ID of the Cell below a Cell.
	 * @param bottomCellId The ID of the Cell below.
	 */
	public void setBottomCellID(int bottomCellID) {
		this.bottomCellID = bottomCellID;
	}
	
	/**
	 * Return the ID number of the Cell to the right.
	 * @return the ID number of the Cell to the right.
	 */
	public int getRightCellID() {
		return this.rightCellID;
	}
	
	/**
	 * Return the ID number of the Cell to the left.
	 * @return the ID number of the Cell to the left.
	 */
	public int getLeftCellID() {
		return this.leftCellID;
	}
	
	/**
	 * Return the ID number of the Cell above.
	 * @return the ID number of the Cell above.
	 */
	public int getTopCellID() {
		return this.topCellID;
	}
	
	/**
	 * Return the ID number of the Cell below.
	 * @return the ID number of the Cell below.
	 */
	public int getBottomCellID() {
		return this.bottomCellID;
	}
}

/**
 * The MouseHandler class handles mouse events.
 */
class MouseHandler implements EventHandler<MouseEvent> {
	GridPane gridpane;
	int maximumNumber;
	
	/**
	 * Creates an object of the MouseHandler class.
	 * @param gridpane The GridPane in which the Cell is in.
	 * @param number The maximum number a label can have.
	 */
	public MouseHandler(GridPane gridpane, int maximumNumber) {
		this.gridpane = gridpane;
		this.maximumNumber = maximumNumber;
	}
	
	/**
	 * Changes label value based on mouse clicks.
	 * @param event The MouseEvent.
	 */
	@Override
	public void handle(MouseEvent event) {	
		if ((((Cell) event.getSource()).getCenterLabel()).getText().equals("")) {
			(((Cell) event.getSource()).getCenterLabel()).setText("1");
		}
		else {
			int labelValue = Integer.parseInt((((Cell) event.getSource()).getCenterLabel()).getText());
			if (labelValue < this.maximumNumber) {
				(((Cell) event.getSource()).getCenterLabel()).setText(Integer.toString(labelValue+1));
			}
			else {
				(((Cell) event.getSource()).getCenterLabel()).setText("");
			}
		}
	}
}