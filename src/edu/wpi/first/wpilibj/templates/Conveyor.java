/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author FIRSTMentor
 */
public class Conveyor {
    
    CANJaguar conveyor;
    Servo plate;
    private static double
            PLATE_ACTIVE_ANGLE = 10,
            PLATE_INACTIVE_ANGLE = -10;
    private Relay.Value conveyDirection;

    public Conveyor (int DIGITAL_SIDECAR_MODULE, int CONVEYOR_SPIKE_ID, int PLATE_ID) {
        if (IronChef.canConvey){
            try{
                conveyor = new CANJaguar(CONVEYOR_SPIKE_ID);
            }catch (CANTimeoutException e){
               e.printStackTrace();
            }
            plate = new Servo(PLATE_ID);
        }
    }
    
    public void convey () {
        try{
            if (XBoxC.OPERATOR.getTriggers()<-0.50){
                conveyor.setX(1.0);
                plate.setAngle(PLATE_ACTIVE_ANGLE);
            }else if (XBoxC.OPERATOR.getTriggers()>0.50){
                conveyor.setX(-1.0);
                plate.setAngle(PLATE_ACTIVE_ANGLE);
            }else{
               
                conveyor.setX(0.0);
                plate.setAngle(PLATE_INACTIVE_ANGLE);
            }
        }catch (CANTimeoutException ex){
            ex.printStackTrace();
        }
    }
    
    public void goForward() {
        setDirection(Relay.Value.kForward);
    }
    public void setDirection(Relay.Value direction){
        double d2=0.0;
        if (direction==Relay.Value.kForward){
            d2=1.0;
        } else if(direction==Relay.Value.kReverse){
            d2=-1.0;
        }
        setDirection(d2);
    }
    public void setDirection(double direction){
        if (IronChef.canConvey){
            try {
                conveyor.setX(direction);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
            
            if (direction==0.0){
                plate.setAngle(PLATE_INACTIVE_ANGLE);
            }else{
                plate.setAngle(PLATE_ACTIVE_ANGLE);
            }
        }
    }
}