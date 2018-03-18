import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Indicator extends JDialog {
    private JPanel contentPane;
    public JLabel label1;
    private JProgressBar progressBar1;

    public Indicator() {

        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        progressBar1.setVisible(true);
        label1.setVisible(true);
        resize();
    }

    public void done() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label1.setText("Done!");
                resize();
                setVisible(true);
            }
        });

        delay(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

    }

    public void status(String text) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar1.setVisible(false);
                label1.setVisible(true);
                label1.setText(text);
                resize();
                delay(4000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                setVisible(true);
            }
        });


    }

    public void progress(int filedone, int peopledone, int file, int people) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                label1.setText(filedone + "/" + file);
                progressBar1.setMaximum(people);
                progressBar1.setValue(peopledone);
                setVisible(true);
            }
        });
    }


    public Timer delay(int time, ActionListener actionListener) {
        Timer temptimer = new Timer(time, actionListener);
        temptimer.setRepeats(false);
        temptimer.start();
        return temptimer;
    }

    public void resize() {
        pack();
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);
    }

}
