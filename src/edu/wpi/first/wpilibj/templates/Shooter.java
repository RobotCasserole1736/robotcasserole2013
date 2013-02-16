package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Victor;
/*
 * @author Ian T.
 */
public class Shooter {
    Relay winchRelay;
    public boolean loaderIsCAN;
    CANJaguar shooterMotor,loaderMotorCAN;
    Victor loaderMotorPWM;
    DigitalInput loaderSwitch;
    private double m_curSpeed, m_curAngle, m_targetAngle;
    public final double MIN_ANGLE = 30, //placeholder
                        MAX_ANGLE = 60,
                        ANGLE_INCR = 5;
    public boolean loaderRunning; // true, loader running. false, loader read
    private boolean m_loaderIsPressed,m_loaderWasPressed;
    private double[][] distToSpeed = /* double[distance][speedrpm] */
        {
            { // 2x8 dimension
                5, 10, 15, 20, 25, 30, 35, 40
            },{
                250, 230, 240, 250, 260, 270, 280, 290
            }
        };                   
    public final double 
            LOADER_CAN_SPEED = 0.50,
            /* TODO: get real constants from electrical team */
            SHOOTER_MAX_RPM = -3000.0,
            SHOOTER_BASE_RPM = -1000.0,
            SHOOTER_RPM_INCR = -50.0,
            LOADER_PWM_SPEED=0.5,
            LOADER_PWM_STOP_SPEED=0;
    
