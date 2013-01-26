/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author Kristen Dunne
 */
public class Climber {
    
    CANJaguar tilt, lift;
    double lift_speed = .8;
    double tilt_speed = .8;
     
    public Climber(int tilt_id, int lift_id)
    {
        try {
            tilt = new CANJaguar(tilt_id);
            lift = new CANJaguar(lift_id);
        } catch (CANTimeoutException ex) {
            IronChef.canClimb=false;
            ex.printStackTrace();     
        }
    }
    public void periodic(){
        tilt(false/*unknown value and use*/);
        lift(false/*unknown value*/);
    }
    public void tilt(boolean activated)
    {
        try {
            if (XBoxC.DRIVER.X.isPressed()){
                tilt.setX(tilt_speed);
            } 
            else if (XBoxC.DRIVER.Y.isPressed()){
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
            if (XBoxC.DRIVER.LB.isPressed()){
                lift.setX(lift_speed);
            } 
            else if (XBoxC.DRIVER.RB.isPressed()){
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
