package master.sim;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import master.sim.dspalgorithm.DSP;

import org.eclipse.swt.widgets.Canvas;


public class SimParams {

	private Canvas _canvas;
	private int _simulationTime;
	private int _samplingPeriod;
	private Dimension _areaDimension = null;
	private int _numberOfServers;
	private int _overloadThreshold;
	private int _numberOfAvatars;
	private int _numberOfPredators;
	private int _dyingOutNumber;
	private DSP _model;
	private int _staticRows;
	private int _staticColumns;
	private int _softThreshold;
	private int _timeWindow;
	private String _resultsFilepath;
	private boolean _verboseResults;

	public Canvas getCanvas() {
		return _canvas;
	}

	public int getSimulationTime() {
		return _simulationTime;
	}

	public int getSamplingPeriod() {
		return _samplingPeriod;
	}

	public Dimension getAreaDimension() {
		return _areaDimension;
	}

	public int getNumberOfServers() {
		return _numberOfServers;
	}

	public int getOverloadThreshold() {
		return _overloadThreshold;
	}

	public int getNumberOfAvatars() {
		return _numberOfAvatars;
	}

	public int getNumberOfPredators() {
		return _numberOfPredators;
	}

	public int getDyingOutNumber() {
		return _dyingOutNumber;
	}

	public void load(String[] args) throws FileNotFoundException {
		List<String> argsList = Arrays.asList(args);
		int indexOf = argsList.indexOf("-c");
		String confFilepath = argsList.get(indexOf+1);
		File file = new File(confFilepath);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		try {
			
			String readLine = null;
			while( (readLine = bufferedReader.readLine()) != null){
				String[] split = readLine.split("=");
				setParam(split[0], split[1]);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setParam(String name, String value) {
		if("simTime".equals(name)){
			setSimulationTime(Integer.parseInt(value));
		}else if("samplingPeriod".equals(name)){
			setSamplingPeriod(Integer.parseInt(value));
		}else if("areaWidth".equals(name)){
			if(getAreaDimension() == null){
				setAreaDimension(new Dimension(Integer.parseInt(value), 0));
			}else{
				getAreaDimension().width = Integer.parseInt(value);
			}
		}else if("areaHeight".equals(name)){
			if(getAreaDimension() == null){
				setAreaDimension(new Dimension(0,Integer.parseInt(value)));
			}else{
				getAreaDimension().height = Integer.parseInt(value);
			}
		}else if("numberOfServers".equals(name)){
			setNumberOfServers(Integer.parseInt(value));
		}else if("overloadThreshold".equals(name)){
			setOverloadThreshold(Integer.parseInt(value));
		}else if("numberOfAvatars".equals(name)){
			setNumberOfAvatars(Integer.parseInt(value));
		}else if("numberOfPredators".equals(name)){
			setNumberOfPredators(Integer.parseInt(value));
		}else if("dyingOutNumber".equals(name)){
			setDyingOutNumber(Integer.parseInt(value));
		}else if("model".equals(name)){
			setModel(DSP.valueOf(value));
		}else if("staticRows".equals(name)){
			setStaticRows(Integer.parseInt(value));
		}else if("staticColumns".equals(name)){
			setStaticColumns(Integer.parseInt(value));
		}else if("softThreshold".equals(name)){
			setSoftThreshold(Integer.parseInt(value));
		}else if("timeWindow".equals(name)){
			setTimeWindow(Integer.parseInt(value));
		}else if("filePath".equals(name)){
			setResultsFilepath(value);
		}else if("verbose".equals(name)){
			setVerboseResults(Boolean.parseBoolean(value));
		}
	}

	public void setCanvas(Canvas canvas) {
		_canvas = canvas;
	}

	public void setSimulationTime(int time) {
		_simulationTime = time;
	}

	public void setSamplingPeriod(int samplingPeriod) {
		_samplingPeriod = samplingPeriod;
	}

	public void setAreaDimension(Dimension dimension) {
		_areaDimension = dimension;
	}

	public void setNumberOfServers(int servers) {
		_numberOfServers = servers;
	}

	public void setOverloadThreshold(int overloadThreshold) {
		_overloadThreshold = overloadThreshold;
	}

	public void setNumberOfAvatars(int numberOfAvatars) {
		_numberOfAvatars = numberOfAvatars;
	}

	public void setNumberOfPredators(int numberOfPredators) {
		_numberOfPredators = numberOfPredators;
	}

	public void setDyingOutNumber(int dyingOutNumber) {
		_dyingOutNumber = dyingOutNumber;
	}

	public void setModel(DSP model) {
		_model = model;
	}

	public DSP getModel() {
		return _model;
	}

	public void setStaticRows(int rows) {
		_staticRows = rows;
	}

	public int getStaticRows(){
		return _staticRows;
	}
	
	public void setStaticColumns(int columns) {
		_staticColumns = columns;
	}
	
	public int getStaticColumns(){
		return _staticColumns;
	}

	public void setSoftThreshold(int softThreshold) {
		_softThreshold = softThreshold;
	}

	public int getSoftThreshold(){
		return _softThreshold;
	}
	
	public void setTimeWindow(int timeWindow) {
		_timeWindow = timeWindow;
	}

	public int getTimeWindow(){
		return _timeWindow;
	}

	public void setResultsFilepath(String filepath) {
		_resultsFilepath = filepath;
	}
	
	public String getResultsFilepath(){
		return _resultsFilepath;
	}

	public void setVerboseResults(boolean selection) {
		_verboseResults = selection;
	}
	
	public boolean getVerboseResults(){
		return _verboseResults;
	}
}
