package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PWM;
/*
 * @author Ian T.
 */
public class Shooter {
    public boolean loaderIsCAN;
    CANJaguar shooterMotor,loaderMotorCAN;
    PWM loaderMotorPWM;
    DigitalInput loaderSwitch;
    private double m_curSpeed;
    public boolean loaderRunning; // true, loader running. false, loader read
    private boolean m_loaderIsPressed,m_loaderWasPressed;
    private final double 
            LOADER_CAN_SPEED = 0.50,
            /* TODO: get real constants from electrical team */
            SHOOTER_MAX_RPM = 1000.0,
            SHOOTER_BASE_RPM = 500.0,
            SHOOTER_RPM_INCR = 20.0;
    private final int
            LOADER_PWM_SPEED=200,
            LOADER_PWM_STOP_SPEED=127;
    
    // Class constructor
    public Shooter(int shooterID, int loaderID, int loaderSwChannel, int loaderModule, boolean loaderIsCANJag) {
        try {
            shooterMotor = new CANJaguar(shooterID);
                shooterMotor.changeControlMode(CANJaguar.ControlMode.kSpeed);
                    shooterMotor.enableControl();
                    shooterMotor.setPID(0.30, 0.005, 0.002);
                    shooterMotor.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
                    shooterMotor.configEncoderCodesPerRev(360);
                loaderIsCAN=loaderIsCANJag;
            if (loaderIsCAN){
                loaderMotorCAN = new CANJaguar(loaderID);
            } else {
                loaderMotorPWM=new PWM(loaderModule,loaderID);
                System.out.println("PWM id");
                System.out.println(loaderID);
            }
            loaderSwitch = new DigitalInput(loaderModule,loaderSwChannel);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
            IronChef.canShoot=false;
        }
    }
    
    public boolean loaderNowPressed(){
        m_loaderWasPressed = m_loaderIsPressed;
        m_loaderIsPressed = loaderSwitch.get();
        return m_loaderIsPressed && !m_loaderWasPressed;
    }
    
    // Check switch and loader and set loader accordingly
    public void periodic() {
        if (loaderRunning && loaderNowPressed()) {
            setLoader(false);
        }
    }

    // Set the loader state
    public void setLoader(boolean turnOn) {
        try {
            if (!turnOn) {
                if (loaderIsCAN){
                    loaderMotorCAN.setX(0);
                } else {
                    loaderMotorPWM.setRaw(LOADER_PWM_STOP_SPEED);
                }
            } else {
                if (loaderIsCAN){
                    loaderMotorCAN.setX(LOADER_CAN_SPEED);
                } else {
                    loaderMotorPWM.setRaw(LOADER_PWM_SPEED);
                }
            }
            loaderRunning = turnOn;
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    // Fire a frisbee
    public void fire() {
        setLoader(true);
        if (m_curSpeed == 0.0) {
            setMotorSpeed(SHOOTER_BASE_RPM);
        }
    }
    
    // Automatically fire a frisbee
    public void fire(double distance, double elevation) {
        
    }

    // Changes the motor's speed, and updates m_curSpeed.
    private void setMotorSpeed(double percentage) {
        try {
            shooterMotor.setX(percentage);
            m_curSpeed = percentage;
            SmartDashboard.putNumber("Shooter Speed set", m_curSpeed);
            SmartDashboard.putNumber("Shooter Speed get", shooterMotor.getSpeed());
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
    
    // Toggles the motor state
    public void toggleShooter() {
        if (m_curSpeed > 0.00) {
            setMotorSpeed(0.00);
        } else {
            setMotorSpeed(SHOOTER_BASE_RPM);
        }
    }
    
    // Increases speed percentage by 5%
    public void incrSpeed() {
        if (m_curSpeed == 0.0){
            setMotorSpeed(SHOOTER_BASE_RPM);
        }else if (m_curSpeed < SHOOTER_MAX_RPM) {
            setMotorSpeed(m_curSpeed + SHOOTER_RPM_INCR);
        }
    }
    
    // Decreases speed percentage by 5%
    public void decrSpeed() {
        if (m_curSpeed > SHOOTER_BASE_RPM) {
            setMotorSpeed(m_curSpeed - SHOOTER_RPM_INCR);
        }
    }
}