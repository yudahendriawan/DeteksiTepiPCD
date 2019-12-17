/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package countingobjectimage;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author LAB-ALJABAR-1
 */
public class BlackWhite {
    public static BufferedImage Mat2BufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1){
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
    
    public static void displayImage(Image img, String title){
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(title);
    }
    
    
    public static void main(String arg[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Load the image
        String filename = "C:\\Users\\pc\\Pictures\\koin2.jpg";
        Mat srcOriginal = Imgcodecs.imread(filename);
        if (srcOriginal.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        displayImage(Mat2BufferedImage(srcOriginal),"Original");
        
        // Show source image
//        HighGui.imshow("Source Image", srcOriginal);
        // Change the background from white to black, since that will help later to
        // extract
        // better results during the use of Distance Transform
        Mat src = srcOriginal.clone();
        byte[] srcData = new byte[(int) (src.total() * src.channels())];
        src.get(0, 0, srcData);
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                if (srcData[(i * src.cols() + j) * 3] == (byte) 255 && srcData[(i * src.cols() + j) * 3 + 1] == (byte) 255
                        && srcData[(i * src.cols() + j) * 3 + 2] == (byte) 255) {
                    srcData[(i * src.cols() + j) * 3] = 0;
                    srcData[(i * src.cols() + j) * 3 + 1] = 0;
                    srcData[(i * src.cols() + j) * 3 + 2] = 0;
                }
            }
        }
        src.put(0, 0, srcData);
        // Show output image
//        HighGui.imshow("Black Background Image", src);
        // Create a kernel that we will use to sharpen our image
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        // an approximation of second derivative, a quite strong kernel
        float[] kernelData = new float[(int) (kernel.total() * kernel.channels())];
        kernelData[0] = 1; kernelData[1] = 1; kernelData[2] = 1;
        kernelData[3] = 1; kernelData[4] = -8; kernelData[5] = 1;
        kernelData[6] = 1; kernelData[7] = 1; kernelData[8] = 1;
        kernel.put(0, 0, kernelData);
        // do the laplacian filtering as it is
        // well, we need to convert everything in something more deeper then CV_8U
        // because the kernel has some negative values,
        // and we can expect in general to have a Laplacian image with negative values
        // BUT a 8bits unsigned int (the one we are working with) can contain values
        // from 0 to 255
        // so the possible negative number will be truncated
        Mat imgLaplacian = new Mat();
        Imgproc.filter2D(src, imgLaplacian, CvType.CV_32F, kernel);
        Mat sharp = new Mat();
        src.convertTo(sharp, CvType.CV_32F);
        Mat imgResult = new Mat();
        Core.subtract(sharp, imgLaplacian, imgResult);
        // convert back to 8bits gray scale
        imgResult.convertTo(imgResult, CvType.CV_8UC3);
        imgLaplacian.convertTo(imgLaplacian, CvType.CV_8UC3);
        // imshow( "Laplace Filtered Image", imgLaplacian );
        //HighGui.imshow("New Sharped Image", imgResult);
        // Create binary image from source image
        Mat bw = new Mat();
        Imgproc.cvtColor(imgResult, bw, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(bw, bw, 40, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        displayImage(Mat2BufferedImage(bw),"Black White");
    
    
    }
}
