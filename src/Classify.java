import java.io.File;
import java.util.List;
import java.util.Scanner;


/**
 * Created by arsee on 01.06.2017.
 */
public class Classify  {
    int num_of_features=1440;
    double w []= new double[num_of_features+1];
    String path ="java-svm.model";
    double porog =0;
    Classify()throws Exception{

            Scanner sc = new Scanner(new File(path));
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
            porog = Double.parseDouble(sc.nextLine());
            for (int i=1;i<=num_of_features;i++){
                w[i]=Double.parseDouble(sc.nextLine());

            }

    }
    public boolean classOf(double[] ar){
        double sum=0;
        for(int i=1;i<=num_of_features;i++){
            sum+=w[i]*ar[i-1];
        }
          //  System.out.print(porog);
        return (sum>porog*1.2);
    }
    public double chance (double[] ar){
        double sum=0;
        for(int i=1;i<=num_of_features;i++){
            sum+=w[i]*ar[i-1];
        }
        double dx = sum-porog;
        return sum;
    }
}
