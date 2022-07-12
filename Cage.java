import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * The Cage class creates cages for a MathDoku.
 */
public class Cage {
	ArrayList<Cell> cellList;
	
	/**
	 * Creates an instance of Cage.
	 * @param cellList This contains Cells for which the cages can be made.
	 */
	public Cage(ArrayList<Cell> cellList) { 
		this.cellList = cellList;
	}
	
	/**
	 * Makes the cages around cells.
	 * @param groupOfCells The group of Cells to make the Cage around.
	 */
	public void makeCage(String[] groupOfCells) {
		String operation = null;
		
		for(int i=0 ; i < groupOfCells.length; i++) {
			
			if(i == 0 ) {
				operation = groupOfCells[i];
			}
			else {
				for(Cell cell : this.cellList) {
					if(i == 1) {
						if(Integer.toString(cell.getID()).equals(groupOfCells[i])) {
							cell.getTopLabel().setText(operation);
						}
					}
					if(Integer.toString(cell.getID()).equals(groupOfCells[i])) {
						int topBorder = 1;
						int bottomBorder = 1;
						int rightBorder = 1;
						int leftBorder = 1;
						
						if(!(Arrays.asList(groupOfCells).contains(Integer.toString(cell.getRightCellID())))) {
							rightBorder = 3;
						}
						if(!(Arrays.asList(groupOfCells).contains(Integer.toString(cell.getLeftCellID())))) {
							leftBorder = 3;
						}
						if(!(Arrays.asList(groupOfCells).contains(Integer.toString(cell.getBottomCellID())))) {
							bottomBorder = 3;
						}
						if(!(Arrays.asList(groupOfCells).contains(Integer.toString(cell.getTopCellID())))) {
							topBorder = 3;
						}
						cell.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(topBorder, rightBorder, bottomBorder, leftBorder))));
					}
				}
			}
		}
	}
	
}
