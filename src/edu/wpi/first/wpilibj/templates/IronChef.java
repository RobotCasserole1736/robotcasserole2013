/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.Watchdog;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class IronChef extends IterativeRobot {
    //function disable/enabling for testing purposes true=>enabled
    static boolean 
            canDrive=true,
            canShoot=true,
            canSee=false,
            canConvey=true,
            canSwap=true,
            canClimb=true;
    RobotDrive drive;
    CANJaguar 
            frontLeftDrive, 
            rearLeftDrive,
            frontRightDrive,
            rearRightDrive;
    boolean driveInverted = false;
    Conveyor conveyorRelay;
    Shooter shooter;
    Vision camera;
    Climber climber;
    Solenoid ovenFlames, cameraLight;
    //Loader motor may or may not be a pmw or a can
    public static final boolean LOADER_IS_CAN=false;
    public static final int
            //Drive motors
            REAR_LEFT_DRV_ID = 3,
            FRNT_LEFT_DRV_ID = 10,
            REAR_RIGHT_DRV_ID = 6,
            FRNT_RIGHT_DRV_ID = 9,
            //Conveyor and shooter motors and switches
            CONVEYOR_SPIKE_ID=8,
            PLATE_ID=7,           //PLEASE CHANGE THIS VALUE WHEN YOU FIND OUT WHAT THE ID ACTUALLY IS
            SHOOTER_DRIVE_ID = 11, 
            LOADER_DRIVE_ID = 8,
            LOADER_SWITCH_CHANNEL=1,
            WINCH_ID = 2,
            //Climber motor ids
            CLIMBER_ID=1,
            //sidecar module id (DOUBLE CHECK THIS)
            DIGITAL_SIDECAR_MODULE=1,
            //Joysitick ids
            DRIVER_ID = 1,
            OPERATOR_ID = 2;
            
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
                drive = new RobotDrive(frontLeftDrive, rearLeftDrive, frontRightDrive, rearRightDrive);
                drive.setSensitivity(0.5);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
            ovenFlames = new Solenoid(1);
            cameraLight = new Solenoid(2);
            ovenFlames.set(true);
            cameraLight.set(true);
        }
        if (canShoot){
            shooter = new Shooter(SHOOTER_DRIVE_ID, LOADER_DRIVE_ID, LOADER_SWITCH_CHANNEL,DIGITAL_SIDECAR_MODULE,false, WINCH_ID);
        }
        if (canSee){
            camera = new Vision();    
        }
        if (canConvey){
            conveyorRelay=new Conveyor(DIGITAL_SIDECAR_MODULE,CONVEYOR_SPIKE_ID,PLATE_ID);
        }
        if (canClimb){
            climber=new Climber(CLIMBER_ID);
        }
    }
 
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        autonomous4();
   
    }


    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        if (canDrive){
            if (XBoxC.DRIVER.START.nowPressed()) {
                driveInverted = !driveInverted;
                drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, driveInverted);
                drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, driveInverted);
                drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, driveInverted);
                drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, driveInverted);
            }
            drive.arcadeDrive(XBoxC.DRIVER);
    
        }
        if (canShoot){
            shooter.periodic();
            if (XBoxC.OPERATOR.RB.nowPressed()){ shooter.incrShooterSpeed();}  
            if (XBoxC.OPERATOR.LB.nowPressed()){ shooter.decrShooterSpeed();}
            if (XBoxC.OPERATOR.B.nowPressed()){  shooter.toggleShooter();}
            if (XBoxC.OPERATOR.A.isPressed()){  shooter.fire();}
        }
        XBoxC.periodic();
        if (canClimb){
            climber.periodic(XBoxC.DRIVER.right.nowPressed());
        }
        if ((XBoxC.DRIVER.BACK.isPressed()|XBoxC.OPERATOR.START.isPressed())&&canSwap){
            XBoxC.swapDriverAndOperator();
        }
        if(canConvey)
        {
            conveyorRelay.convey();
        }
        // add logic for each button here as needed
        
    }  
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {   
        
    }
    //shoots from corner of pyramid, drives back, picks up discs from center line and shoots them
    public void autonomous1() {
        shooter.setShooterMotorSpeed(shooter.SHOOTER_BASE_RPM);
        try {
            while(Math.abs(shooter.shooterMotor.getSpeed()-shooter.SHOOTER_BASE_RPM)>50){
                //waiting for wheel to speed up
                //TODO  put in time limit?
                //why do we need a time limit?
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);
        
        drive.drive(-0.5, 0);
        Timer.delay(0.5);     
        conveyorRelay.goForward();   
        drive.drive(-.5, 0.5);
        Timer.delay(0.5);
        drive.drive(-0.5, 0);
        Timer.delay(0.5);        
        drive.drive(0.5,0);
        Timer.delay(0.5);
        drive.drive(0, 0.5);
        Timer.delay(0.5);
        drive.drive(0.5,0);
        Timer.delay(0.5);
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);
    }
    //shoots from corner of pyramid, drives forward and picks up discs from under pyramid, drives back and shoots them
    public void autonomous2() {
        shooter.setShooterMotorSpeed(shooter.SHOOTER_BASE_RPM);
        try {
            while(Math.abs(shooter.shooterMotor.getSpeed()-shooter.SHOOTER_BASE_RPM)>50){
                //waiting for wheel to speed up
                //TODO  put in time limit?
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);  
        //aim and fire before driving
        
        drive.drive(0.5, 0.8);
        Timer.delay(0.5);
        
        conveyorRelay.goForward(); 
        
        //aim and fire here
        drive.drive(-0.5, -0.8);
        Timer.delay(0.5);
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);
    }
    /*
     * aimAtTarget(): returns whether or not the robot is looking at a target.
     * If it is not looking at a target, it will turn the robot until it is.
     */
    //shoots three discs from anywhere
    public void autonomous3(){
        shooter.setShooterMotorSpeed(shooter.SHOOTER_BASE_RPM);
         try {
            while(Math.abs(shooter.shooterMotor.getSpeed()-shooter.SHOOTER_BASE_RPM)>50){
                //waiting for wheel to speed up
                //TODO  put in time limit?
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);  
        //aim and fire before driving
    }
    //shoots discs from center of pyramid, drives forward to collect discs from under pyramid, drives back and shoots those
    public void autonomous4() {
        shooter.setShooterMotorSpeed(shooter.SHOOTER_BASE_RPM);
         try {
            while(Math.abs(shooter.shooterMotor.getSpeed()-shooter.SHOOTER_BASE_RPM)>50){
                //waiting for wheel to speed up
                //TODO  put in time limit?
            }
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);  
        //aim and fire before driving
        
        drive.drive(-1.0, 0);
        Timer.delay(0.5);
        drive.drive(0, 0);
        conveyorRelay.goForward();
        drive.drive(1.0, 0);
        Timer.delay(0.5);
        drive.drive(0, 0);
        shooter.setLoader(true);
        Timer.delay(2);
        shooter.setLoader(false);        
    }
          
    public boolean aimAtTarget()
    {
        int deadband = 10;
        if (Math.abs(getDistanceCenter())<deadband){
            return true;
        }
        else
        {
            if (getDistanceCenter()>deadband) {
                drive.drive(0, -1);
            } else if (getDistanceCenter()<-deadband) {
                drive.drive(0, 1);
            }
            return false;
        }
    }
    
    /**
     * Get distance to the target from the SmartDashboard
     * 
     * @return Distance to target, in feet
     */
    public double getDistanceFromTarget()
    {
        double distance = SmartDashboard.getNumber("TargetDistance");
        
        //May do more stuff with this function eventually
        
        return distance;
    }
    
    /**
     * Gets the center of the target from SmartDashboard
     * 
     * @return Offset from target center, in pixels
     */
    public double getDistanceCenter()
    {
        double center = SmartDashboard.getNumber("TargetCenter");
        
        //May do more stuff with this function eventually
        
        return center;
    }
    
    /**
     * Gets the target number.  Numbering system is:
     * 1: Top Left 2pt Target
     * 2: Top 3pt Target
     * 3: Top Right 2pt Target
     * 4: Bottom (Right) 1pt Target
     * 5: No Target Found
     * 
     * @return Target Number, using numbering system above
     */
    public int getTargetNumber()
    {
        int targetNum = (int) SmartDashboard.getNumber("TargetNumber");
        
        //May do more stuff with this function eventually
        
        return targetNum;
    }
}
