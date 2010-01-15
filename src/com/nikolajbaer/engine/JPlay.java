package com.nikolajbaer.engine;

/* java */
import java.util.ArrayList;

/* AWT */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;

/* local */
import com.nikolajbaer.game.Game;
import com.nikolajbaer.render.Renderable;

public class JPlay extends JFrame implements ActionListener { //implements Runnable{
    private Game m_game;
    private Timer m_timer;
    private int m_gameWidth=400;
    private int m_gameHeight=600;
    private BufferedImage m_backBuffer ;
    private Graphics2D m_backGraphics;

    public JPlay(String name){
        super(name);
        setSize(m_gameWidth+10,m_gameHeight+20);
        m_backBuffer = new BufferedImage( m_gameWidth,m_gameHeight, BufferedImage.TYPE_INT_RGB ) ;
        m_backGraphics = (Graphics2D)m_backBuffer.getGraphics();
        m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        m_game=new Game(m_gameWidth,m_gameHeight);
        Game.game=m_game;
        m_timer = new Timer(1000/40,this);
        m_timer.setInitialDelay(500);
        m_timer.start(); 
        
        addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e){
                // TODO parse input commands 
                switch(e.getKeyCode()){
                    case KeyEvent.VK_SPACE:
                        m_game.getPlayer().triggerOn();
                        break;
                    case KeyEvent.VK_UP:
                        m_game.getPlayer().forward();
                        break;
                    case KeyEvent.VK_DOWN:
                        m_game.getPlayer().reverse();
                        break;
                    case KeyEvent.VK_LEFT:
                        m_game.getPlayer().left();
                        break;
                    case KeyEvent.VK_RIGHT:
                        m_game.getPlayer().right();
                        break;
                }
            }

            public void keyReleased(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_SPACE:
                        m_game.getPlayer().triggerOff();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        m_game.getPlayer().halt();
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        m_game.getPlayer().stopRotate();
                        break;
                }

            }
            public void keyTyped(KeyEvent e){}
        });
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
        // render to back buffer
        m_backGraphics.setColor( Color.black ) ;
        m_backGraphics.fillRect( 0,0, m_gameWidth,m_gameHeight) ;
        // render game field
        //m_game.draw(m_backGraphics);
        ArrayList<Renderable> renderables=m_game.getRenderables();
        for(int i=0;i<renderables.size(); i++){
            Renderable r=renderables.get(i);
            String k=r.getRenderKey(); 
            float[] t=r.getWorldTransform();
            // TODO draw sprite k at location t
            // CONSIDER sprite lookup is expensive, should embed it in renderable as renderobject
            //  and call .renderAtTransform(), maybe setup graphics first or something..
        }
        
        Graphics g = getGraphics();
        g.drawImage( m_backBuffer, 5,25, null );
        g.dispose();
    }    
}
