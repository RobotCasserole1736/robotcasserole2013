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
    CANJaguar leftDrive, rightDrive, conveyor;
    Shooter shooter;
    
    public static final int   
            LEFT_DRIVE_ID = 2,
            RIGHT_DRIVE_ID = 7,
            SHOOTER_DRIVE_ID = 1, 
            LOADER_DRIVE_ID = -1, // not actually this
            CONVEYOR_ID = -1,
            LOADER_SWITCH_CHANNEL = -1, // not actually this
            DRIVER_ID = 1,
            OPERATOR_ID = 2;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        XBoxC.DRIVER=new XBoxC(DRIVER_ID);
        XBoxC.OPERATOR=new XBoxC(OPERATOR_ID);
        try {
            leftDrive = new CANJaguar(LEFT_DRIVE_ID);
            rightDrive = new CANJaguar(RIGHT_DRIVE_ID);
            conveyor = new CANJaguar(CONVEYOR_ID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        drive = new RobotDrive(leftDrive, rightDrive);
        shooter = new Shooter(SHOOTER_DRIVE_ID, LOADER_DRIVE_ID, LOADER_SWITCH_CHANNEL);
        
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
               
        if (XBoxC.OPERATOR.RB.nowPressed()) {
            shooter.incrSpeed();
        }
        if (XBoxC.OPERATOR.LB.nowPressed()) {
            shooter.decrSpeed();
        }
        if (XBoxC.OPERATOR.B.nowPressed()) {
            shooter.toggleShooter();
        }
        if (XBoxC.OPERATOR.A.nowPressed()) {
           shooter.fire(); 
        }
        // add logic for each button here as needed
        if (XBoxC.OPERATOR.getRawAxis(3)>.5){
            try {
                conveyor.setX(1.0);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }
        else if(XBoxC.OPERATOR.getRawAxis(3)<-.5){
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
