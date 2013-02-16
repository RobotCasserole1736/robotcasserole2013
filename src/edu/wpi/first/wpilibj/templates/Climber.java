/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Servo;

                                  
/**
 *  
 * @author Kristen Dunne
 */
public class Climber {
    
     Servo left, right;
     
     private static double
        preclimb_angle = 0,
        climb_angle = 180;           
     
    public Climber(int left_id, int right_id)
    {
        if (IronChef.canClimb){
            left = new Servo(left_id);
            right = new Servo(right_id);      
        }
    }
    
    public void periodic(){
        if (IronChef.canClimb){
            if (XBoxC.DRIVER.getRawButton(5)){
                //left && right.setAngle(climb_angle);
            }
            else {
                //left && right.setAngle(preclimb_angle);        
            }
        }
    }
}
     
   
