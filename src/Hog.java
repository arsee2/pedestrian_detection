import com.dgimenes.jhog.util.ImageProcessingUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by arsee on 02.06.2017.
 */
public class Hog {
    private static int blockK=2;
    public static int widthOfCell = 8;
    public static int heightOfCell = 8;
    private static  int numOfcellsH;
    private static  int numOfcellsW;
    private double[][] pixelLuminMatrix;
    private int width;
    private int height;
    private  double[][] dxMatrix;
    private  double[][] dyMatrix;
    public double gradients[][];
    public double vectorOfGradients[][];
    public double description[];
    BufferedImage originalImage;
    BufferedImage image;


    public Hog(BufferedImage im) {
        originalImage = im;
        image = im;
        width =im.getWidth();
        height=im.getHeight();
        gradients =new double [im.getWidth()][im.getHeight()];
        vectorOfGradients =new double [im.getWidth()][im.getHeight()];
        dxMatrix=new double [im.getWidth()][im.getHeight()];
        dyMatrix=new double [im.getWidth()][im.getHeight()];
        description = new double[(im.getWidth()/widthOfCell)*(im.getHeight()/heightOfCell)*9];
        numOfcellsH=im.getHeight()/heightOfCell;
        numOfcellsW=im.getWidth()/widthOfCell;
        createPixelLuminosityMatrix();
        calculateHOG();
        globalNormalization();
        //blockNormalisation();
    }
    private void blockNormalisation(){
        double desc [][][]= new double[numOfcellsW][numOfcellsH][9];
        int f=0;
        //for (int i=0;i<description.length;i++){
            //System.out.print(description[i]+" ");
        //}
        System.out.print("\n");
        for (int i=0;i<numOfcellsW;i++){
            for (int j=0;j<numOfcellsH;j++){
                for (int k=0;k<9;k++){
                    desc[i][j][k]=description[f];

                    f++;

                }
            }
            //System.out.print("\n");
        }
        double sum=0;
        int g=0;
        double newDescription [] = new double[(numOfcellsH-1)*(numOfcellsW-1)*36];
        for (int i=0;i<numOfcellsW-1;i++){
            for (int j=0;j<numOfcellsH-1;j++){
                sum=0;
                for (int k=0;k<9;k++){
                    sum+=desc[i][j][k]*desc[i][j][k]+desc[i+1][j][k]*desc[i+1][j][k]+desc[i+1][j+1][k]*desc[i+1][j+1][k]+desc[i][j+1][k]*desc[i][j+1][k];
                }
                for (int k=0;k<9;k++){
                    newDescription[9*g+k]=Math.sqrt(desc[i][j][k]/(sum+0.001));
                    newDescription[9*g+9+k]=Math.sqrt(desc[i+1][j][k]/(sum+0.001));
                    newDescription[9*g+18+k]=Math.sqrt(desc[i][j+1][k]/(sum+0.001));
                    newDescription[9*g+27+k]=Math.sqrt(desc[i+1][j+1][k]/(sum+0.001));
                }
                g+=4;
            }
        }
        sum=0;
       for (int i=0;i<newDescription.length;i++){
            sum+=newDescription[i]*newDescription[i];

       }
       for (int i=0;i<newDescription.length;i++){
           newDescription[i]/=Math.sqrt(sum);
       }
        description=newDescription;
    }
    private void globalNormalization() {
        double maxGradMagnitude = 0.0;
        double minGradMagnitude = Double.MAX_VALUE;
        double magnitude = 0.0;
        for (int i = 0; i < description.length; i++) {

        magnitude =description[i];
        maxGradMagnitude = Math.max(magnitude, maxGradMagnitude);
        minGradMagnitude = Math.min(magnitude, minGradMagnitude);

    }
    double normalizationRate = 1.0 / (maxGradMagnitude - minGradMagnitude);
        for (int i = 0; i < description.length; i++) {
        //description[i]-=(maxGradMagnitude-minGradMagnitude)/2;
        description[i]*=normalizationRate;

    }
}
    public BufferedImage dxRepresentation(){
        int [] buffer = matrixToArray(dxMatrix);
        int[] rgb = new int[buffer.length];
        for (int i = 0; i < rgb.length; ++i) {
            int j=buffer[i]/2+128;
            rgb[i] = ((j << 16) | (j << 8) | j);
        }
        return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
                this.image.getHeight());
    }
    public BufferedImage dyRepresentation(){
        int [] buffer = matrixToArray(dyMatrix);
        int[] rgb = new int[buffer.length];
        for (int i = 0; i < rgb.length; ++i) {
            int j=buffer[i]/2+128;
            rgb[i] = ((j << 16) | (j << 8) | j);
        }
        return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
                this.image.getHeight());
    }
    private  void calculateHOG(){
        double dx;
        double dy;
        double magnitude;
        for (int i=0;i<image.getWidth();i++){
            for (int j=0;j<image.getHeight();j++){

                if (i==0 || j==0 || i==image.getWidth()-1||j==image.getHeight()-1){dx=0;dy=0;magnitude=0; }else {
                    dx = pixelLuminMatrix[i + 1][j] - pixelLuminMatrix[i - 1][j];
                    dy = pixelLuminMatrix[i][j + 1] - pixelLuminMatrix[i][j - 1];
                   // dy=0;
                    magnitude = Math.sqrt((dx * dx) + (dy * dy));
                    dxMatrix[i][j]=(int)Math.round(dx);
                    dyMatrix[i][j]=(int)Math.round(dy);


                }
                double orientation = Math.toDegrees(Math.atan(dy / (dx == 0 ? 0.000001 : dx)));
                gradients[i][j]=magnitude;
                vectorOfGradients[i][j]=orientation;

            }
        }
        int f=0;
        for (int i=0;i<=image.getWidth()-widthOfCell;i+=widthOfCell){
            for (int j=0;j<=image.getHeight()-heightOfCell;j+=heightOfCell){
                for (int x=0;x<widthOfCell;x++){
                    for (int y=0;y<heightOfCell;y++){

                        int angle =(int)(vectorOfGradients[i+x][j+y])+90;
                        int down = (int)(angle/20)%9;
                        int upper = (int)(Math.ceil((double)angle/20))%9;
                        int deltadown = Math.abs(angle-20*down);
                        int deltaupper = Math.abs(angle-20*upper);
                        double ratio = deltaupper/((deltadown+deltaupper)==0? 0.000001: (deltadown+deltaupper));
                        description[9*f+upper]+=gradients[x+i][j+y]*ratio;
                        description[9*f+down]+=gradients[x+i][j+y]*(1-ratio);
                        //System.out.print(description.length+" "+j+"\n");


                    }
                }
             //  System.out.print(" (");
                  // for (int k=0;k<9;k++){
                      // System.out.print(description[9*f+k]+" ");
                   //}
               // System.out.print(") ");
               // System.out.print(" "+9*f+"\n");

                f++;
            }

        }
       // System.out.print("\n");
    }
    public BufferedImage getHogRepresetation(){
        BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),1);
        Graphics graphics = img.getGraphics();
        graphics.setColor(Color.GREEN);
        double scale =1.5;
        int f=0;
        for (int i=0;i<=image.getWidth()-widthOfCell;i+=widthOfCell){
            for (int j=0;j<=image.getHeight()-heightOfCell;j+=heightOfCell){
                for (int k=0;k<9;k++){
                    int orientation = 20*k;
                    double magnitude = (double)Math.round(widthOfCell*scale*(description[9*f+k]));
                    int x1 = (int) (Math.sin(Math.toRadians(orientation)) * magnitude);
                    int y1 = (int) (Math.cos(Math.toRadians(orientation)) * magnitude);
                    int x2=-1*x1;
                    int y2=-1*y1;
                    x1 += widthOfCell / 2+i;
                    x2 += widthOfCell / 2+i;
                    y1 += heightOfCell / 2+j;
                    y2 += heightOfCell / 2+j;
                    if (magnitude>0) {
                        graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    }
                    // graphics.drawString(i+";"+j,i,j);
                }
                f++;
            }
        }
        return img;
    }
    private void createPixelLuminosityMatrix() {
        BufferedImage image = originalImage;
        this.pixelLuminMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        byte[] imagePixelBytes = (((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        int imageWidth = this.image.getWidth();
        boolean hasAlpha = image.getAlphaRaster() != null;
        int x = 0;
        int y = 0;
        double average =0;
        if (hasAlpha) {
            for (int i = 0; i < imagePixelBytes.length; i += 4) {
                this.pixelLuminMatrix[x][y] = desaturatePixel(imagePixelBytes[i + 1],
                        imagePixelBytes[i + 2], imagePixelBytes[i + 3]);
                average+=pixelLuminMatrix[x][y];
                x++;
                if (x == imageWidth) {
                    x = 0;
                    y++;
                }

            }
            average/=image.getHeight()*image.getWidth();
            int  buf []= new int[256];
            for (int i=0;i<256;i++){
                int temp  =(int) ((i-average)*1.1+average);
                if (temp<0){
                    temp=0;
                }
                if (temp>255){
                    temp=255;
                }
                buf[i]=temp;
            }
            x=0;
            y=0;
            for (int i = 0; i < imagePixelBytes.length; i += 4) {
                this.pixelLuminMatrix[x][y] = buf[(int)pixelLuminMatrix[x][y]];

                x++;
                if (x == imageWidth) {
                    x = 0;
                    y++;
                }

            }

        } else {
            for (int i = 0; i < imagePixelBytes.length; i += 3) {
                this.pixelLuminMatrix[x][y] = desaturatePixel(imagePixelBytes[i],
                        imagePixelBytes[i + 1], imagePixelBytes[i + 2]);
                average+=pixelLuminMatrix[x][y];
                x++;
                if (x == imageWidth) {
                    x = 0;
                    y++;
                }
            }
            average/=image.getHeight()*image.getWidth();
            int  buf []= new int[256];
            for (int i=0;i<256;i++){
                int temp  =(int)( ((i-average)*1.1+average));
                if (temp<0){
                    temp=0;
                }
                if (temp>255){
                    temp=255;
                }
                buf[i]=temp;
            }
            x=0;
            y=0;
            for (int i = 0; i < imagePixelBytes.length; i += 3) {
                this.pixelLuminMatrix[x][y] = buf[(int)pixelLuminMatrix[x][y]];

                x++;
                if (x == imageWidth) {
                    x = 0;
                    y++;
                }

            }
        }

    }
    private int[] matrixToArray(double[][] matrix) {
        int[] array = new int[matrix.length * matrix[0].length];
        int x = 0;
        int y = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) matrix[x][y];
            x++;
            if (x == matrix.length) {
                x = 0;
                y++;
            }
        }
        return array;
    }
    private double desaturatePixel(byte r, byte g, byte b) {
        int red = r & 0xff;
        int green = g & 0xff;
        int blue = b & 0xff;
        return (0.3 * red) + (0.586* green) + (0.113* blue);
    }
    public BufferedImage getLuminosityImage() {
        createPixelLuminosityMatrix();
        int[] buffer = this.matrixToArray(this.pixelLuminMatrix);
        int[] rgb = new int[buffer.length];
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
        }
        return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
                this.image.getHeight());
    }
}