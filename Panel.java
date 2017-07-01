import javax.swing.JPanel;

public class Panel extends JPanel {
	private int row, col;
	
	public Panel(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public int getCol() {
		return col;
	}
	
	public int getRow() {
		return row;
	}
}
