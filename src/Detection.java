import com.dgimenes.jhog.HOGProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by arsee on 07.06.2017.
 */
public class Detection {
    public void Detection (){
        sum=0;
    }
    private static BufferedImage crop(BufferedImage img){
        int maxHeight =  1000;
        int maxWidth =1000;
        int height=img.getHeight(),width=img.getWidth();
        double ratio =(double)img.getWidth()/img.getHeight();
        if (img.getHeight()>maxHeight){
            height=maxHeight;
            width=(int)(ratio*height);
        }
        if (img.getWidth()>maxWidth){
            width=maxWidth;
            height=(int)(width/ratio);
        }
        BufferedImage image2 = new BufferedImage(width, height, 5);
        Graphics gr = image2.getGraphics();
        gr.drawImage(img, 0, 0, width, height, null);
        return image2;
    }



    public static boolean is_crossed (Rect a, Rect b) {
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
    int sum;
    public  BufferedImage detect (String path) throws Exception{
        try {

            BufferedImage img = ImageIO.read(new File(path));
            img = crop(img);
            double ratio =2.5;
            int width;
            int crossingK=3;

            int height;
            if (img.getHeight()>ratio*img.getWidth()){
                width = img.getWidth();
                height = (int)(width*ratio);
            }else{
                height = img.getHeight();
                width = (int)(height/ratio);
            }
            int limitW = Math.max(32,width/4);
            int limitH = Math.max(80,height/4);
            int stepW= Math.max(2,width/6);
            int step = width/4;
            int g=0;
            Classify classify= new Classify();
            ArrayList<Integer> points = new ArrayList<Integer>();
            int o=0;
            ArrayList<Pair1> array = new ArrayList<Pair1>();
            while (width>=limitW && height>=limitH) {
                for (int i = 0; i < img.getWidth() - width ; i += step) {
                    for (int j =0; j < img.getHeight() - height ; j += step) {
                        // System.out.print(i+" "+j+" ");
                        BufferedImage img2 = img.getSubimage(i, j, width, height);
                        BufferedImage image2 = new BufferedImage(64, 160, 5);
                        Graphics gr = image2.getGraphics();
                        gr.drawImage(img2, 0, 0, 64, 160, null);
                        Hog hog = new Hog(image2);
                        if (classify.classOf(hog.description) ){
                            //System.out.print("Yes");
                            points.add(i);
                            points.add(j);
                            points.add(i + width);
                            points.add(j + height);
                            array.add(new Pair1(classify.chance(hog.description), o,width));
                            //ImageIO.write(image2,"png",new File("C:\\Users\\arsee\\Desktop\\new\\i"+o+".png"));
                           // ImageIO.write(hog.getHogRepresentation(),"png",new File("C:\\Users\\arsee\\Desktop\\new\\"+o+".png"));
                            o++;
                        } else {
                            //System.out.print("No");
                        }
                    }
                }
                width-=stepW;
                height=(int)(width*ratio);
                step = Math.max(2,width/6);
            }
            Graphics2D gr = img.createGraphics();
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
                y1 =(int) (cy - dy /crossingK);
                y2 = (int)(cy + dy / crossingK);
                x1 = (int)(cx - dx / crossingK);
                x2 = (int)(cx + dx / crossingK);
                Rect rect = new Rect(x1, y1, x2, y2);
                boolean flag = true;
                for (int f = 0; f < used.size(); f++) {
                    if (is_crossed(rect, used.get(f))) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {

                    if (Math.pow(array.get(j).chance,1)>-1999) {
                        gr.setFont(new Font("TimesRoman",Font.PLAIN,12));
                        gr.drawString(String.format("%.3f",array.get(j).chance),cx,cy);
                        sd=true;
                        gr.setStroke(new BasicStroke(2));

                        gr.drawRect(points.get(i), points.get(i + 1), points.get(i + 2) - points.get(i), points.get(i + 3) - points.get(i + 1));
                           used.add(rect);
//                        BufferedImage image3 =  new BufferedImage(img.getWidth(),img.getHeight(),5);
//                        Graphics gr2 = image3.getGraphics();
//                        gr2.setColor(Color.GREEN);
//                        gr2.drawImage(img,0,0,img.getWidth(),img.getHeight(),null);
//                        gr2.drawString(String.format("%.3f",array.get(j).chance),cx,cy);
//                        g++;
//                        gr2.drawRect(points.get(i), points.get(i + 1), points.get(i + 2) - points.get(i), points.get(i + 3) - points.get(i + 1));
//                        //ImageIO.write(image3,"png",new File("C:/Users/arsee/Desktop/new/"+g+".png"));
                    }
                }


            }
            sum+=array.size();

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
            return img;

        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        return null;
    }
}
