package com.nikolajbaer.awtrender;

/* java */
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* jbox2d */
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/* AWT */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;

/* local */
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.game.*;
import com.nikolajbaer.game.players.*;
import com.nikolajbaer.game.weapons.*;

public class JPlay extends JFrame implements ActionListener { //implements Runnable{
    private Game m_game;
    private Timer m_timer;
    private BufferedImage m_backBuffer ;
    private Graphics2D m_backGraphics;
    private HashMap<String,AWTRenderObject> m_renderObjects;
    private HashMap<Integer,Boolean> m_keyMap;

    // pixels per meter
    public static final float PPM = 10.0f;
    private int m_gameWidth=600;
    private int m_gameHeight=800;
    private int m_screenWidth=400;
    private int m_screenHeight=600;
    private double m_zoom=1.0;
    private double m_zoom2=0.5;
    private float m_playerAngle;
    private Vec2 m_playerPos;

    public JPlay(String name){
        super(name);

        m_renderObjects=new HashMap<String,AWTRenderObject>();

        setSize(m_screenWidth,m_screenHeight);//m_gameWidth+10,m_gameHeight+20);
        m_backBuffer = new BufferedImage( m_gameWidth,m_gameHeight, BufferedImage.TYPE_INT_RGB ) ;
        m_backGraphics = (Graphics2D)m_backBuffer.getGraphics();
        //m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int gwidth=(int)(m_gameWidth/PPM);
        int gheight=(int)(m_gameHeight/PPM);
        m_game=new Game(gwidth,gheight); // game is in meters
        Game.game=m_game;
        m_timer = new Timer(1000/40,this);
        m_timer.setInitialDelay(500);
        m_timer.start(); 
       
        m_keyMap=new HashMap<Integer,Boolean>(); 

        addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e){
                m_keyMap.put(e.getKeyCode(),true);
            }

