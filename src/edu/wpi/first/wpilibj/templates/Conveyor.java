/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;

/**
 *
 * @author FIRSTMentor
 */
public class Conveyor {
    
    Relay conveyor;
    Servo plate;
    private static double
            PLATE_ACTIVE_ANGLE = 10,
            PLATE_INACTIVE_ANGLE = -10;

    public Conveyor (int DIGITAL_SIDECAR_MODULE, int CONVEYOR_SPIKE_ID, int PLATE_ID) {
        conveyor = new Relay(DIGITAL_SIDECAR_MODULE, CONVEYOR_SPIKE_ID);
        plate = new Servo(PLATE_ID);
    }
    
    public void convey () {
        if (XBoxC.OPERATOR.getTriggers()<-0.50){
            conveyor.set(Relay.Value.kForward);
            plate.setAngle(PLATE_ACTIVE_ANGLE);
        }else if (XBoxC.OPERATOR.getTriggers()>0.50){
             conveyor.set(Relay.Value.kReverse);
             plate.setAngle(PLATE_ACTIVE_ANGLE);
        }else{
            conveyor.set(Relay.Value.kOff);
            plate.setAngle(PLATE_INACTIVE_ANGLE);
        }
    }
    
    public void goForward() {
        conveyor.set(Relay.Value.kForward);
    }
}
