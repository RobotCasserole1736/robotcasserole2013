package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 * @author Ian T.
 */
public class Shooter {

    CANJaguar shooterMotor, loaderMotor;
    DigitalInput loaderSwitch;
    private double m_curSpeed; // -1.0 to 1.0 scale, percentage
    public boolean loaderRunning; // true, loader running. false, loader ready
    
    private final double LOADER_CAN_SPEED = 0.50,
                        /* TODO: get real constants from electrical team */
                        SHOOTER_MAX_RPM = 1000.0,
                        SHOOTER_BASE_RPM = 500.0,
                        SHOOTER_RPM_INCR = 20.0;
                        

    // Class constructor
    public Shooter(int shooterID, int loaderID, int loaderSwChannel) {
        try {
            shooterMotor = new CANJaguar(shooterID);
            shooterMotor.changeControlMode(CANJaguar.ControlMode.kSpeed);
            shooterMotor.enableControl();
            shooterMotor.setPID(0.10, 0.05, 0.00);
            
            loaderMotor = new CANJaguar(loaderID);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        loaderSwitch = new DigitalInput(loaderSwChannel);
    }

    // Check switch and loader and set loader accordingly
    public void periodic() {
        if (loaderRunning && loaderSwitch.get()) {
            setLoader(false);
        }
    }

    // Set the loader state
    public void setLoader(boolean turnOn) {
        try {
            if (!turnOn) {
                loaderMotor.setX(0);
            } else {
                loaderMotor.setX(LOADER_CAN_SPEED);
            }
            loaderRunning = turnOn;
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    // Fire a frisbee
    public void fire() {
        setLoader(true);
    }
    public void fire(double distance, double elevation) {
        
    }

    // Changes the motor's speed, and updates m_curSpeed.
    private void setMotorSpeed(double percentage) {
        try {
            shooterMotor.setX(percentage);
            m_curSpeed = percentage;
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
        if (m_curSpeed < SHOOTER_MAX_RPM) {
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