
public class Raster {
	

	// if it is not a column or an hydrant
	private boolean isUsable;
	private boolean isTrainStat;
	private int row;
	private int column;

	public Raster(int row, int column, boolean isTrainStat) {
		this.row = row;
		this.column = column;
		this.setIsTrainStat(isTrainStat);
	}

	@Override
	public String toString() {
		return "Raster [isTrainStat= " + isTrainStat +  ", isUsable=" + isUsable + ", row=" + row + ", column=" + column + "]";
	}

	/**
	 * @return the isTrainStat
	 */
	public boolean getIsTrainStat() {
		return isTrainStat;
	}

	/**
	 * @param isTrainStat the isTrainStat to set
	 */
	public void setIsTrainStat(boolean isTrainStat) {
		this.isTrainStat = isTrainStat;
	}
}
