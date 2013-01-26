/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class MrRoboto extends IterativeRobot {
    RobotDrive drive;
    XBoxC controller;
    CANJaguar 
            frontLeftDrive, 
            rearLeftDrive,
            frontRightDrive,
            rearRightDrive,
            conveyor;
               
    Shooter shooter;
    Vision camera;
    Climber climber;
    
    public static final int
            // TODO: constants with a val of -1 need a new val
            REAR_LEFT_DRV_ID = -1,
            FRNT_LEFT_DRV_ID = -1,
            REAR_RIGHT_DRV_ID = -1,
            FRNT_RIGHT_DRV_ID = -1,
            
            SHOOTER_DRIVE_ID = 1, 
            LOADER_DRIVE_ID = -1, 
            CONVEYOR_ID = -1,
            LOADER_SWITCH_CHANNEL = -1, 
            DRIVER_ID = 1,
            OPERATOR_ID = 2,
            CLIMB_TILT_ID = -1,
            CLIMB_LIFT_ID = -1;

    
    public static final boolean
            UP = true,
            DOWN = false,
            FORWARD = true,
            BACKWARD = false;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        XBoxC.DRIVER=new XBoxC(DRIVER_ID);
        XBoxC.OPERATOR=new XBoxC(OPERATOR_ID);
        try {
            frontLeftDrive  = new CANJaguar(FRNT_LEFT_DRV_ID);
            rearLeftDrive   = new CANJaguar(REAR_LEFT_DRV_ID);
            frontRightDrive = new CANJaguar(FRNT_RIGHT_DRV_ID);
            rearRightDrive  = new CANJaguar(REAR_RIGHT_DRV_ID);
            conveyor = new CANJaguar(CONVEYOR_ID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        drive = new RobotDrive(frontLeftDrive, rearLeftDrive, frontRightDrive, rearRightDrive);
        shooter = new Shooter(SHOOTER_DRIVE_ID, LOADER_DRIVE_ID, LOADER_SWITCH_CHANNEL);
        camera = new Vision();  
        climber = new Climber(CLIMB_TILT_ID, CLIMB_LIFT_ID);
    }
 
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        drive.arcadeDrive(XBoxC.DRIVER);
        shooter.periodic();
               
        if (XBoxC.OPERATOR.RB.nowPressed()) shooter.incrSpeed();  
        if (XBoxC.OPERATOR.LB.nowPressed()) shooter.decrSpeed();
        if (XBoxC.OPERATOR.B.nowPressed())  shooter.toggleShooter();
        if (XBoxC.OPERATOR.A.nowPressed())  shooter.fire();
        
        if (XBoxC.OPERATOR.dPad.isUp())    climber.lift(UP);
            else climber.stopLift(); 
        if (XBoxC.OPERATOR.dPad.isDown())  climber.lift(DOWN);
            else climber.stopLift();
        if (XBoxC.OPERATOR.dPad.isRight()) climber.tilt(FORWARD);
            else climber.stopTilt();
        if (XBoxC.OPERATOR.dPad.isLeft())  climber.tilt(BACKWARD);
            else climber.stopTilt();
        // add logic for each button here as needed
        
        // Conveyor code here
        if (XBoxC.OPERATOR.getRawAxis(3) > 0.5){
            try {
                conveyor.setX(1.0);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        } else if(XBoxC.OPERATOR.getRawAxis(3) < -0.5){
            try {
                conveyor.setX(-1.0);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }
        else{
            try {
                conveyor.setX(0.0);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }
        
    }  
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {    
    }
}
