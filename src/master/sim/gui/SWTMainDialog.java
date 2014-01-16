package master.sim.gui;

import java.awt.Dimension;
import java.awt.TextField;
import java.util.Map;

import master.sim.SimParams;
import master.sim.dspalgorithm.DSP;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.runner.notification.StoppedByUserException;

public class SWTMainDialog {

	private Display _display;
	private Shell _shell;
	private Text _simTimeText;
	private Text _samplingPeriodText;
	private Text _areaWidthText;
	private Text _areaHeightText;
	private Text _numberOfServersText;
	private Text _overloadThresholdText;
	private Text _numberOfAvatarsText;
	private Text _numberOfPredatorsText;
	private Text _dyingOutNumberText;
	private SimParams _simParams = null;
	private Combo _modelCombo;
	private Text _staticRowsText;
	private Text _staticColumnsText;
	private Text _softThresholdText;
	private Text _timeWindowText;
	private Text _filePathText;
	private Button _verboseCheckbox;

	public SimParams show() {
		_display = new Display();
		_shell = new Shell(_display);
		GridLayout gridLayout = new GridLayout(2, false);
		_shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(50, 15);

		Label label0 = new Label(_shell, SWT.NONE);
		label0.setText("Simulation Time");
		_simTimeText = new Text(_shell, SWT.RIGHT);
		_simTimeText.setText("105");
		_simTimeText.setLayoutData(gridData);
		Label label1 = new Label(_shell, SWT.NONE);
		label1.setText("Sampling Period");
		_samplingPeriodText = new Text(_shell, SWT.RIGHT);
		_samplingPeriodText.setText("5");
		_samplingPeriodText.setLayoutData(gridData);
		Label label2 = new Label(_shell, SWT.NONE);
		label2.setText("Area width");
		_areaWidthText = new Text(_shell, SWT.RIGHT);
		_areaWidthText.setText("128");
		_areaWidthText.setLayoutData(gridData);
		Label label3 = new Label(_shell, SWT.NONE);
		label3.setText("Area height");
		_areaHeightText = new Text(_shell, SWT.RIGHT);
		_areaHeightText.setText("128");
		_areaHeightText.setLayoutData(gridData);
		Label label4 = new Label(_shell, SWT.NONE);
		label4.setText("Number of servers");
		_numberOfServersText = new Text(_shell, SWT.RIGHT);
		_numberOfServersText.setText("16");
		_numberOfServersText.setLayoutData(gridData);
		Label label5 = new Label(_shell, SWT.NONE);
		label5.setText("Overload threshold");
		_overloadThresholdText = new Text(_shell, SWT.RIGHT);
		_overloadThresholdText.setText("10");
		_overloadThresholdText.setLayoutData(gridData);
		Label label6 = new Label(_shell, SWT.NONE);
		label6.setText("Number of avatars");
		_numberOfAvatarsText = new Text(_shell, SWT.RIGHT);
		_numberOfAvatarsText.setText("40");
		_numberOfAvatarsText.setLayoutData(gridData);
		Label label7 = new Label(_shell, SWT.NONE);
		label7.setText("Number of predators");
		_numberOfPredatorsText = new Text(_shell, SWT.RIGHT);
		_numberOfPredatorsText.setText("5");
		_numberOfPredatorsText.setLayoutData(gridData);
		Label label8 = new Label(_shell, SWT.NONE);
		label8.setText("Remove number of avatars");
		_dyingOutNumberText = new Text(_shell, SWT.RIGHT);
		_dyingOutNumberText.setText("2");
		_dyingOutNumberText.setLayoutData(gridData);
		Label label10 = new Label(_shell, SWT.NONE);
		label10.setText("Model");
		_modelCombo = new Combo(_shell, SWT.NONE);
		String[] modelStrings = new String[DSP.values().length];
		int i = 0;
		for (DSP dspString : DSP.values()) {
			modelStrings[i] = dspString.toString();
			i++;
		}
		_modelCombo.setItems(modelStrings);
		_modelCombo.select(0);
		Label label11 = new Label(_shell, SWT.NONE);
		label11.setText("Static: rows");
		_staticRowsText = new Text(_shell, SWT.RIGHT);
		_staticRowsText.setText("4");
		_staticRowsText.setLayoutData(gridData);
		Label label12 = new Label(_shell, SWT.NONE);
		label12.setText("Static: columns");
		_staticColumnsText = new Text(_shell, SWT.RIGHT);
		_staticColumnsText.setText("4");
		_staticColumnsText.setLayoutData(gridData);
		Label label13 = new Label(_shell, SWT.NONE);
		label13.setText("DSP: soft threshold");
		_softThresholdText = new Text(_shell, SWT.RIGHT);
		_softThresholdText.setText("7");
		_softThresholdText.setLayoutData(gridData);
		Label label14 = new Label(_shell, SWT.NONE);
		label14.setText("DSP: time window");
		_timeWindowText = new Text(_shell, SWT.RIGHT);
		_timeWindowText.setText("5");
		_timeWindowText.setLayoutData(gridData);
		Label label15 = new Label(_shell, SWT.NONE);
		label15.setText("Reults filepath");
		_filePathText = new Text(_shell, SWT.LEFT);
		_filePathText.setText("D:\\results.txt");
		GridData filepathGridData = new GridData(200, 15);
		_filePathText.setLayoutData(filepathGridData);
		_filePathText.addMouseListener(new FileMouseListener());
		Label label16 = new Label(_shell, SWT.NONE);
		label16.setText("Verbose output");
		_verboseCheckbox = new Button(_shell, SWT.CHECK);
		_verboseCheckbox.setSelection(false);
		
		Button okButton = new Button(_shell, SWT.PUSH);
		okButton.setText("Start");
		okButton.addMouseListener(new StartSimMouseListener());
		Button cancelButton = new Button(_shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addMouseListener(new CancelSimMouseListener());
		
		_shell.setText("Dynamic Space Partitioning");
		_shell.setSize(350, 400);
		_shell.layout();
		_shell.open();

		while (!_shell.isDisposed()) {
			if (!_display.readAndDispatch()) {
				_display.sleep();
			}
		}

		_display.dispose();
		_shell.dispose();

		return _simParams;
	}

	private class FileMouseListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			FileDialog fileDialog = new FileDialog(_shell);
			String filepath = fileDialog.open();
			if(filepath != null){
				_filePathText.setText(filepath);
			}
		}
		
	}
	
	private class StartSimMouseListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseUp(MouseEvent e) {
			SimParams params = new SimParams();
			params.setSimulationTime(Integer.parseInt(_simTimeText.getText()));
			params.setSamplingPeriod(Integer.parseInt(_samplingPeriodText.getText()));
			params.setAreaDimension(new Dimension(Integer.parseInt(_areaWidthText.getText()), Integer.parseInt(_areaHeightText.getText())));
			params.setNumberOfServers(Integer.parseInt(_numberOfServersText.getText()));
			params.setOverloadThreshold(Integer.parseInt(_overloadThresholdText.getText()));
			params.setNumberOfAvatars(Integer.parseInt(_numberOfAvatarsText.getText()));
			params.setNumberOfPredators(Integer.parseInt(_numberOfPredatorsText.getText()));
			params.setDyingOutNumber(Integer.parseInt(_dyingOutNumberText.getText()));
			params.setModel(DSP.valueOf(_modelCombo.getItem(_modelCombo.getSelectionIndex())));
			params.setStaticRows(Integer.parseInt(_staticRowsText.getText()));
			params.setStaticColumns(Integer.parseInt(_staticColumnsText.getText()));
			params.setSoftThreshold(Integer.parseInt(_softThresholdText.getText()));
			params.setTimeWindow(Integer.parseInt(_timeWindowText.getText()));
			params.setResultsFilepath(_filePathText.getText());
			params.setVerboseResults(_verboseCheckbox.getSelection());
			_simParams = params;
			_shell.close();
		}
	}
	
	private class CancelSimMouseListener implements MouseListener{

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseUp(MouseEvent e) {
			_simParams = null;
			_shell.close();
		}
		
	}
	
}
