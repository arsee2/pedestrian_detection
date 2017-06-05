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
    private static String path ="test-public";
    private static int i1=481;
    private static int im1=1677;
    Features() throws  Exception{
        String answer="";
        Writer wr = new FileWriter(path+"/features.idl");
        for (int i=1;i<i1-1;i++){

            BufferedImage img = ImageIO.read(new File(path + "/1/"+i+".png"));
            answer="";
            HOGProcessor hog = new HOGProcessor(img);
            hog.processImage();

            for (int j=0;j<hog.getHOGDescriptors().size();j++){
               wr.append(String.valueOf(hog.getHOGDescriptors().get(j))+" ");
            }
            Classify classify = new Classify();
            classify.classOf(hog.getHOGDescriptors());
            wr.append("1\n");
            System.out.print(100*i/i1+"%\n");
        }

        for (int i=1;i<im1;i++){

            BufferedImage img = ImageIO.read(new File(path + "/-1/"+i+".png"));
            HOGProcessor hog = new HOGProcessor(img);
            hog.processImage();
            int f=0;
            for (int j=0;j<hog.getHOGDescriptors().size();j++){
                wr.append(String.valueOf(hog.getHOGDescriptors().get(j))+" ");
                f++;
            }
            Classify classify = new Classify();

            wr.append("-1\n");
            System.out.print(f+" "+100*i/im1+"%\n");
        }
        wr.flush();
    }
}
