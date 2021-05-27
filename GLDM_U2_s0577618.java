package uebung2;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class GLDM_U2_s0577618 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	//IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
    	IJ.open("/Users/milanatran/Documents/Schule/Semester 2/GDM/orchid.jpeg");
		
		GLDM_U2_s0577618 pw = new GLDM_U2_s0577618();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    
    public void run(String arg) {
    	if (imp==null) 
    		imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);
        
        storePixelValues(imp.getProcessor());
        
        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
    	width = ip.getWidth();
		height = ip.getHeight();
		
		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderBrightness;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private JSlider jSliderContrast;
		private double brightness;
		private double saturation = 1;
		private double hue;
		private double contrast = 1;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", 0, 256, 128);
            jSliderSaturation = makeTitledSilder("Sättigung", 0, 8, 4);
            jSliderHue = makeTitledSilder("Hue", 0, 360, 0);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 10, 5);
            panel.add(jSliderBrightness);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);
            panel.add(jSliderContrast);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue()-128;
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderSaturation) {
				if(slider.getValue() > 4) {
					saturation = slider.getValue() -3;
				} else {
					saturation = slider.getValue() / 4.0;
				}
				String str = "Sättigung " + saturation; 
				setSliderTitle(jSliderSaturation, str); 
			}
			
			if (slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Hue " + hue; 
				setSliderTitle(jSliderHue, str); 
			}
			
			if (slider == jSliderContrast) {
				if(slider.getValue() > 5) {
					contrast = slider.getValue() - (10 - slider.getValue());
				} else {
					contrast = slider.getValue() / 5.0;
				}
				String str = "Kontrast " + contrast; 
				setSliderTitle(jSliderContrast, str); 
			}
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					int yWert = (int) (0.299 * r + 0.587 * g + 0.114 * b);
					int uWert = (int) ((b - yWert) * 0.493);
					int vWert = (int) ((r-yWert) * 0.877);
					
					//Brightness, saturation and hue
					
					 // double yn = (yWert + brightness); 
					  //double un = uWert + (uWert * Math.cos(Math.toRadians(hue)) - vWert * Math.sin(Math.toRadians(hue))) * saturation; 
					  //double vn = vWert + (uWert * Math.sin(Math.toRadians(hue)) + vWert * Math.cos(Math.toRadians(hue))) * saturation;
					 
					//Hinweis: Für die Aufagbe 4 hat Nermin Rustic (s0577683) mir geholfen 
					//Brightness
					double yn = yWert + brightness;
					double un = uWert;
					double vn = vWert;
					
					//Saturation
					un += (uWert * saturation) - uWert;
					vn += (vWert * saturation) - vWert;
					
					//Hue
					un += (uWert * Math.cos(Math.toRadians(hue)) - vWert * Math.sin(Math.toRadians(hue))) - uWert;
					vn += (uWert * Math.sin(Math.toRadians(hue)) + vWert * Math.cos(Math.toRadians(hue))) - vWert;
					//Contrast
					yn += (contrast * (yWert -128) + 128) - yWert;
					un += (contrast * uWert) - uWert;
					vn += (contrast * vWert) - vWert;
					
					
					
					//YUV wieder in RGB
					int rn = (int) (yn + vn/0.877);
					int bn = (int) (yn + un/0.493);
					int gn = (int) (1/0.587 * yn - 0.299/0.587*rn - 0.114/0.587 * bn);
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					//Korrigieren des Überlaufs
					rn = Math.min(255, Math.max(0, rn));
					gn = Math.min(255, Math.max(0, gn));
					bn = Math.min(255, Math.max(0, bn));
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
}