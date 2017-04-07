import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.JTable;
import java.util.*;
import java.io.*;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.TableModel.*;
import javax.swing.table.*;
import javax.swing.table.AbstractTableModel;
import java.util.concurrent.ExecutionException;

public class AmbulanceDisplay extends JFrame{
	private JButton StartButton = new JButton("Start");
	private JButton StopButton = new JButton("Stop");
	protected static Object[][] data;
	private static String[] columnName = {"id", "location", "status", "patient"};	
	protected static MyTableModel model;
	protected static JPanel ambulancePanel;
	protected static JTable table;
	public static boolean sign = true;
	public static String time;
	protected static JTextField timeArea;
	
	public static void updateTable(MyTableModel model1){
		model = model1;
		table.setModel(model);
		timeArea.setText(time);
		VisualisationInterface.reset();
		if(Integer.parseInt(time) == 0){
			JOptionPane.showMessageDialog(new JFrame(),"time is out.","Information",JOptionPane.INFORMATION_MESSAGE);						
		}
	}
	public AmbulanceDisplay(){
		ambulancePanel = new JPanel();
		ambulancePanel.setPreferredSize(new Dimension(840, 680));
		ambulancePanel.setLayout(null);
		JLabel title = new JLabel("Ambulance Simulation");
		title.setFont(new Font("GENEVA", Font.BOLD, 40));
		title.setBounds(180, 20, 600, 60);
		
		JLabel timelabel = new JLabel("Duration(seconds): ");
		timeArea = new JTextField("60");		
		timelabel.setBounds(100, 430, 200, 50);
		timelabel.setFont(new Font("GENEVA", Font.BOLD, 20));
		timeArea.setBounds(320, 430, 380, 50);
		
		ArrayList<String[]> s1 = csvread("ambulances.csv");
		ArrayList<String[]> s2 = csvread("patients.csv");
		data = new Object[s1.size()][columnName.length];
		for(int j = 0; j < s1.size(); j++){
			String[] st = new String[(s1.get(j)).length-1];
			data[j][0] = (s1.get(j))[0];
			data[j][1] = "( " + (s1.get(j))[1] + " , " + (s1.get(j))[2] + " )";
			for(int i=2; i<(s1.get(j)).length-1; i++){data[j][i] = (s1.get(j))[i+1];}	
		}
		

		model = new MyTableModel(data);
		table = new JTable(model);
		
		table.setPreferredScrollableViewportSize(new Dimension(600, 300));
		table.setFillsViewportHeight(true);		
		
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(100, 100, 600, 300);
		//Add the scroll pane to this panel.
		ambulancePanel.add(scrollPane);
		ambulancePanel.add(title);
		
		StopButton.setEnabled(false);
		StartButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sign = true;
				try{
					time = timeArea.getText();
					if(time == ""){
						JOptionPane.showMessageDialog(new JFrame(),"Duration is required.","Input error",JOptionPane.ERROR_MESSAGE);						
					}
					else{
						int the_time = Integer.parseInt(time);
						if(the_time <=0){
							JOptionPane.showMessageDialog(new JFrame(),"Duration should be an positive integer.","Input error",JOptionPane.ERROR_MESSAGE);
						}
						else{				
							StartButton.setEnabled(false);
							StopButton.setEnabled(true);
							timeArea.setEnabled(false);
							AnswerWorker worker = new AnswerWorker(model, timeArea);
							worker.execute();
						}
					}
				}catch(NumberFormatException e1){
					JOptionPane.showMessageDialog(new JFrame(),"Duration should be an positive integer.","Input error",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		StopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				StartButton.setEnabled(true);
				StopButton.setEnabled(false);	
				timeArea.setEnabled(true);
				sign = false;
				
			}
		});
		Color backgroundcolor = new Color(72,209,204);
		ambulancePanel.setBackground(backgroundcolor);
		StartButton.setBounds(20, 500, 300, 100);
	
	
		Color buttoncolor = new Color(100,149,237);
		StartButton.setBackground(buttoncolor);
		StartButton.setForeground(Color.white);
		StartButton.setFont(new Font("GENEVA", Font.PLAIN, 30));
	
		StopButton.setBounds(520, 500, 300, 100);
		StopButton.setBackground(buttoncolor);
		StopButton.setForeground(Color.white);
		StopButton.setFont(new Font("GENEVA", Font.PLAIN, 30));

		ambulancePanel.add(StartButton);
		ambulancePanel.add(StopButton);
		ambulancePanel.add(timelabel);
		ambulancePanel.add(timeArea);
		
