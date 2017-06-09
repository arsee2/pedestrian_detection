public class Pair1 implements  Comparable<Pair1>{
    double chance;
    int index;
    int width;
    @Override
    public int compareTo(Pair1 p2){
        double k1 =this.width*this.chance;
        double k2 =p2.width*p2.chance;
        if (k1<k2){
            return 1;
        }
        if (k1>k2){
            return -1;
        }
        if (k1==k2){
            if (this.chance<p2.chance){
                return 1;
            }else{
                return -1;
            }
        }
        return 0;
    }
    public Pair1 (double c,int in,int w){
        chance=c;
        index=in;
        width=w;
    }

}