/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

                                  
/**
 *  
 * @author Kristen Dunne
 */
public class Climber {
public double StartTime;
public Relay motor;
public boolean isClimbing=false;
    public Climber(int motor_id)
    {if (IronChef.canClimb){
        motor=new Relay(motor_id);
        StartTime = -1;
                           }
    }
    
    public void periodic(boolean climb){
        if (climb){
            setClimbing(true);
        }else{
            setClimbing(false);
        }
    }
    public void setClimbing(boolean shouldClimb){
        if (shouldClimb) { 
            motor.set(Relay.Value.kForward);
            StartTime = Timer.getFPGATimestamp();
            isClimbing=true;
        }else  {
            if (Timer.getFPGATimestamp()-StartTime > 0.25) {
            motor.set(Relay.Value.kOff);
            isClimbing=false;  
            }
        }
        isClimbing=shouldClimb;
    }
}