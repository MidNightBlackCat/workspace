package time.test;

import javax.swing.*;

/**
 * Created by limingda on 15/9/28.
 */
public class timeDemo {

    private JPanel en;

    public timeDemo()
    {

    }

    JFrame jFrame= new JFrame("SwingDemoMain");
    JPanel rootPane=new timeDemo().en;
    jFrame.setContentPane(rootPane);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jFrame.pack();
    jFrame.setSize(800, 600);
    jFrame.setLocationRelativeTo(rootPane);//居中
    jFrame.setVisible(true);
    }
}