    // Class constructor
    public Shooter(int shooterID, int loaderID,
                   int loaderSwChannel, int loaderModule, 
                   boolean loaderIsCANJag, int winchID) {
        try {
            shooterMotor = new CANJaguar(shooterID);
                shooterMotor.changeControlMode(CANJaguar.ControlMode.kSpeed);
                shooterMotor.enableControl();
                shooterMotor.setPID(0.30, 0.005, 0.002);
                shooterMotor.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
                shooterMotor.configEncoderCodesPerRev(360);
                winchRelay= new Relay(winchID);
                loaderIsCAN=loaderIsCANJag;
                if (loaderIsCAN){
                    loaderMotorCAN = new CANJaguar(loaderID);
                } else {
                    loaderMotorPWM=new Victor(loaderModule,loaderID);
                    //System.out.println("PWM id");
                    //System.out.println(loaderID);
                }
                loaderSwitch = new DigitalInput(loaderModule,loaderSwChannel);
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
                IronChef.canShoot=false;
            }
    } 
    // Check switch and loader and set loader accordingly
    public void periodic() {
        try {
            SmartDashboard.putNumber("Shooter Speed Measured", shooterMotor.getSpeed());
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        SmartDashboard.putBoolean("Loader Switch", loaderSwitch.get());
        if (loaderRunning && loaderNowPressed()) {
            setLoader(false);
        }
        //TODO updateM_curAngle
        if (m_curAngle >m_targetAngle+.01){
            winchRelay.set(Relay.Value.kForward);
        }else if (m_curAngle <m_targetAngle-.01){
            winchRelay.set(Relay.Value.kReverse);
        }else{
            stopWinch();
        }
    }
    public double updateCurAngle(){
        //TODO
        Relay.Value wV=winchRelay.get();
        if (wV==Relay.Value.kForward){
            m_curAngle+=1;
        }else if(wV==Relay.Value.kReverse){
            m_curAngle-=1;
        }
        return m_curAngle;
    }
    
    public boolean isTiltedDown() {
        return (m_curAngle <= MIN_ANGLE);
    }
    public boolean isTiltedUp() {
        return (m_curAngle >= MAX_ANGLE);
    }
    public boolean isAngleAtTarget() {
        return (m_curAngle == m_targetAngle);
    }
    public void movAngleDown() {
        setTargetAngle(m_targetAngle - ANGLE_INCR);
    }
    public void movAngleUp(){
        setTargetAngle(m_curAngle + ANGLE_INCR);
    }
    public void stopWinch(){
        //TODO set motors to stopped
        winchRelay.set(Relay.Value.kOff);
        m_targetAngle=updateCurAngle();
    }
    
    
    public void setTargetAngle(double newTargetAngle){
        m_targetAngle=newTargetAngle;
        if (m_targetAngle<=MIN_ANGLE){
            m_targetAngle=MIN_ANGLE;
        }
        if (m_targetAngle>=MAX_ANGLE){
            m_targetAngle=MAX_ANGLE;
        }
    }
    
    public boolean loaderNowPressed(){
        m_loaderWasPressed = m_loaderIsPressed;
        m_loaderIsPressed = loaderSwitch.get();
        return m_loaderIsPressed && !m_loaderWasPressed;
    }
    // Fire a frisbee
    public void fire() {
        setLoader(true);
        if (m_curSpeed == 0.0) {
            setShooterMotorSpeed(SHOOTER_BASE_RPM);
        }
    }
    // Automatically fire a frisbee
    public void fire(double distance, double elevation) {
        
    }

    // Changes the motor's speed, and updates m_curSpeed.
    public void setShooterMotorSpeed(double rpm) {
        try {
            shooterMotor.setX(rpm);
            m_curSpeed = rpm;
            SmartDashboard.putNumber("Shooter Speed Setpoint", m_curSpeed);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    
    // Toggles the motor state
    public void toggleShooter() {
        if (m_curSpeed != 0.00) {
            setShooterMotorSpeed(0.00);
        } else {
            setShooterMotorSpeed(SHOOTER_BASE_RPM);  
        }
        if (loaderRunning){
                setLoader(false);
        } 
    }
    
    // Increases speed rpm
    public void incrShooterSpeed() {
        if (m_curSpeed == 0.0){
            setShooterMotorSpeed(SHOOTER_BASE_RPM);
        }else if (m_curSpeed > SHOOTER_MAX_RPM) {
            setShooterMotorSpeed(m_curSpeed + SHOOTER_RPM_INCR);
        }
    }
    
    // Decreases speed rpm 
    public void decrShooterSpeed() {
        if (m_curSpeed < SHOOTER_BASE_RPM) {
            setShooterMotorSpeed(m_curSpeed - SHOOTER_RPM_INCR);
        }
    }
    public void autoSetShooterSpeed(double dist) {
        if (IronChef.canShoot){
            try {
                shooterMotor.setX(interpolate(dist));
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Returns the required rpm speed in order to shoot "dist" meters.
     * @param dist desired distance in meters.
     * @return shooter motor speed in rpms.
     */
    public double interpolate(double dist){
        double nextDist, lastDist, thisSpeed, lastSpeed;
        for (int i=0; i<distToSpeed[0].length; i++) {
            if (distToSpeed[0][i] >= dist) {
                nextDist = distToSpeed[0][i];
                lastDist = distToSpeed[0][i-1];
                thisSpeed = distToSpeed[1][i];
                lastSpeed = distToSpeed[1][i-1];
                double slope = ( (thisSpeed-lastSpeed) / (nextDist-lastDist) );
                return (lastSpeed+slope*(dist-lastDist)); // return y 
            }
        }
        return -1;
    }
    
    // Set the loader state
    public void setLoader(boolean turnOn) {
        SmartDashboard.putBoolean("Loader should be", turnOn);
        if (IronChef.canShoot){
            try {
                if (!turnOn) {
                    if (loaderIsCAN){
                        loaderMotorCAN.setX(0);
                    } else {
                        loaderMotorPWM.set(LOADER_PWM_STOP_SPEED);
                    }
                } else {
                    if (loaderIsCAN){
                        loaderMotorCAN.setX(LOADER_CAN_SPEED);
                    } else {
                        loaderMotorPWM.set(LOADER_PWM_SPEED);
                    }
                }
                loaderRunning = turnOn;
            }catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
        }else{
            loaderRunning = turnOn;
        } 
    }
}
