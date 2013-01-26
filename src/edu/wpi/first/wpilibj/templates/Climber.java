/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author Kristen Dunne
 */
public class Climber {
    
    CANJaguar tilt, lift;
    XBoxC joy;
    double lift_speed = .8;
    double tilt_speed = .8;
     
    public Climber(int tilt_id, int lift_id, XBoxC joy)
    {
        try {
            tilt = new CANJaguar(tilt_id);
            lift = new CANJaguar(lift_id);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();     
        }
        this.joy = joy;
    }
    
    public void tilt(boolean activated)
    {
        try {
            if (joy.getRawButton(3)){
                tilt.setX(tilt_speed);
            } 
            else if (joy.getRawButton(4)){
                tilt.setX(-tilt_speed);
            }
            else {
                tilt.setX(0);
            }
        }
       catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
              
    }
     public void lift(boolean activated)
    {
        try {
            if (joy.getRawButton(5)){
                lift.setX(lift_speed);
            } 
            else if (joy.getRawButton(6)){
                lift.setX(-lift_speed);
            }
            else {
                lift.setX(0);
            }
        }
       catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
              
    }
    
    
}
