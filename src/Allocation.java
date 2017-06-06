import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Scanner;

public class Allocation{
    private String path = "C:/Users/arsee/Desktop/INRIAPerson/";
    int width=8;

    int min=4;
    int max=18;

    int height=16;
    public BufferedImage im1() throws Exception{
        Scanner sc =new Scanner(new File(path+"Train/neg.lst"));
        int g =0;
        while (sc.hasNext()){

            BufferedImage img = ImageIO.read(new File(path+sc.nextLine()));
            max = img.getHeight()/height;

            for (int i=0;i<25;i++){
                g++;
                int widthimg = width*(int)((min+Math.random()*(max-min)));
                int heightimg =(int) 2.5*widthimg;
                System.out.print(widthimg+" "+heightimg+"\n");
                int x1 = (int)(Math.random()*(img.getWidth()-widthimg));
                int y1 =(int)(Math.random()*(img.getHeight()-heightimg));
                BufferedImage image = img.getSubimage(x1,y1,widthimg,heightimg);
                BufferedImage image2 = new BufferedImage(64,160,5);
                Graphics gr = image2.getGraphics();
                gr.drawImage(image,0,0,64,160,null);
                ImageIO.write(image2,"png",new File(path+"/Train/-1/"+g+".png"));
            }

        }




        return null;

    }
    public void i1() throws  Exception{
        int g=0;
        Scanner sc = new Scanner(new File(path+"Train/annotations.lst"));
        int i=0;
        ArrayList<String> ar=new ArrayList<>();
        while (sc.hasNext()){
            String s = sc.nextLine();
            ar.add(i,path+s);
            System.out.print(ar.get(i)+"\n");
           i++;

        }
        sc.reset();
        int f=0;
        for (int j=0;j<ar.size();j++){
            Scanner sc2 = new Scanner(new File(ar.get(j)),"cp1251");
            String file="";
            while (sc2.hasNextLine()){
                int x1,x2,y1,y2;
                String line =sc2.nextLine();
                if (line.contains("Image filename")){
                   file= path+line.replace('"','*').replace("*","").split(" ")[3];
                   //System.out.print(file+"\n");
                }
                if (line.contains("Bounding box for")){
                   line = line.replace(",","").replace(")","").replace("(","")
                           .replace("-","");
                    //System.out.print(line);

                    String [] strings = line.split(" ");
                    y2 = Integer.parseInt(strings[strings.length-1]);
                    x2 =Integer.parseInt(strings[strings.length-2]);
                    y1 =Integer.parseInt(strings[strings.length-4]);
                    x1 =Integer.parseInt(strings[strings.length-5]);
                    double ratio =2.5;
                    BufferedImage img = ImageIO.read(new File(file));
                    int dx=x2-x1;
                    int cx = x1+(x2-x1)/2;
                    int cy = y1+(y2-y1)/2;
                    int dy=y2-y1;
                    if (ratio*dx>dy){
                        y2=Math.max((int)(cy+dx*ratio/2),y2);
                        y1=Math.min(y1,(int)(cy-dx*ratio/2));
                    }else{
                        x1=Math.min(x1,(int)(cx-dy/ratio/2));
                        x2=Math.max(x2,(int)(cx+dy/ratio/2));
                    }
                    //System.out.print(x1+" "+y1+" "+x2+" "+y2);



                        for (int ji=0;ji<5;ji++) {

                        x1--;
                        x2++;
                        y2++;
                        y1--;
                            try {
                            BufferedImage img2 = img.getSubimage(x1, y1, x2 - x1, y2 - y1);
                                f++;
                            BufferedImage image2 = new BufferedImage(64, 160, 5);
                            Graphics gr = image2.getGraphics();
                            gr.drawImage(img2, 0, 0, 64, 160, null);
                            ImageIO.write(image2, "png", new File(path + "/Train/1/" + f + ".png"));
                            }catch (Exception e){
                                // System.out.print(e.getMessage());
                            }
                        }

                }
            }
            sc2.reset();
        }

    }
}