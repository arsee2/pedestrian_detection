import com.dgimenes.jhog.util.ImageProcessingUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by arsee on 02.06.2017.
 */
public class Hog {
    public static int widthOfCell = 8;
    public static int heightOfCell = 8;
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
        description = new double[(im.getWidth()/widthOfCell)*(im.getHeight()/heightOfCell)*9+10];
        createPixelLuminosityMatrix();
        calculateHOG();
        //globalNormalization();
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
                    magnitude = Math.sqrt((dx * dx) + (dy * dy));
                    dxMatrix[i][j]=(int)Math.round(dx);
                    dyMatrix[i][j]=(int)Math.round(dy);


                }
                for (int k=0;k<9;k++){
                    //System.out.print(i+" "+j+" "+magnitude+" "+"\n");
                }
                double orientation = Math.toDegrees(Math.atan(dy / (dx == 0 ? 0.000001 : dx)));
                gradients[i][j]=magnitude;
                vectorOfGradients[i][j]=orientation;

            }
        }
        int f=0;
        for (int i=0;i<image.getWidth()-widthOfCell;i+=widthOfCell){
            for (int j=0;j<image.getHeight()-heightOfCell;j+=heightOfCell){
                for (int x=0;x<widthOfCell;x++){
                    for (int y=0;y<heightOfCell;y++){

                        int angle =(int)(vectorOfGradients[i+x][j+y])+90;
                        int down = (int)(angle/20);
                        int upper = (int)(Math.ceil((double)angle/20));
                        int deltadown = Math.abs(angle-20*down);
                        int deltaupper = Math.abs(angle-20*upper);
                        double ratio = deltaupper/((deltadown+deltaupper)==0? 0.000001: (deltadown+deltaupper));
                        description[9*f+upper]+=gradients[x+i][j+y]*ratio;
                        description[9*f+down]+=gradients[x+i][j+y]*(1-ratio);
                        //System.out.print(description.length+" "+j+"\n");


                    }
                }

                f++;
            }
        }
    }
    public BufferedImage getHogRepresetation(){
        BufferedImage img = new BufferedImage(image.getWidth(),image.getHeight(),1);
        Graphics graphics = img.getGraphics();
        graphics.setColor(Color.GREEN);
        double scale =1.5;
        int f=0;
        for (int i=0;i<image.getWidth()-widthOfCell;i+=widthOfCell){
            for (int j=0;j<image.getHeight()-heightOfCell;j+=heightOfCell){
                for (int k=0;k<9;k++){
                    int orientation = 20*k;
                    double magnitude = (double)widthOfCell*scale*description[9*f+k]/800;
                    int   x1 = (int) (Math.sin(Math.toRadians(orientation)) * magnitude);
                    int  y1 = (int) (Math.cos(Math.toRadians(orientation)) * magnitude);
                    int x2=-1*x1;
                    int y2=-1*y1;
                    x1 += widthOfCell / 2+i;
                    x2 += widthOfCell / 2+i;
                    y1 += widthOfCell / 2+j;
                    y2 += widthOfCell / 2+j;
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
        if (hasAlpha) {
            for (int i = 0; i < imagePixelBytes.length; i += 4) {
                this.pixelLuminMatrix[x][y] = desaturatePixel(imagePixelBytes[i + 1],
                        imagePixelBytes[i + 2], imagePixelBytes[i + 3]);
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
        return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
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
