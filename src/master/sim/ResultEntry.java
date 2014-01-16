package master.sim;
public class ResultEntry {

	private int _time;
	private int _crossingCounter;
	private int _numberOfLeaves;
	private int _totalCrossingCounter;
	private int _totalReconfigurationCounter;
	private double _averageOverload;
	private int _entities;
	private int _reconfCounter;
	private int _emptyServers;
	private int _maxOverload;

	public ResultEntry(int time) {
		_time = time;
	}

	public void setTotalCrossingCounter(int crossingCounter) {
		_totalCrossingCounter = crossingCounter;
	}
	public int getTotalCrossingCounter() {
		return _totalCrossingCounter;
	}
	public void setNumberOfLeaves(int numberOfLeaves) {
		_numberOfLeaves = numberOfLeaves;

	}

	@Override
	public String toString() {

		return _time + " ; " 
				+ _crossingCounter + " ; "
				+ _totalCrossingCounter + " ; "
				+ _numberOfLeaves + " ; " 
				+ _reconfCounter + " ; "
				+ _totalReconfigurationCounter + " ; " 
				+ _averageOverload + " ; "
				+ _maxOverload + " ; "
				+ _entities + " ; "
				+ _emptyServers;
	}

	public String toVerboseString() {
		return _time + " s: messages = " 
				+ _crossingCounter 
				+ "(total = " 
				+ _totalCrossingCounter 
				+ ") ; used servers = " 
				+ _numberOfLeaves 
				+ "; reconfs = " 
				+ _reconfCounter
				+ "; totalReconfs = " 
				+ _totalReconfigurationCounter
				+ "; averageOverload = " 
				+ _averageOverload
				+ "; maxOverload = "
				+ _maxOverload
				+ "; entities = " 
				+ _entities
				+ "; emptyServers = " 
				+ _emptyServers;
	}

	public void setCrossingCounter(int crossingCounter) {
		_crossingCounter = crossingCounter;
	}

	public void setTotalReconfigurationCounter(int reconfCounter) {
		_totalReconfigurationCounter = reconfCounter;
	}

	public void setAverageOverload(double averageOverload) {
		_averageOverload = averageOverload;
	}

	public void setNumberOfEntities(int entities) {
		_entities = entities;
	}

	public int getCrossingCounter() {
		return _crossingCounter;
	}

	public int getTotalReconfigurationCounter() {
		return _totalReconfigurationCounter;
	}

	public double getAverageOverload() {
		return _averageOverload;
	}

	public int getNumberOfEntities() {
		return _entities;
	}

	public int getNumberOfLeaves() {
		return _numberOfLeaves;
	}

	public void setReconfigurationCounter(int reconfCounter) {
		_reconfCounter = reconfCounter;
		
	}

	public int getReconfigurationCounter() {
		return _reconfCounter;
	}

	public void setEmptyServers(int emptyServers) {
		_emptyServers = emptyServers;
	}

	public int getEmptyServers() {
		return _emptyServers;
	}

	public int getMaxOverload() {
		return _maxOverload;
	}

	public void setMaxOverload(int i) {
		_maxOverload = i;
	}

}