		setContentPane(ambulancePanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
	}
	
	
	public static ArrayList<String[]> csvread(String filename){
		ArrayList<String[]> sl = new ArrayList<String[]>();
		BufferedReader fn; 
		try{
		fn = new BufferedReader(new FileReader(filename));
		String rd = fn.readLine();
		String s;
		while((s=fn.readLine())!=null){
			s = s.replace("\"", "");
			String[] st1 = s.split(",");		
			sl.add(st1);
		}
		fn.close();
		}catch(IOException e){System.out.println("file not found");}
		return sl;
	}

	public static int getNEWID(){return data.length+1;}
	
	public static Object[][] getDATA(){return data;}
	
	/*public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AmbulanceDisplay();
            }
        });
    }*/
}
class MyTableModel extends AbstractTableModel {
	private boolean DEBUG = false;
		
    private String[] columnNames = {"ID","Location","Status","Patient"};

    private Object[][] data;
	
	public MyTableModel(Object[][] data){
		this.data= data;				
	}

	public void setData(Object[][] data){
		this.data = data;
	}
	
    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col < 2) {
        return false;
      } else {
        return true;
      }
    }

    public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
        System.out.println("Setting value at " + row + "," + col
            + " to " + value + " (an instance of "
            + value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
        System.out.println("New value of data:");
        printDebugData();
      }
    }

    private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          System.out.print("  " + data[i][j]);
        }
        System.out.println();
      }
      System.out.println("--------------------------");
    }
  }

class AnswerWorker extends SwingWorker<Object[][], MyTableModel> {
	MyTableModel model;
	JTextField timeArea;
	public AnswerWorker(MyTableModel model, JTextField timeArea){
		this.model = model;
		this.timeArea = timeArea;
	}
	protected Object[][] doInBackground() throws Exception{
		Object[][] data = AmbulanceDisplay.getDATA();
		ArrayList<String[]> s1 = AmbulanceDisplay.csvread("ambulances.csv");
		ArrayList<String[]> s2 = AmbulanceDisplay.csvread("patients.csv");
		SimulationProcess.setDATA(s1, s2);
		int available_number = (int)Math.ceil((s1.size()-1)/3.0);
		int number1 = 0, number2 = 0, number3 = 0;
		for(int i=0; i<s1.size(); i++){
			int x1 = Integer.parseInt((s1.get(i))[1]);
			int y1 = Integer.parseInt((s1.get(i))[2]);
			if(x1 == 10 && y1 == 0){number1 += 1;}
			else if(x1 == 30 && y1 == 80){number2 += 1;}
			else if(x1 == 90 && y1 == 20){number3 += 1;}
		}
		SimulationProcess.setNumber(number1, number2, number3, available_number);
		for(int i=0; i<s1.size(); i++){
			(new SimulationProcess(i)).start();
		}		
		while(AmbulanceDisplay.sign == true && Integer.parseInt(AmbulanceDisplay.time)!=0){
		ArrayList<String[]> read = SimulationProcess.getDATA();  //first method
		ArrayList<String[]> read_patient = SimulationProcess.getpatientDATA();
		for(int j = 0; j < read.size(); j++){
			String[] st = new String[(read.get(j)).length-1];
			data[j][0] = (read.get(j))[0];
			data[j][1] = "( " + (read.get(j))[1] + " , " + (read.get(j))[2] + " )";
			for(int i=2; i<(read.get(j)).length-1; i++){data[j][i] = (read.get(j))[i+1];}
			int[] ap = new int[2];
			ap[0] = Integer.parseInt((read.get(j))[1]);
			ap[1] = Integer.parseInt((read.get(j))[2]);
			VisualisationInterface.ambulance_position.set(j, ap);
		}
		for(int k=0; k<read_patient.size(); k++){
			int[] ap = new int[2];
			if((read_patient.get(k))[3].equals("Transporting")||(read_patient.get(k))[3].equals("Completed")){
				ap[0] = 200;
				ap[1] = 200;
			}
			else{
				ap[0] = Integer.parseInt((read_patient.get(k))[1]);
				ap[1] = Integer.parseInt((read_patient.get(k))[2]);
			}
			VisualisationInterface.patient_position.set(k, ap);			
		}
		model = new MyTableModel(data);
		int time1 = Integer.parseInt(AmbulanceDisplay.time);
		time1 -= 1;	
		AmbulanceDisplay.time = ""+time1;
		publish(model);
		Thread.sleep(1000);
		}
		return data;
	}
	protected void process(List<MyTableModel> chunks) {
		String[] columnName = {"id", "location", "status", "patient"};
		for(MyTableModel model:chunks){
			AmbulanceDisplay.updateTable(model);	
		}
	}	
}
