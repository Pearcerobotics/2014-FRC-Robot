/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package P51Classes;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
/**
 * Code that Drives the Manupulaor
 * 2 Motors Turning Counter clockwise to grab the ball
 * Pnumatic pistons to raise and lower the rollers
 * can be called as normal Jaguar or as CAN enabled Jaguar
 * Can Set Speed of rollers
 * Forward Rollers
 * Reverse Rollers
 * turn off rollers
 * lower the rollers
 * raise the rollers
 * Get the Roller Speed
 * Get the Roller Position
 * Get the Roller Direction
 * Set the Rollers Speed
 * Increment the Rollers Speed up (min neutral)
 * Increment the Rollers Speed down ( min neutral)
 * @author Stuart
 */


public class P51_RollerManipulator {
   
    private boolean position; //True Lowered, False Raised
    private boolean direction; //True Forward, False Reverse
    private boolean CANEnabled; // is CAN enabled on this Manipulator
    private double speed;
    private double speedIncrement; // increment that the roller increases by when speedUp() and speedDown() methods are called value needs to be be between 0 and 1. values smaller than .2 are best
    private SpeedController right, left;
    private CANJaguar rightCAN, leftCAN;
    private Jaguar rightJag, leftJag;
    private Solenoid pistons;

    public P51_RollerManipulator(int rightMotorPort, int leftMotorPort, int solenoidPort, boolean CANEnabled) throws CANTimeoutException {
        
        this.CANEnabled = CANEnabled;
        if (this.CANEnabled)
        {
            this.rightCAN = new CANJaguar(rightMotorPort);
            this.right = this.rightCAN;
            this.leftCAN = new CANJaguar(leftMotorPort);
            this.left = this.leftCAN;
        }
        else
        {
            this.rightJag = new Jaguar(rightMotorPort);
            this.right = this.rightJag;
            this.leftJag = new Jaguar(leftMotorPort);
            this.left = this.leftJag; 
        }
        this.pistons = new Solenoid(solenoidPort);
        this.setPosition(false);
        this.setDirection(true);
        this.setSpeed(0);
        this.setSpeedIncrement(0);
    }
    
    /*
    * Increased the Speed of the Rollers by the Speed increment in the direction of the direction (will not go past 1)
    */
    public void speedUp()
    {
       double temp = 0;
       this.setSpeed(temp);
    }
    /*
    * Decrese the Speed of the Rollers by the Speed increment in the direction of the direction (will not go past 0)
    */
    public void speedDown()
    {
        double temp = 0;
    }
    public boolean getPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
        this.pistons.set(this.position);
    }

    public boolean getDirection() {
        return direction;
    }

    private void setDirection(boolean direction) {
        this.direction = direction;
        }

    public double getSpeed() {
        return speed;
    }
    /*
    * Sets the Speed of the rollers. Protects against Out of bounds cases for >1 and <-1
    * Sets the direction based on positive or negitive numbers
    */
    public void setSpeed(double speed) {
        this.checkOutOfBounds(speed);
        this.speed = speed;
        this.setDirection(this.isPositive(speed));
        this.right.set(this.speed);
        this.left.set(-this.speed);
    }

    public double getSpeedIncrement() {
        return speedIncrement;
    }

    /*
    * Set the Speed increment.
    * protects against values inbetween 1 and 0
    */
    public void setSpeedIncrement(double speedIncrement) {
        this.checkOutOfBounds(speedIncrement);
        this.speedIncrement = Math.abs(speedIncrement);
    }
    //check to see if the value is between -1 and 1 if not set to -1 or 1
    private double checkOutOfBounds(double value)
    {
        if (value < -1)
            value = -1;
        if ( value > 1)
            value = 1;
        return value;
    }
    //check if the number is positive or negitive
    private boolean isPositive(double number)
    {
        return number >= 0;
    }
    
    
    
   
    
}
