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
public class IronChef extends IterativeRobot {
    //function disable/enabling for testing purposes true=>enabled
    static boolean canDrive=false,canShoot=true,canSee=false,canSwap=false;
    RobotDrive drive;
    CANJaguar 
            frontLeftDrive, 
            rearLeftDrive,
            frontRightDrive,
            rearRightDrive;
               
    Shooter shooter;
    Vision camera;
    
    public static final int
            // TODO: get constants for motor id's
            REAR_LEFT_DRV_ID = 8,
            FRNT_LEFT_DRV_ID = 9,
            REAR_RIGHT_DRV_ID = 10,
            FRNT_RIGHT_DRV_ID = 11,
            
            SHOOTER_DRIVE_ID = 6, 
            LOADER_DRIVE_ID = 3, // not actually this
            LOADER_SWITCH_CHANNEL = 7, // not actually this
            DIGITAL_SIDECAR_MODULE=2,
            DRIVER_ID = 2,
            OPERATOR_ID = 1;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        XBoxC.DRIVER=new XBoxC(DRIVER_ID);
        XBoxC.OPERATOR=new XBoxC(OPERATOR_ID);
        if (canDrive){
            try {
                frontLeftDrive  = new CANJaguar(FRNT_LEFT_DRV_ID);
                rearLeftDrive   = new CANJaguar(REAR_LEFT_DRV_ID);
                frontRightDrive = new CANJaguar(FRNT_RIGHT_DRV_ID);
                rearRightDrive  = new CANJaguar(REAR_RIGHT_DRV_ID); 
            } catch (CANTimeoutException ex) {
                canDrive=false;
                ex.printStackTrace();
            }
            drive = new RobotDrive(frontLeftDrive, rearLeftDrive, frontRightDrive, rearRightDrive);
        }
        if (canShoot){
            shooter = new Shooter(SHOOTER_DRIVE_ID, LOADER_DRIVE_ID, LOADER_SWITCH_CHANNEL,DIGITAL_SIDECAR_MODULE);
        }
        if (canSee){
            camera = new Vision();    
        }
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
        if (canDrive){
            drive.arcadeDrive(XBoxC.DRIVER);
        }
        if (canShoot){
            shooter.periodic();
            if (XBoxC.OPERATOR.RB.nowPressed()) shooter.incrSpeed();  
            if (XBoxC.OPERATOR.LB.nowPressed()) shooter.decrSpeed();
            if (XBoxC.OPERATOR.B.nowPressed())  shooter.toggleShooter();
            if (XBoxC.OPERATOR.A.isPressed())  shooter.fire();
        }
        
        //TODO added for testing
        //if ((XBoxC.DRIVER.START.nowPressed()||XBoxC.OPERATOR.START.nowPressed())&&canSwap) XBoxC.swapDriverOperator();
        // add logic for each button here as needed
        
    }  
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {    
    }
}
