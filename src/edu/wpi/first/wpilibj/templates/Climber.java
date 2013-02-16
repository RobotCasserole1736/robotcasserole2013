/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

                                  
/**
 *  
 * @author Kristen Dunne
 */
public class Climber {
public CANJaguar motor;
public boolean isClimbing=false;
public Timer timer;
    public Climber(int motor_id)
    {
        if (IronChef.canClimb){
            try{
                motor=new CANJaguar(motor_id);
                timer=new Timer();
            }catch (CANTimeoutException ex){
                ex.printStackTrace();
                IronChef.canClimb=false;
            }
        }
    }
    
    public void periodic(){
        if (XBoxC.DRIVER.right.nowPressed()){
            setClimbing(true);
        }else if (timer.get()>0.1&& isClimbing){
            setClimbing(false);
        }
    }
    public void setClimbing(boolean shouldClimb){
        if (IronChef.canClimb){
            try {
                if (shouldClimb) { 
                    motor.setX(1.0);
                    timer.reset();
                    isClimbing=true;
                }else{
                    motor.setX(0.0);
                    isClimbing=false;  
                }
                isClimbing=shouldClimb;
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }
    }
}