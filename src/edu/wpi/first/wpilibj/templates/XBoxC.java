package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chris
 */
public class XBoxC extends Joystick {
    
    /**
     * controller accesses
     */
    public static XBoxC DRIVER, OPERATOR;
    /**
     *  switch people
     */
    public static void swapDriverOperator(){
        XBoxC temp=DRIVER;
        DRIVER=OPERATOR;
        OPERATOR=temp;
    }
    public XBoxC(int i){
        super(i);
    }
    
    /**
     * the DPad for this player
     */
    public DPad dPad=new DPad(this);
    /**
     * the control Sticks for this player
     */
    public ControlStick left=new ControlStick(9,this,1),
                        right=new ControlStick(10,this,4);
    /**
     * Buttons for this player
     */
    public Button   A = new Button(1,this),
                    B=new Button(2,this),
                    X=new Button(3,this),
                    Y=new Button(4,this),
                    LB=new Button(5,this),
                    RB=new Button(6,this),
                    START=new Button(7,this),
                    BACK=new Button(8,this);
    /**
     * the trigger axis id
     */
    public int TRIGGERS=3;
    /**
     * get trigger Axis Value
     */
    public double getTriggers(){
        return getRawAxis(TRIGGERS);
    }
    
    /**
     * the Button class
     */
    public class Button{
        XBoxC p;
        public int id;
        private boolean m_isPressed, m_wasPressed;
        
        public Button(int id2,XBoxC p2){
            p=p2;
            id=id2;
        }
        public boolean isPressed() {
            m_wasPressed = m_isPressed;
            m_isPressed = get();
            return m_isPressed;
        }
        public boolean wasPressed() {
            return m_wasPressed;
        }
        public boolean nowPressed() {
            if (isPressed() && !wasPressed()){
                System.out.print("nowPressed ");
                System.out.print(id);
                return true;
            }
            return false;
        }
        /**
         * @return returns true if this button is down
         */
        public boolean get(){
            return p.getRawButton(id);
        }
    }
    public class ControlStick extends Button{    
        public int xId;
        public int yId;
        public ControlStick(int b,XBoxC p2, int axis){
            super(b,p2);
            xId=axis;
            yId=axis+1;
        }
        public double getX(){
            return p.getRawAxis(xId);
        }
        
        public double getY(){
            return p.getRawAxis(yId);
        }
        //distance (in units) to the center
        public double getMagnitude(){
            if (xId==1){
                return p.getMagnitude();
            }
            double x=p.getRawAxis(xId);
            double y=p.getRawAxis(yId);
            return Math.sqrt(x*x+y*y);
        }
        //Left is 0, standard coordinate planes.
        public double getDirectionRadians(){
            if (xId==1){
                return p.getDirectionRadians();
            }
            return MathUtil.atan2(p.getRawAxis(yId), p.getRawAxis(xId));
        }
        public double getDirectionDegrees(){
            if (xId==1){
                return p.getDirectionDegrees();
            }
            return 180*MathUtil.atan2(p.getRawAxis(yId), p.getRawAxis(xId))/Math.PI;
        }
    }
    public class DPad{
        private XBoxC play;
        public DPad(XBoxC p){
            play=p;
        }
        public boolean isUp(){
            double x1=play.getThrottle();
            return x1>0 &&x1<180;
        }
        public boolean isDown(){
            double x1=play.getThrottle();
            return x1>180 &&x1<360;
        }
        public boolean isLeft(){
            double x1=play.getThrottle();
            return x1>90 &&x1<270;
        }
        public boolean isRight(){
            double x1=play.getThrottle();
            return x1>270 &&x1<90;
        }
        public double get(){
            return play.getThrottle();
        }
    }
}
