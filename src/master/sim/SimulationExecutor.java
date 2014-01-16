package master.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import master.sim.dspalgorithm.DSP;
import master.sim.dspalgorithm.DSPRunnable;
import master.sim.dspalgorithm.ERQuadTree;
import master.sim.dspalgorithm.LRQuadTree;
import master.sim.dspalgorithm.MAQuadTree;
import master.sim.dspalgorithm.MainMigrationListener;
import master.sim.dspalgorithm.MigrationListener;
import master.sim.dspalgorithm.QuadTree;
import master.sim.dspalgorithm.Quadrant;
import master.sim.dspalgorithm.RatioQuadTree;
import master.sim.dspalgorithm.StaticPartition;
import master.sim.gui.GridPainter;
import master.sim.usermodel.Avatar;
import master.sim.usermodel.Predator;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;


public class SimulationExecutor implements Runnable{

	private List<ResultEntry> _resultEntries;
	
	private int _duration;
	private int _interval;
	private DSP _partitionModel;
	private int _numberOfPreys;
	private int _numberOfPredators;
	private int _numberOfMachines;
	private Rectangle _virtualAreaDimensions;

	private volatile boolean _isRunning = false;
	private int _threshold;
	private Canvas _canvas;
	private Thread _simulationThread;

	private boolean _forcedEnd = false;

	private Quadrant _model;

	private int _staticRows = 4;

	private int _staticColumns = 4;

	private int _softThreshold = 1;

	private int _timeWindow;

	private int _stopAvatars;
	
	public SimulationExecutor(SimParams simParams) {
		_canvas = simParams.getCanvas();
		setDuration(simParams.getSimulationTime());
		setInterval(simParams.getSamplingPeriod());
		setStaticRows(simParams.getStaticRows());
		setStaticColumns(simParams.getStaticColumns());
		setNumberOfMachines(simParams.getNumberOfServers());
		setVirtualAreaDimension(simParams.getAreaDimension().width,simParams.getAreaDimension().height);
		setNumberOfPreys(simParams.getNumberOfAvatars());
		setNumberOfPredators(simParams.getNumberOfPredators());
		setPartitionModel(simParams.getModel());
		setSoftThreshold(simParams.getSoftThreshold());
		setThreshold(simParams.getOverloadThreshold());
		setDyingOutFactor(simParams.getDyingOutNumber());
		setTimeWindow(simParams.getTimeWindow());
		
	}


	public void execute() {
		_isRunning = true;
		_resultEntries = new ArrayList<ResultEntry>();
		
		AvatarRegistry avatarRegistry = AvatarRegistry.getInstance();
		avatarRegistry.clearAvatars();
		avatarRegistry.setMachines(_numberOfMachines);
		
		_model = getModel();
		_model.init();
		MigrationListener migrationListener = new MainMigrationListener(_model);
		avatarRegistry.clearMigrationListeners();
		avatarRegistry.registerMigrationListener(migrationListener);

		for (int i = 0; i < _numberOfPreys; i++) {
			avatarRegistry.add(new Avatar(new Random().nextInt(_virtualAreaDimensions.width), new Random().nextInt(_virtualAreaDimensions.height), _model));
//			avatarRegistry.add(new Avatar(25, 25, _model));
		}
		for (int i = 0; i < _numberOfPredators; i++) {
			avatarRegistry.add(new Predator(new Random().nextInt(_virtualAreaDimensions.width), new Random().nextInt(_virtualAreaDimensions.height), _model));
//			avatarRegistry.add(new Predator(475, 475, _model));
		}
		
		for (Avatar avatar : avatarRegistry.getAvatars()) {
			_model.add(avatar);
		}

		if(_canvas != null){
			GridPainter painter = new GridPainter(_model, _canvas);
			_canvas.addPaintListener(painter);
		}
		_simulationThread = new Thread(this, "SimRunner");
		_simulationThread.start();
	}
	

