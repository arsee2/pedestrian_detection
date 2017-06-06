

    import javafx.util.Pair;

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
        private class Rect {
            int x,x1,y,y1;
            public Rect(int i, int j, int i1, int j1) {
                x=i;
                x1=i1;
                y=j;
                y1=j1;
            }

        }
        private BufferedImage crop(BufferedImage im){
            int maxHeight = 750;
            int maxWidth = 300;
            int height=img.getHeight(),width=img.getWidth();
            double ratio =(double)im.getWidth()/im.getHeight();
            if (im.getHeight()>maxHeight){
                height=maxHeight;
                width=(int)(ratio*height);
            }
            if (im.getWidth()>maxWidth){
                width=maxWidth;
                height=(int)(width/ratio);
            }
            BufferedImage image2 = new BufferedImage(width, height, 5);
            Graphics gr = image2.getGraphics();
            gr.drawImage(im, 0, 0, width, height, null);
            return image2;
        }



        private static boolean is_crossed (Rect a, Rect b) {
            return(
                    (
                            (
                                    ( a.x>=b.x && a.x<=b.x1 )||( a.x1>=b.x && a.x1<=b.x1  )
                            ) && (
                                    ( a.y>=b.y && a.y<=b.y1 )||( a.y1>=b.y && a.y1<=b.y1 )
                            )
                    )||(
                            (
                                    ( b.x>=a.x && b.x<=a.x1 )||( b.x1>=a.x && b.x1<=a.x1  )
                            ) && (
                                    ( b.y>=a.y && b.y<=a.y1 )||( b.y1>=a.y && b.y1<=a.y1 )
                            )
                    )
            )||(
                    (
                            (
                                    ( a.x>=b.x && a.x<=b.x1 )||( a.x1>=b.x && a.x1<=b.x1  )
                            ) && (
                                    ( b.y>=a.y && b.y<=a.y1 )||( b.y1>=a.y && b.y1<=a.y1 )
                            )
                    )||(
                            (
                                    ( b.x>=a.x && b.x<=a.x1 )||( b.x1>=a.x && b.x1<=a.x1  )
                            ) && (
                                    ( a.y>=b.y && a.y<=b.y1 )||( a.y1>=b.y && a.y1<=b.y1 )
                            )
                    )
            );
        }
        public Main() throws Exception {



            try {
                //new Features();
               // new Allocation().i1();;
                img = ImageIO.read(new File("C:/Users/arsee/Desktop/INRIAPerson/Test/pos/crop_000021.png"));
                img = crop(img);
                double ratio =2.5;
                int width;
                int limitW = img.getWidth()/32;
                int limitH = img.getHeight()/32;
                int height;
                if (img.getHeight()>ratio*img.getWidth()){
                     width = img.getWidth();
                     height = (int)(width*ratio);
                }else{
                    height = img.getHeight();
                    width = (int)(height/ratio);
                }
                int stepW= 10;
                int step = 20;
                Classify classify= new Classify();
                ArrayList<Integer> points = new ArrayList<Integer>();
                int o=0;
                ArrayList<Pair1> array = new ArrayList<Pair1>();
                while (width>=limitW && height>=limitH) {
                    for (int i = 0; i < img.getWidth() - width - step; i += step) {
                        for (int j =0; j < img.getHeight() - height - step; j += step) {
                            // System.out.print(i+" "+j+" ");
                            BufferedImage img2 = img.getSubimage(i, j, width, height);
                            BufferedImage image2 = new BufferedImage(64, 160, 5);
                            Graphics gr = image2.getGraphics();
                            gr.drawImage(img2, 0, 0, 64, 160, null);
                            Hog hog = new Hog(image2);
                            if (classify.classOf(hog.description)) {
                                //System.out.print("Yes");
                                points.add(i);
                                points.add(j);
                                points.add(i + width);
                                points.add(j + height);
                                array.add(new Pair1(classify.chance(hog.description), o));
                                o++;
                            } else {
                                //System.out.print("No");
                            }
                        }
                    }
                    width-=stepW;
                    height=(int)(width*ratio);
                }
                Graphics gr = img.createGraphics();
                gr.setColor(Color.GREEN);
                ArrayList<Rect>  used= new ArrayList<Rect>();
                array.sort(Pair1::compareTo);;
                for (int j=0;j<array.size();j++){
                    boolean sd = false;
                    int i=array.get(j).index*4;
                    int x1 = points.get(i);
                    int x2 = points.get(i + 2);
                    int dx = x2 - x1;
                    int cx = x1 + (x2 - x1) / 2;
                    int y1 = points.get(i + 1);
                    int y2 = points.get(i + 3);
                    int cy = (y2 - y1) / 2 + y1;
                    int dy = y2 - y1;
                    y1 =(int) (cy - dy /2.5);
                    y2 = (int)(cy + dy / 2.5);
                    x1 = (int)(cx - dx / 2.5);
                    x2 = (int)(cx + dx / 2.5);
                    Rect rect = new Rect(x1, y1, x2, y2);
                    boolean flag = true;
                    for (int f = 0; f < used.size(); f++) {
                        if (is_crossed(rect, used.get(f))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {

                        if (array.get(j).chance>0.7) {
                            gr.drawString(String.format("%.3f",array.get(j).chance),cx,cy);
                            sd=true;
                            gr.drawRect(points.get(i), points.get(i + 1), points.get(i + 2) - points.get(i), points.get(i + 3) - points.get(i + 1));
                            used.add(rect);
                        }
                    }


                }
                System.out.print(used.size()+" ");

//                for (int i=0;i<points.size();i+=4) {
//                    int x1 = points.get(i);
//                    int x2 = points.get(i + 2);
//                    int dx = x2 - x1;
//                    int cx = x1 + (x2 - x1) / 2;
//                    int y1 = points.get(i + 1);
//                    int y2 = points.get(i + 3);
//                    int cy = (y2 - y1) / 2 + y1;
//                    int dy = y2 - y1;
//                    y1 =(int) (cy - dy /3);
//                    y2 = (int)(cy + dy / 3);
//                    x1 = (int)(cx - dx / 3);
//                    x2 = (int)(cx + dx / 3);
//                    Rect rect = new Rect(x1, y1, x2, y2);
//                    boolean flag = true;
//                    for (int j = 0; j < used.size(); j++) {
//                        if (is_crossed(rect, used.get(j))) {
//                            flag = false;
//                            break;
//                        }
//                    }
//                    if (flag) {
//                        //gr.drawString(String.format("%.3f",chances.get(i/4)),cx,cy);
//                        gr.drawRect(points.get(i), points.get(i + 1), points.get(i + 2) - points.get(i), points.get(i + 3) - points.get(i + 1));
//                        used.add(rect);
//                    }
//                }


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