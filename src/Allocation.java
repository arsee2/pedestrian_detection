import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Scanner;

public class Allocation{
    private String path = "D:\\INRIAPerson\\";
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
            for (int j=32;j<img.getWidth()-10 && j*2.5<img.getHeight()-10 ;j+=20) {
                for (int i = 0; i < 8; i++) {
                    g++;
                    int widthimg = j;
                    int heightimg = (int) 2.5 * widthimg;
                    System.out.print(widthimg + " " + heightimg + "\n");
                    int x1 = (int) (Math.random() * (img.getWidth() - widthimg));
                    int y1 = (int) (Math.random() * (img.getHeight() - heightimg));
                    BufferedImage image = img.getSubimage(x1, y1, widthimg, heightimg);
                    BufferedImage image2 = new BufferedImage(64, 160, 5);
                    Graphics gr = image2.getGraphics();
                    gr.drawImage(image, 0, 0, 64, 160, null);
                    ImageIO.write(image2, "png", new File(path + "/Train/-1/" + g + ".png"));
                }
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
                    double dox1=x1;
                    double dox2=x2;
                    double doy1=y1;
                    double doy2=y2;

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
                        double k=0.01;

                            x1-=2;
                            x2+=2;
                            y1-=5;
                            y2+=5;
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
            sc2.reset();
        }

    }
    public static BufferedImage crop (BufferedImage img,int x1,int y1,int x2,int y2){
        System.out.print(x1+" "+y1+" "+x2+" "+y2+"\n");
        int dx=x2-x1;
        int cx = x1+(x2-x1)/2;
        int cy = y1+(y2-y1)/2;
        int dy=y2-y1;
        double ratio=2.5;
        int width=0;
        int height=0;
        if (dx*ratio>dy){
            width=dx;
            height= (int)(dx*((double)(ratio)));
        }else {
            height=dy;
            width = (int)(dy/ratio);
         }
         if (cx-width/2<0 || cy-height/2<0 || cx+width/2>img.getWidth() || cy+height/2>img.getHeight()){
             if (dx*ratio<dy){
                 width=dx;
                 height= (int)(dx*((double)(ratio)));
             }else {
                 height=dy;
                 width = (int)(dy/ratio);
             }
         }
        if (Math.abs(dy/dx)<0.1){
            width=dx;
            height=dy;
        }
        if (cx-width/2<0 || cy-height/2<0 || cx+width/2>img.getWidth() || cy+height/2>img.getHeight()){
             width=dx;
             height=dy;
            BufferedImage image2 = new BufferedImage(64,160,5);
            Graphics gr = image2.getGraphics();
            gr.drawImage(img,0,0,64,160,null);
            return image2;

        }

        System.out.print(height+" "+width+" "+cx+" "+cy+"\n");
         BufferedImage image =img.getSubimage(cx-width/2,cy-height/2,width,height);
         BufferedImage image2 = new BufferedImage(64,160,5);
         Graphics gr = image2.getGraphics();
         gr.drawImage(image,0,0,64,160,null);
         return image2;
    }
    public void order () throws  Exception{
        String path = "C:/Users/arsee/Desktop/cvpr10_multiview_pedestrians/-1/";
        File [] files = (new File(path).listFiles());

        for (int i=0;i<files.length;i++){
            BufferedImage img = ImageIO.read(new File(files[i].getPath()));
            ImageIO.write(img,"png",new File(path+(i+1)+".png"));
        }
    }
    public void hFormat() throws Exception{
        String path = "C:/Users/arsee/Desktop/cvpr10_multiview_pedestrians/";


        String line="";
        String pi="";
        int g=0;
        int gg=0;
        int ni=0;
        for (int yu=1;yu<=8;yu++) {
            String file = "viewpoints_train"+yu+".al";
            Scanner sc = new Scanner(new File(path + file));
            ArrayList<Integer> rects = new ArrayList<Integer>();
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.contains("<image>")) {
                    if (pi != "") {

                        BufferedImage img = ImageIO.read(new File(path + pi));
                        for (int i = 0; i < rects.size(); i += 4) {
                            try {
                                BufferedImage image2 = crop(img, rects.get(i), rects.get(i + 1), rects.get(i + 2), rects.get(i + 3));
                                g++;
                                ImageIO.write(image2, "png", new File(path + "1/" + g + ".png"));
                            }catch (Exception e){
                                System.out.print(e.getMessage());
                            }


                        }
                        if (ni % 20 == 0) {
                            for (int j = 80; j < img.getHeight() * 9 / 10; j += img.getHeight() / 10) {
                                for (int i = 0; i < 2*(8 * img.getHeight() / j + 8); i+=2) {
                                    double coef = 2;
                                    int y1 = (int) (Math.random() * (img.getHeight() - j));
                                    int x1 = (int) (Math.random() * (img.getWidth() - j / 2.5));
                                    int x11 = (int) ((x1 + (j / 2.5) / 2) - j / 2.5 / coef);
                                    int y11 = (int) (y1 + (j / 2) - j / coef);
                                    int x22 = (int) ((x1 + (j / 2.5) / 2) + j / 2.5 / coef);
                                    int y22 = (int) (y1 + (j / 2) + j / coef);

                                    boolean flag = false;
                                    for (int k = 0; k < rects.size(); k += 4) {
                                        System.out.print("|");
                                        int i1 = rects.get(k);
                                        int j1 = rects.get(k + 1);
                                        int i2 = rects.get(k + 2);
                                        int j2 = rects.get(k + 3);
                                        int di = i2 - i1;
                                        int dj = j2 - j1;
                                        int i11 = (int) (i1 + di / 2 - di / coef);
                                        int i22 = (int) (i1 + di / 2 + di / coef);
                                        int j11 = (int) (j1 + di / 2 - di / coef);
                                        int j22 = (int) (j1 + di / 2 + di / coef);

                                        if (Detection.is_crossed(new Rect(i11, j11, i22, j22), new Rect(x11, y11, x22, y22))) {
                                            flag = true;
                                            break;
                                        }

                                    }
                                    if (!flag) {
                                        gg++;
                                        ImageIO.write(crop(img, x1, y1, (int) (x1 + j / 2.5), y1 + j), "png", new File(path + "-1/" + gg + ".png"));
                                    } else {
                                        i--;
                                    }
                                }
                            }
                        }
                        rects = new ArrayList<Integer>();
                        ni++;
                    }
                    String[] strs = line.split(">");
                    pi = strs[2].split("<")[0];
                    System.out.print(pi + "\n");
                }
                if (line.contains("<x1>")) {
                    String[] strs = line.split(">");
                    Integer point = Integer.parseInt(strs[2].split("<")[0]);
                    rects.add(point);
                    //System.out.print(point+"\n");
                }
                if (line.contains("<x2>") || line.contains("<y1>") || line.contains("<y2>")) {
                    String[] strs = line.split(">");
                    Integer point = Integer.parseInt(strs[1].split("<")[0]);
                    rects.add(point);
                    //System.out.print(point+"\n");
                }
            }
        }
    }
}