	private Quadrant getModel() {
		switch (_partitionModel) {
		case QUADTREE:
			return new QuadTree(null, _virtualAreaDimensions, _threshold);
		case STATIC:
			return new StaticPartition(_virtualAreaDimensions, _staticRows, _staticColumns, _threshold);
		case MOVING_AVERAGE:
			return new MAQuadTree(null, _virtualAreaDimensions, _timeWindow, _softThreshold, _threshold);
		case LINEAR_REGRESSION:
			return new LRQuadTree(null, _virtualAreaDimensions, _timeWindow, _softThreshold, _threshold);
		case EXP_REGRESSION:
			return new ERQuadTree(null, _virtualAreaDimensions, _timeWindow, _softThreshold, _threshold);
		case RATIO:
			return new RatioQuadTree(null, _virtualAreaDimensions, _timeWindow, _softThreshold, _threshold);
		default:
			break;
		}
		return null;
	}


	public void setDuration(int i) {
		_duration = i;
	}

	public void setInterval(int i) {
		_interval = i;
	}

	public void setPartitionModel(DSP model) {
		_partitionModel = model;
	}

	public void setNumberOfPreys(int i) {
		_numberOfPreys = i;
	}

	public void setNumberOfPredators(int i) {
		_numberOfPredators = i;
	}

	public List<ResultEntry> getResults() {
		if(!_isRunning){
			return _resultEntries;
		}
		return new ArrayList<ResultEntry>();
	}

	public void setNumberOfMachines(int i) {
		_numberOfMachines = i;
	}

	public void setVirtualAreaDimension(int width, int height) {
		_virtualAreaDimensions = new Rectangle(0,0,width,height);
	}


	public void setThreshold(int i) {
		_threshold = i;
	}


	@Override
	public void run() {
		_isRunning = true;
		
		int step = 0;
		int numberOfSteps = _duration/_interval;
		int lastCrossingCounter = 0;
		int lastReconfCounter = 0;
		while(step < numberOfSteps && !_forcedEnd){
			step++;
			int crossingCounter = _model.getCrossingCounter();
			int tempLeaves = _model.getNumberOfLeaves();
			int numberOfLeaves = tempLeaves > 0 ? tempLeaves : 1;
			int reconfCounter = _model.getReconfigurationCounter();
			int overload = _model.getTotalOverload();
			int maxOverload = _model.getMaxOverload();
			int entities = _model.getNumberOfEntities();
			int emptyServers = _model.getNumberOfEmptyLeaves();
			
			ResultEntry entry = new ResultEntry(step*_interval);
			entry.setTotalCrossingCounter(crossingCounter);
			entry.setTotalReconfigurationCounter(reconfCounter);
			entry.setAverageOverload(overload/(double)numberOfLeaves);
			entry.setMaxOverload(maxOverload);
			entry.setCrossingCounter((crossingCounter - lastCrossingCounter)/_interval);
			entry.setReconfigurationCounter(reconfCounter - lastReconfCounter);
			lastCrossingCounter = crossingCounter;
			lastReconfCounter = reconfCounter;
			
			entry.setEmptyServers(emptyServers);
			entry.setNumberOfLeaves(numberOfLeaves);
			entry.setNumberOfEntities(entities);
			_resultEntries.add(entry);

			List<Avatar> avatars = AvatarRegistry.getInstance().getAvatars();
			if(avatars.size() > 0){
				for(int i = 0; i < _stopAvatars; i++){
					Avatar avatar = avatars.get(new Random().nextInt(avatars.size()));
//					_model.remove(avatar);
					avatar.stop();
					AvatarRegistry.getInstance().remove(avatar);
					avatars.remove(avatar);
				}
			}
			
			try {
				Thread.sleep(_interval*1000);
			} catch (InterruptedException e) {
				// end of simulation on user's demand
			}
		}
		
		for (Avatar avatar : AvatarRegistry.getInstance().getAvatars()) {
			avatar.stop();
			_model.remove(avatar);
		}

		if(_model instanceof DSPRunnable){
			((DSPRunnable)_model).stopRunning();
		}
		
		for (Avatar avatar : AvatarRegistry.getInstance().getAvatars()) {
			try {
				avatar.getAvatarThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		_isRunning = false;
	}


	public void interrupt() {
		_forcedEnd  = true;
		_simulationThread.interrupt();
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void setStaticColumns(int i) {
		_staticColumns = i;
	}

	public void setStaticRows(int i) {
		_staticRows = i;
	}


	public void setSoftThreshold(int i) {
		_softThreshold = i;
	}

	public void setTimeWindow(int i) {
		_timeWindow = i;
	}


	public void setDyingOutFactor(int avatarsPerSecond) {
		_stopAvatars = avatarsPerSecond;
	}
	
}
