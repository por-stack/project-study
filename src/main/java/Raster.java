

public class Raster {
	
	// if it is not a column or an hydrant
	private boolean isUsable;
	private int row; 
	private int column; 
	private Zone belongTo; 
	
	public Raster(int row, int column) {
		this.row = row; 
		this.column = column; 
	}

	@Override
	public String toString() {
		return "Raster [isUsable=" + isUsable + ", row=" + row + ", column=" + column + "]";
	}
}
