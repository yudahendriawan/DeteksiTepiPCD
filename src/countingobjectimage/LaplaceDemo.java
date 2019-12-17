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
import org.opencv.core.*;

import org.opencv.highgui.HighGui;

import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.imgproc.Imgproc;

class LaplaceDemoRun {

    public void run(String[] args) {

        //! [variables]
        // Declare the variables we are going to use
        Mat src, src_gray = new Mat(), dst = new Mat();

        int kernel_size = 3;

        int scale = 1;

        int delta = 0;

        int ddepth = CvType.CV_16S;

        String window_name = "Laplace Demo";

        //! [variables]
        //! [load]
        String imageName = ((args.length > 0) ? args[0] : "C:\\Users\\pc\\Pictures\\84631.jpg");

        src = Imgcodecs.imread(imageName, Imgcodecs.IMREAD_COLOR); // Load an image
        
        // Check if image is loaded fine
        if (src.empty()) {

            System.out.println("Error opening image");

            System.out.println("Program Arguments: [image_name -- default ../data/lena.jpg] \n");

            System.exit(-1);

        }

        //! [load]
        //! [reduce_noise]
        // Reduce noise by blurring with a Gaussian filter ( kernel size = 3 )
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

        //! [reduce_noise]
        //! [convert_to_gray]
        // Convert the image to grayscale
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);

        //! [convert_to_gray]
        /// Apply Laplace function
        Mat abs_dst = new Mat();

        //! [laplacian]
        Imgproc.Laplacian(src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT);

        //! [laplacian]
        //! [convert]
        // converting back to CV_8U
        Core.convertScaleAbs(dst, abs_dst);
        Image img = Mat2BufferedImage(abs_dst);
        displayImage(img, "ASU");

        //! [convert]
        //! [display]
//        HighGui.imshow(window_name, abs_dst);
//
//        HighGui.waitKey(0);

        //! [display]
        //System.exit(0);

    }

    public static void displayImage(Image img, String title) {
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(null) + 50, img.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(title);
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

}

public class LaplaceDemo {

    public static void main(String[] args) {

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new LaplaceDemoRun().run(args);

    }

}