            public void keyReleased(KeyEvent e){
                m_keyMap.put(e.getKeyCode(),false);
            }
            public void keyTyped(KeyEvent e){}
        });
    
        addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e){
                //m_game.getPlayer().rotateToPointAt(toWorld(e.getX()),toWorld(e.getY()));
            }
            public void mouseExited(MouseEvent e){ }
            public void mouseReleased(MouseEvent e){ }
            public void mouseEntered(MouseEvent e){ }
            public void mousePressed(MouseEvent e){ }
        });

        // Add Players
        float[] verts={-2.4f,3.2f,-2.4f,-3.2f,2.4f,-3.2f,2.4f,3.2f};

        // Spread the players out in a ring 
        Vec2 mid=new Vec2(gwidth/2.0f,gheight/2.0f);
        Vec2 offset=new Vec2(gwidth/2*0.75f,0);
        int np=2; //6;
        for(int i=0;i<np;i++){
            Body b=m_game.createRect(0.2f,-2.4f,-3.2f,4.8f,6.4f);
            //Body b=m_game.createPolygon(1.0f, verts );

            double a=(2*Math.PI)/np;
            Vec2 rv=Util.rotate( offset, (float)(i*a) );
            float ra=(float)(i*a < Math.PI ? i*a+Math.PI : i*a-Math.PI);
            b.setXForm( mid.add(rv) ,(float)(ra+Math.PI/2)); // i guess it goes from 0,1 not 1,0
            PlayerObject po=new PlayerObject(b,verts);
            // arm the tank
            po.addWeapon(0,new TankCannon());
            po.addWeapon(1,new Blaster());
            po.selectWeapon(0);
            // and add ai or player
            if(i==0){ 
                m_game.addPlayer(new LivePlayer(po),true);
            }else{
                m_game.addPlayer(new LambPlayer(po));
            }
        }
        m_game.addObstacle(3,3);
    }

    private Boolean getKey(int k){
        Boolean b= m_keyMap.get(k);
        return b != null && b;
    }

    private void processKeyActions(){
        // TODO be smarter about end-game state
        if(m_game.getPlayer().getBody()==null){ return; }

        if(getKey(KeyEvent.VK_SPACE)){
            m_game.getPlayer().triggerOn();
        }else{
            m_game.getPlayer().triggerOff();
        }

        // zoomer hotkey
        if(getKey(KeyEvent.VK_SHIFT)){
            if(m_zoom>m_zoom2){ 
                m_zoom-=0.1;
            }else{
                m_zoom=m_zoom2;
            }
        }else{
            if(m_zoom<1.0){ 
                m_zoom+=0.1;
            }else{
                m_zoom=1.0;
            }
        }

        if(getKey(KeyEvent.VK_LEFT)){
            m_game.getPlayer().left();
        }else if(getKey(KeyEvent.VK_RIGHT)){
            m_game.getPlayer().right();
        }
        
        if(getKey(KeyEvent.VK_UP)){
            m_game.getPlayer().forward();
        }else if(getKey(KeyEvent.VK_DOWN)){
            m_game.getPlayer().reverse();
        }else{
            m_game.getPlayer().halt();
        } 


        if(getKey(KeyEvent.VK_1)){
            m_game.getPlayer().selectWeapon(0);
        }else if(getKey(KeyEvent.VK_2)){
            m_game.getPlayer().selectWeapon(1);
        }else if(getKey(KeyEvent.VK_3)){
            m_game.getPlayer().selectWeapon(2);
        }



        // Read from accelerometer
        // CONSIDER would need to adjust for starting position on phone..
        // NOTE this stopped working o nmy laptop due to grahnix
        /*
        float[] accel=readAccel();
        float x=accel[0];
        float y=accel[1]; 
        System.out.println("accel "+x+","+y);
        Vec2 i=new Vec2(x,y);
        if(x > 0.5){
            m_game.getPlayer().left();
        }else if(x < -0.5){
            m_game.getPlayer().right();
        }
        if(y > 0.5){
            m_game.getPlayer().forward();
        }else if(y < -0.5){
            m_game.getPlayer().reverse();
        }
        Vec2 d=m_game.getPlayer().getDir();
        float a=Vec2.cross(i,d);
        //System.out.println("applying torque "+a);
        if(i.length()>0.3){ 
            if(Vec2.dot(i,d) < 0){
                //m_game.getPlayer().reverse();
            }else{
                //m_game.getPlayer().setThrottle(i.length());
            }
        }else{ 
            m_game.getPlayer().halt();
        }
        //m_game.getPlayer().forward();

        Body b=m_game.getPlayer().getBody();
        b.wakeUp();
        //b.applyTorque(a*-400);
        //b.applyTorque(a*10);
        */
    }

    // Use this to help develop phone-like control system
    private float[] readAccel(){
        // never understood why there can't be some simpler defaults for java..
        File file = new File("/sys/devices/platform/hdaps/position");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        float x=0;
        float y=0;
        try {
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            String l=dis.readLine();
            l=l.replace("(","").replace(")","");
            String[] tokens = l.split(",");
            // 0 - flat is -500, goes from ~-600 left to -300 right down
            // 1 - flat is 500, goes from 600 tipped forward to 300 tipped back
            x=(Float.parseFloat(tokens[0])+500)/150.0f;
            y=(Float.parseFloat(tokens[1])-500)/150.0f;
            // Now x is left/right, with left full as -150, right full as 150
            // y is front/back with tipped forward full as -150, back full is 150
            fis.close();
            dis.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return new float[]{x,y};
    }

    public void actionPerformed(ActionEvent e) {
        processKeyActions();
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
        m_backGraphics.fillRect( 0,0, m_screenWidth,m_screenHeight) ;
        // render game field
        //m_game.draw(m_backGraphics);
        //m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform t0=m_backGraphics.getTransform();

        if(m_game.getPlayer().getBody()!=null){
            Body pb=m_game.getPlayer().getBody();
            m_playerPos=toScreen(pb.getPosition());
            m_playerAngle=pb.getAngle();
        }
        
        // Translate to user position
        // and then offset to center of screen
        
        m_backGraphics.translate(m_screenWidth/2,m_screenHeight/2);

        /*if(m_zoom!=1.0){
            m_backGraphics.scale(m_zoom,m_zoom);
        } */
        m_backGraphics.scale(0.5,0.5);

        m_backGraphics.rotate(-m_playerAngle);
        m_backGraphics.translate(-m_playerPos.x,-m_playerPos.y);

        m_backGraphics.setColor( Color.gray ) ;
        m_backGraphics.drawRect( 0,0, m_gameWidth,m_gameHeight) ;


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
                    //ro=new PolygonRenderObject();
                    if(k=="obstacle"){
                        int gs=(int)toScreen(m_game.OBSTACLE_GRID_SIZE);
                        ro=new RectRenderObject(gs,gs);
                    }else{
                        ro=new PNGRenderObject("res/drawable/"+k+".png");
                    }
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
      
        //reset to original trnasform 
        m_backGraphics.setTransform(new AffineTransform(t0)); 
        Graphics g = getGraphics();
        g.drawImage( m_backBuffer, 5,25, null );
        g.dispose();
    }    

    /* Convert world/screen */
    private float[] toWorld(float[] n){
        if(n==null){ return null; }
        float[] r=new float[n.length];
        for(int i=0;i<n.length;i++){
            r[i]=toWorld(n[i]);
        }
        return r;
    }

    private Vec2 toWorld(Vec2 v){
        return new Vec2(toWorld(v.x),toWorld(v.y));
    }

    private float[] toScreen(float[] n){
        if(n==null){ return null; }
        float[] r=new float[n.length];
        for(int i=0;i<n.length;i++){
            r[i]=toWorld(n[i]);
        }
        return r;
    }

    private float toWorld(int n){ return toWorld((float)n); }

    private float toWorld(float n){
        return n/PPM;
    }

    private float toScreen(float n){
        return n*PPM;
    }

    private Vec2 toScreen(Vec2 v){
        return new Vec2(toScreen(v.x),toScreen(v.y));
    }

}
