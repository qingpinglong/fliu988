import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.JTable;
import java.util.*;
import java.io.*;
import java.util.List;
import java.awt.BorderLayout;
public class VisualisationInterface extends JFrame{
	public static ArrayList<int[]> patient_position;
	public static ArrayList<int[]> ambulance_position;
	private static VisualAmbulance visual_ambulance;
	private KeyArea key_area;
	public static void reset(){
		visual_ambulance.revalidate();
		visual_ambulance.repaint();
	}
	public VisualisationInterface(){
		setLayout(null);
		setSize(800, 680);
		Color backgroundcolor = new Color(72,209,204);
		getContentPane().setBackground(backgroundcolor);
		JLabel title = new JLabel("Ambulance Simulation");
		title.setFont(new Font("GENEVA", Font.BOLD, 40));
		title.setBounds(180, 20, 600, 60);
		visual_ambulance = new VisualAmbulance();
		key_area = new KeyArea();
		add(title);
		add(visual_ambulance);
		add(key_area);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
		
	}
	/*public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new visualisation_interface();
            }
        });
    }*/


	class KeyArea extends JPanel{
		public KeyArea(){
			setBackground(Color.white);
			setPreferredSize(new Dimension(200, 500));
			setBounds(550, 100, 200, 500);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(new Font("GENEVA", Font.PLAIN, 20));		
			g.drawString("Key", 10, 20);
			g.drawLine(10, 25, 50, 25);
			g.drawString("Ambulance", 40, 55);
			g.drawString("Patient", 40, 85);
			g.drawString("Station", 40, 115);
			g.drawString("Hospital", 40, 145);
			g.setColor(Color.red);   //ambulance
			g.fillOval(10, 40, 10, 10);
			g.setColor(Color.blue);  // patient
			g.fillOval(10, 70, 10, 10);
			g.setColor(Color.black);   //station
			g.fillOval(10, 100, 10, 10);
			g.setColor(Color.green);  //Hospital
			g.fillOval(10, 130, 10, 10);		
		}
	}
	static class VisualAmbulance extends JPanel{

		public VisualAmbulance(){
			setBackground(Color.white);
			setPreferredSize(new Dimension(500, 500));
			setBounds(20, 100, 500, 500);
			ambulance_position = csvread("ambulances.csv");		
			patient_position = csvread("patients.csv");
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.green);
			g.fillOval(250, 250, 10, 10);
			g.setColor(Color.black);
			g.fillOval(50, 0, 10, 10);
			g.fillOval(150, 400, 10, 10);
			g.fillOval(450, 100, 10, 10);
			g.setColor(Color.red);
			
			for(int i=0; i< ambulance_position.size(); i++){
				int x = (ambulance_position.get(i))[0];
				int y = (ambulance_position.get(i))[1];
				g.fillOval(x*5, y*5, 10, 10);
			}
			g.setColor(Color.blue);
			
			for(int i=0; i< patient_position.size(); i++){
				int x = (patient_position.get(i))[0];
				int y = (patient_position.get(i))[1];
				if(x<=100 && y<=100){
					g.fillOval(x*5, y*5, 10, 10);
				}
			}
		}
		public ArrayList<int[]> csvread(String filename){
			ArrayList<int[]> position = new ArrayList<int[]>();
			BufferedReader fn; 
			try{
			fn = new BufferedReader(new FileReader(filename));
			fn.readLine();
			String s;
			while((s=fn.readLine())!=null){
				s = s.replace("\"", "");
				String[] st1 = s.split(",");
				int[] pos = new int[2];
				pos[0] = Integer.parseInt(st1[1]);
				pos[1] = Integer.parseInt(st1[2]);		
				position.add(pos);
			}
			fn.close();
			}catch(IOException e){System.out.println("file not found");}
			return position;
		}
	}
}