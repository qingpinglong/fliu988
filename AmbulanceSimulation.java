import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class AmbulanceSimulation{
	public static void main(String[] Args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AmbulanceDisplay();
				new VisualisationInterface();
            }
        });		
	}
}