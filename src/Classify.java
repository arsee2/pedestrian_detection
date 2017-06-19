import java.io.File;
import java.util.List;
import java.util.Scanner;


/**
 * Created by arsee on 01.06.2017.
 */
public class Classify  {
    int num_of_features=1440;
    double w[] ;
    String path ="java-svm.model";
    double porog =0;
    double point[];
    Classify()throws Exception{

            Scanner sc = new Scanner(new File(path));
            num_of_features = Integer.parseInt(sc.nextLine());
            sc.nextLine();
            sc.nextLine();
            sc.nextLine();
             w = new double[num_of_features+1];
            porog = Double.parseDouble(sc.nextLine());
            for (int i=0;i<num_of_features;i++){
                w[i]=Double.parseDouble(sc.nextLine());

            }
            double sum=0;



    }
    public boolean classOf(double ar[]){
        double sum=0;
        double sq=0;
        for(int i=0;i<num_of_features;i++){
            sum+=w[i]*ar[i];

        }
           //System.out.print(porog+" ");
        return (sum>porog-0.05);
    }
    public double chance (double ar []){
        double sum=0;
        double sq=0;
        for(int i=0;i<num_of_features;i++){
            sum+=w[i]*ar[i];
            sq+=w[i]*w[i];
        }
        double dx = sum;
        return sum;
    }
}
