import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class Allocation{
    private String path = "C://Users//arsee//Desktop/";
    public BufferedImage im() throws Exception{
        Scanner sc =new Scanner(new File(path+"left/data.txt"));
        int x1,x2,y1,y2;
        BufferedImage img =null;
        while (sc.hasNextLine()){
          String line =sc.nextLine();
          line = line.replace(",","").replace('"','*').replace("*","").
                  replace("(","").replace(")","").replace(";","").replace(":","");
          //System.out.print(line);
          String [] strings = line.split(" ");
         img =ImageIO.read(new File(path+strings[0]));
          Graphics gr  =img.getGraphics();
          for (int i=1;i<strings.length;i+=4){
              x1 = Integer.valueOf(strings[i]);
              y1 = Integer.valueOf(strings[i+1]);
              x2 = Integer.valueOf(strings[i+2]);
              y2 = Integer.valueOf(strings[i+3].replace(".",""));
              //System.out.print(x1+" "+y1+" "+x2+" "+y2+"\n");
              //gr.drawRect(x1,y1,x2-x1,y2-y1);

          }


        }
        return img;
    }
}