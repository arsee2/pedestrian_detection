

import  java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;


public class Main extends Component  {

    BufferedImage img;
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
    int b=0;
    public Main() throws Exception {

        try {
            img = new Allocation().im();
            //img = ImageIO.read(new File("IM4.jpg"));
            Hog hog1=new Hog(img);
            img = hog1.getHogRepresetation();



        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100,100);
        } else {
            return new Dimension(img.getWidth(null), img.getHeight(null));
        }
    }

    public static void main(String[] args) throws   Exception{

        JFrame f = new JFrame("Detection");

        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.add(new Main());
        f.pack();
        f.setVisible(true);
    }
}