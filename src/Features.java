import com.dgimenes.jhog.HOGProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Created by arsee on 31.05.2017.
 */
public class Features {
    private static String path ="C://Users/arsee/Desktop/INRIAPerson/Train";
    private static int i1=6000;
    private static int im1=30450;
    Features() throws  Exception{
        String answer="";
        Writer wr = new FileWriter(path+"/features.idl");
        for (int i=1;i<i1-1;i++){

            BufferedImage img = ImageIO.read(new File(path + "/1/"+i+".png"));
            answer="";
            Hog hog = new Hog(img);
            int f=0;
            for (int j=0;j<hog.description.length;j++){
               // wr.append(String.valueOf(hog.description[j])+" ");
                f++;
            }
            Classify classify = new Classify();
            System.out.print(classify.classOf(hog.description));
            wr.append("1\n");
            System.out.print(i+" from "+i1+" features: "+f+" "+100*i/i1+"%\n");
        }

        for (int i=1;i<im1;i++){

            BufferedImage img = ImageIO.read(new File(path + "/-1/"+i+".png"));
            Hog hog = new Hog(img);
            Classify classify = new Classify();
            int f=0;
            for (int j=0;j<hog.description.length;j++){
                //wr.append(String.valueOf(hog.description[j])+" ");
                f++;
            }
            System.out.print(classify.classOf(hog.description));


            wr.append("-1\n");
            System.out.print(f+" "+100*i/im1+"%\n");
        }
        wr.flush();
    }
}
