import java.awt.*;
import javax.swing.*;

public class JPlay extends JFrame {
    private Game m_game;

    public JPlay(String name){
        super(name);
        setSize(400,600);
        m_game=new Game(400,600,10.0f);
    }

    public static void main(String[] args) {
        JPlay f = new JPlay("JPlay");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public void paint( Graphics g ) {
        m_game.tick();
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        m_game.draw(g2);
    }    
}
