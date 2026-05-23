package system;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        JFrame window = new JFrame("PetMoCo");

        Dimension maxSize = new Dimension(1080, 640);
        window.setSize(maxSize);
        window.setResizable(false);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}
