public class Pair1 implements  Comparable<Pair1>{
    double chance;
    int index;
    @Override
    public int compareTo(Pair1 p2){
        if (this.chance<p2.chance){
            return 1;
        }
        if (this.chance>p2.chance){
            return -1;
        }
        return  0;
    }
    public Pair1 (double c,int in){
        chance=c;
        index=in;
    }

}