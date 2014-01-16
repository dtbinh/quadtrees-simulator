package master.sim.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import master.sim.ResultEntry;
import master.sim.SimParams;
import master.sim.SimulationExecutor;
import master.sim.dspalgorithm.DSP;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SimulatorRunner {

	private static Display display;
	private static Shell shell;
	private static Canvas canvas;

	public static void main(String[] args) throws FileNotFoundException {

		Set<String> params = new HashSet<String>(Arrays.asList(args));
		
		SimParams simParams = new SimParams();
		boolean headless = false;
		if(params.contains("headless")){
			simParams.load(args);
			simParams.setCanvas(null);
			headless = true;
		}else{
			simParams = new SWTMainDialog().show();
		}
		if(simParams == null){
			return;
		}
		if(!headless){
			display = new Display();
			shell = new Shell(display);
			shell.setLayout(new FillLayout());
			canvas = new Canvas(shell, SWT.NO_BACKGROUND);
			simParams.setCanvas(canvas);
		}
		
		int seriesSize = Integer.MAX_VALUE;

		List<List<ResultEntry>> resultsAll = new ArrayList<List<ResultEntry>>();

		SimulationExecutor simulationExecutor = new SimulationExecutor(simParams);
		
		simulationExecutor.execute();
		if(!headless){
			shell.setText("Dynamic Space Partitioning");
			shell.setSize(simParams.getAreaDimension().width+50,simParams.getAreaDimension().height+70);
			shell.open();
		
			while(!shell.isDisposed()){
				if(!display.readAndDispatch()){
					display.sleep();
				}
			}		
			display.dispose();
			shell.dispose();
			canvas.dispose();
			if(simulationExecutor.isRunning()){
				simulationExecutor.interrupt();
			}
		}else{
			System.out.println("Simulation is running...");
			while(simulationExecutor.isRunning()){
				try {
					System.out.print(".");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println();
		}
				
		 List<ResultEntry> results = simulationExecutor.getResults();
		 if(results.size() < seriesSize){
			 seriesSize = results.size();
		 }
		 resultsAll.add(results);


		File file = new File(simParams.getResultsFilepath());
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.println(simParams.getModel());
			List<ResultEntry> averageResults = new ArrayList<ResultEntry>();
			int resultsAllSize = resultsAll.size();
			for(int j = 0; j < seriesSize; j++){
				ResultEntry aveEntry = new ResultEntry(j);
				int crossingCounter = 0;
				int totalCrossingCounter = 0;
				int numberOfLeaves = 0;
				int reconfCounter = 0;
				int totalReconfCounter = 0;
				double averageOverload = 0;
				int maxOverload = 0;
				int entities = 0;
				int emptyServers = 0;
				for(int i = 0; i < resultsAllSize; i++){

					List<ResultEntry> series = resultsAll.get(i);
					ResultEntry resultEntry = series.get(j);
					crossingCounter += resultEntry.getCrossingCounter();
					totalCrossingCounter += resultEntry.getTotalCrossingCounter();
					numberOfLeaves += resultEntry.getNumberOfLeaves();
					reconfCounter += resultEntry.getReconfigurationCounter();
					totalReconfCounter += resultEntry.getTotalReconfigurationCounter();
					averageOverload += resultEntry.getAverageOverload();
					maxOverload += resultEntry.getMaxOverload();
					entities += resultEntry.getNumberOfEntities();
					emptyServers += resultEntry.getEmptyServers();
				}
				aveEntry.setCrossingCounter(crossingCounter/resultsAllSize);
				aveEntry.setTotalCrossingCounter(totalCrossingCounter/resultsAllSize);
				aveEntry.setNumberOfLeaves(numberOfLeaves/resultsAllSize);
				aveEntry.setReconfigurationCounter(reconfCounter/resultsAllSize);
				aveEntry.setTotalReconfigurationCounter(totalReconfCounter/resultsAllSize);
				aveEntry.setAverageOverload(averageOverload/resultsAllSize);
				aveEntry.setMaxOverload(maxOverload/resultsAllSize);
				aveEntry.setNumberOfEntities(entities/resultsAllSize);
				aveEntry.setEmptyServers(emptyServers/resultsAllSize);
				
				averageResults.add(aveEntry);
			}
			
			for (ResultEntry resultEntry : averageResults) {
				if(simParams.getVerboseResults()){
					writer.println(resultEntry.toVerboseString());
				}else{
					writer.println(resultEntry);
				}
			}
			writer.flush();
			writer.close();
			System.err.println("Saved " + simParams.getModel());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
