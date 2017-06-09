

    import com.dgimenes.jhog.HOGProcessor;

    import  java.io.*;
    import java.awt.*;
    import java.awt.event.*;
    import java.awt.image.*;
    import javax.imageio.*;
    import javax.swing.*;


    public class Main extends Component  {

        BufferedImage img;
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
        int b=0;

        public Main() throws Exception {

           new Testing().testing();
            img = new Detection().detect("im5.jpg");

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