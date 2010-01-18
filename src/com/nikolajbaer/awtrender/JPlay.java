package com.nikolajbaer.awtrender;

/* java */
import java.util.ArrayList;
import java.util.HashMap;


/* AWT */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;

/* local */
import com.nikolajbaer.game.Game;
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;

public class JPlay extends JFrame implements ActionListener { //implements Runnable{
    private Game m_game;
    private Timer m_timer;
    private BufferedImage m_backBuffer ;
    private Graphics2D m_backGraphics;
    private HashMap<String,AWTRenderObject> m_renderObjects;

    // pixels per meter
    public static final float PPM = 10.0f;
    private int m_gameWidth=400;
    private int m_gameHeight=600;

    public JPlay(String name){
        super(name);

        m_renderObjects=new HashMap<String,AWTRenderObject>();

        setSize(m_gameWidth+10,m_gameHeight+20);
        m_backBuffer = new BufferedImage( m_gameWidth,m_gameHeight, BufferedImage.TYPE_INT_RGB ) ;
        m_backGraphics = (Graphics2D)m_backBuffer.getGraphics();
        m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        m_game=new Game((int)(m_gameWidth/PPM),(int)(m_gameHeight/PPM)); // game is in meters
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
        AffineTransform t=m_backGraphics.getTransform();
        for(int i=0;i<renderables.size(); i++){
            Renderable r=renderables.get(i);
            AWTRenderObject ro=(AWTRenderObject)r.getRenderObject(); 
            if(ro==null){
                // TODO lookup render object via key
                // get render key  
                String k=r.getRenderKey();
                // if in m_renderObjects, then attach that,
                ro=m_renderObjects.get(k);
                // otherwise create a new one from the renderobject factory 
                // CONSIDER do i want a factory to be retrieved or an instance?
                if(ro==null){
                    // TODO add a renderobjectfactory abstract class
                    // that i can override for each render lib
                    System.out.println("Building a "+k+" Render Object");
                    ro=new PolygonRenderObject();
                    m_renderObjects.put(k,ro);
                }
                r.setRenderObject(ro);
            }
            float[] wt=r.getWorldTransform();
            ro.setGraphics(m_backGraphics);
            ro.setPixelRatio(PPM);
            ro.renderFromWorld(wt[0],wt[1],wt[2]);
            // TODO draw sprite k at location t
            // CONSIDER sprite lookup is expensive, should embed it in renderable as renderobject
            //  and call .renderAtTransform(), maybe setup graphics first or something..
            m_backGraphics.setTransform(new AffineTransform(t));
        }
        
        Graphics g = getGraphics();
        g.drawImage( m_backBuffer, 5,25, null );
        g.dispose();
    }    
}
