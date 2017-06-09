import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by arsee on 09.06.2017.
 */
public class Testing {
    String path ="D:\\INRIAPerson";
    public void testing() throws Exception{
        File [] files = (new File(path+"/Test/pos")).listFiles();
        Detection detection = new Detection();
        for (int i=0;i<files.length;i++){

            BufferedImage img = detection.detect(files[i].getPath());
            System.out.print(detection.sum+" ");
            ImageIO.write(img,"png",new File(path+"/testing/"+(i+288)+".png"));
        }
    }

}
