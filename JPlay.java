import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;

public class JPlay extends JFrame implements ActionListener { //implements Runnable{
    private Game m_game;
    private Timer m_timer;
    private int m_gameWidth=400;
    private int m_gameHeight=600;
    private BufferedImage m_backBuffer ;
    private Graphics2D m_backGraphics;

    public JPlay(String name){
        super(name);
        setSize(m_gameWidth+10,m_gameHeight+10);
        m_backBuffer = new BufferedImage( m_gameWidth,m_gameHeight, BufferedImage.TYPE_INT_RGB ) ;
        m_backGraphics = (Graphics2D)m_backBuffer.getGraphics();
        m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        m_game=new Game(m_gameWidth,m_gameHeight);
        m_timer = new Timer(1000/60,this);
        m_timer.setInitialDelay(500);
        m_timer.start(); 
    }

    public void actionPerformed(ActionEvent e) {
        m_game.tick();
        render();
    }

    public static void main(String[] args) {
        JPlay f = new JPlay("JPlay");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public void render( ) {
        m_backGraphics.setColor( Color.gray ) ;
        m_backGraphics.fillRect( 0,0, m_gameWidth,m_gameHeight) ;
        m_game.draw(m_backGraphics);
        Graphics g = getGraphics();
        g.drawImage( m_backBuffer, 5,5, null );
        g.dispose();
    }    
}
