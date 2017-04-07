import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
public class SimulationProcess extends Thread{
	
	static private ArrayList<String[]> ambulance_data = new ArrayList<String[]>();
	static private ArrayList<String[]> patient_data = new ArrayList<String[]>();
	static private int available_number=0, number1=0, number2=0, number3=0;
	private int choose;
	private int flag = 2;
	private boolean stop = false;
	private int before_returning = 2, before_transport = 4;
	private static int countdown = 0;
	
	public SimulationProcess(int num){		
		choose = num;
	}
	
	public static int getCountdown(){
		return countdown;
	}
	public static void setCountdown(int i){
		countdown = i;
	}
	public static ArrayList<String[]> csvread(String filename){
		ArrayList<String[]> data = new ArrayList<String[]>();
		BufferedReader fn; 
		try{
		fn = new BufferedReader(new FileReader(filename));
		String s;
		while((s=fn.readLine())!=null){
			s = s.replace("\"", "");
			String[] st1 = s.split(",");
			data.add(st1);
		}
		fn.close();
		}catch(IOException e){System.out.println("file not found");}
		return data;
	}
	public void run(){
		while( AmbulanceDisplay.sign == true && Integer.parseInt(AmbulanceDisplay.time)!=0){
			try{
			Thread.sleep(1000);
			move(choose);
			for(int i=0; i<patient_data.size(); i++){
				for(int j=0; j<ambulance_data.size(); j++){
					if(!((patient_data.get(i))[3].equals("Completed")) || !(ambulance_data.get(j)[3].equals("At Station"))){
						System.out.println("thread "+ choose + " stopped.");
						countdown += 1;
						break;
					}
				}
			}
			if(countdown == ambulance_data.size()){
				JOptionPane.showMessageDialog(new JFrame(),"All patients transported","Information",JOptionPane.INFORMATION_MESSAGE);
				AmbulanceDisplay.sign = false;
			}
			}catch(InterruptedException e4){}
		}
	}
	public synchronized void move(int choose){
		String[] ambulance = ambulance_data.get(choose);
		if(ambulance[3].equals("At Station")){
			ArrayList<Integer> distance = new ArrayList<Integer>();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			ArrayList<Integer> p_name = new ArrayList<Integer>();
			for(int i=0; i<patient_data.size(); i++){
				if((patient_data.get(i))[3].equals("Pending")){
					int x1 = Integer.parseInt(ambulance[1]);
					int y1 = Integer.parseInt(ambulance[2]);
					int x2 = Integer.parseInt((patient_data.get(i))[1]);
					int y2 = Integer.parseInt((patient_data.get(i))[2]);
					int sum = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
					temp.add(sum);
					distance.add(sum);
					p_name.add(i);
				}
			}
			if(temp.size() != 0){
				Collections.sort(temp);
				int index1 = distance.indexOf(temp.get(0));
				int index = p_name.get(index1);
				String[] s1 = new String[5];
				for(int i=0; i<3; i++){
					s1[i]= (patient_data.get(index))[i];
				}
				s1[3] = "Assigned";
				s1[4] = ambulance[0];
				patient_data.set(index, s1);
				
				int pos = ambulance_data.indexOf(ambulance);
				String[] s2 = new String[5];
				for(int i=0; i<3; i++){
					s2[i]= ambulance[i];
				}
				s2[3] = "Responding";
				s2[4] = patient_data.get(index)[0];
				ambulance_data.set(pos, s2);
				System.out.println(s2[0] + " responding to " + s2[4]);
			}
		}
		else if(ambulance[3].equals("Responding")){
			int x1 = Integer.parseInt(ambulance[1]);
			int y1 = Integer.parseInt(ambulance[2]);
			int pos = ambulance_data.indexOf(ambulance);
			int index=0;
			for(int i=0; i<patient_data.size(); i++){
				if(((patient_data.get(i))[0]).equals(ambulance[4])){index = i;}
			}
			if(!patient_data.get(index)[4].equals(ambulance[0])){
				ambulance[3] = "At Station";
				ambulance_data.set(pos, ambulance);
			}
			else{
				if(x1 == 10 && y1 == 0){number1 -= 1;}
				else if(x1 == 30 && y1 == 80){number2 -= 1;}
				else if(x1 == 90 && y1 == 20){number3 -= 1;}
				int x2 = Integer.parseInt((patient_data.get(index))[1]);
				int y2 = Integer.parseInt((patient_data.get(index))[2]);
				if((Math.abs(x1-x2)+Math.abs(y1-y2))<=4){
					x1 = x2;
					y1 = y2;
					ambulance[3] = "At Scene";	
					System.out.println(ambulance[0] + " arrived at " + ambulance[4]);	
				}
				else if((x1-x2)>4){x1 -= 4;}
				else if((x2-x1)>4){x1 += 4;}
				else if((y1-y2)>4){y1 -= 4;}
				else if((y2-y1)>4){y1 += 4;}
				else if(x1>=x2 && y1>=y2){
					x1 -= (x1-x2);
					y1 -= 4-(x1-x2);
				}
				else if(x1>=x2 && y1<y2){
					x1 -= (x1-x2);
					y1 += 4-(x1-x2); 
				}
				else if(x1<x2 && y1>=y2){
					x1 += (x2-x1);
					y1 -= 4-(x2-x1);
				}
				else if(x1<x2 && y1<y2){
					x1 += (x2-x1);
					y1 += 4-(x2-x1); 
				}
				ambulance[1] = ""+x1;
				ambulance[2] = ""+y1;
				ambulance_data.set(pos, ambulance);	
			}
		}
		else if(ambulance[3].equals("At Scene")){
			before_transport -= 1;//wait for 4 seconds
			if(before_transport == 0){
			int pos = ambulance_data.indexOf(ambulance);
			ambulance[3] = "Transporting";
			ambulance_data.set(pos, ambulance);
			for(int i=0; i<patient_data.size(); i++){
				if(((patient_data.get(i))[0]).equals(ambulance[4])){
					String[] the_patient = patient_data.get(i);
					the_patient[3] = "Transporting";
					patient_data.set(i, the_patient);
				}
			}
			System.out.println(ambulance[0] + " transporting " + ambulance[4]);
			}
		}
		else if(ambulance[3].equals("Transporting")){
			int x1 = Integer.parseInt(ambulance[1]);
			int y1 = Integer.parseInt(ambulance[2]);
			int pos = ambulance_data.indexOf(ambulance);
			if((Math.abs(x1-50)+Math.abs(y1-50))<=3){				
				for(int i=0; i<patient_data.size(); i++){
					if(((patient_data.get(i))[0]).equals(ambulance[4])){
						String[] the_patient = patient_data.get(i);
						the_patient[3] = "Completed";
						the_patient[4] = "";
						patient_data.set(i, the_patient);
					}
				}
				x1 = 50;
				y1 = 50;
				System.out.println(ambulance[0] + " at destination, " + ambulance[4] + " completed.");
				ambulance[3] = "At Destination";
				ambulance[4] = "";
			}
			else if(x1-50>3){x1 -= 3;}
			else if(y1-50>3){y1 -= 3;}
			else if(50-x1>3){x1 += 3;}
			else if(50-y1>3){y1 += 3;}
			else if(x1<=50 && y1<=50){
				x1 += (50-x1);
				y1 += 3-(50-x1);
			}
			else if(x1<=50 && y1>50){
				x1 += (50-x1);
				y1 -= 3-(50-x1);
			}
			else if(x1>50 && y1<=50){
				x1 -= (x1-50);
				y1 += 3-(x1-50);
			}
			else if(x1>50 && y1>50){
				x1 -= (x1-50);
				y1 -= 3-(x1-50);
			}
			ambulance[1] = ""+x1;
			ambulance[2] = ""+y1;
			ambulance_data.set(pos, ambulance);	
		}
		else if(ambulance[3].equals("At Destination")){			
			before_returning -= 1; //wait for 2 sceonds
			if(before_returning == 0){
			int pos = ambulance_data.indexOf(ambulance);
			ambulance[3] = "Returning";
			ambulance_data.set(pos, ambulance);}
		}
		else if(ambulance[3].equals("Returning")){
			int x1 = Integer.parseInt(ambulance[1]);
			int y1 = Integer.parseInt(ambulance[2]);
			int pos = ambulance_data.indexOf(ambulance);
			if(x1 == 50 && y1 == 50){ //at the hospital
				if(number2<available_number){  //return to bluelane
					flag = 2;
					number2 += 1;
					x1 = x1-3; 
					ambulance[1] = ""+x1;
					ambulance_data.set(pos, ambulance);
				}
				else if(number3<available_number){ // return to redvill
					flag = 3;
					number3 += 1;
					x1 = x1+3;
					ambulance[1] = ""+x1;
					ambulance_data.set(pos, ambulance);					
				}
				else if(number1<available_number){ // return to greenfields
					flag = 1;
					number1 += 1;
					x1 = x1-3;
					ambulance[1] = ""+x1;
					ambulance_data.set(pos, ambulance);					
				}
			}
			else{ 
				int x2=0, y2=0;
				if(flag == 1){
					x2 = 10;
					y2 = 0;
				}
				else if(flag == 2){
					x2 = 30;
					y2 = 80;
				}
				else if(flag == 3){
					x2 = 90;
					y2 = 20;
				}
				if((Math.abs(x1-x2)+Math.abs(y1-y2))<=3){
					x1 = x2;
					y1 = y2;
					ambulance[3] = "At Station";	
					System.out.println(ambulance[0] + " at station" + x1 + " " + y1);	
				}
				else if((x1-x2)>3){x1 -= 3;}
				else if((x2-x1)>3){x1 += 3;}
				else if((y1-y2)>3){y1 -= 3;}
				else if((y2-y1)>3){y1 += 3;}
				else if(x1>=x2 && y1>=y2){
					x1 -= (x1-x2);
					y1 -= 3-(x1-x2);
				}
				else if(x1>=x2 && y1<y2){
					x1 -= (x1-x2);
					y1 += 3-(x1-x2); 
				}
				else if(x1<x2 && y1>=y2){
					x1 += (x2-x1);
					y1 -= 3-(x2-x1);
				}
				else if(x1<x2 && y1<y2){
					x1 += (x2-x1);
					y1 += 3-(x2-x1); 
				}
				ambulance[1] = ""+x1;
				ambulance[2] = ""+y1;
				ambulance_data.set(pos, ambulance);					
			}
		}
	}
	public static ArrayList<String[]> getDATA(){
		return ambulance_data;
	}
	public static ArrayList<String[]> getpatientDATA(){
		return patient_data;
	}
	public static void setDATA(ArrayList<String[]> s1, ArrayList<String[]> s2){
		ambulance_data = s1;
		patient_data = s2;
	}
	public static void setNumber(int x1, int x2, int x3, int y){
		available_number=y;
		number1=x1;
		number2=x2;
		number3=x3;
	}
	/*public static void main(String[] args){		
		ambulance_data = csvread("ambulances.csv");
		patient_data = csvread("patients.csv");
		available_number = (int)Math.ceil((ambulance_data.size()-1)/3.0);
		for(int i=1; i<ambulance_data.size(); i++){
			int x1 = Integer.parseInt((ambulance_data.get(i))[1]);
			int y1 = Integer.parseInt((ambulance_data.get(i))[2]);
			if(x1 == 10 && y1 == 0){number1 += 1;}
			else if(x1 == 30 && y1 == 80){number2 += 1;}
			else if(x1 == 90 && y1 == 20){number3 += 1;}
		}
		for(int i=1; i<ambulance_data.size(); i++){
			(new SimulationProcess(i)).start();
		}
	}*/
}