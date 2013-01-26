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
    double liftSpeed = .8;
    double tiltSpeed = .8;
     
    public Climber (int tiltID, int liftID) {
        try {
            tilt = new CANJaguar(tiltID);
            lift = new CANJaguar(liftID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();     
        }
    }
    
    // direction: true = forward, false = backward
    public void tilt (boolean direction) {
        try {
            if (direction) {
                tilt.setX(tiltSpeed);      
            } else if (!direction) {
                tilt.setX(-tiltSpeed);
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    public void stopTilt () {
        try {
            tilt.setX(0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    
    // direction: true = up, false = down
    public void lift (boolean direction) {
        try {
            if (direction){
                lift.setX(liftSpeed);
            } else if (!direction) {
                lift.setX(-liftSpeed);
            }  
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }            
    }
    public void stopLift () {
        try {
            lift.setX(0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
}
