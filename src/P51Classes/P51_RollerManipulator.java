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
 * Increment the Rollers Speed down (min neutral)
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

    public P51_RollerManipulator() throws CANTimeoutException {
        this.setPosition(false);
        this.setDirection(true);
        this.setSpeed(0);
        this.setSpeedIncrement(0);
        if (this.CANEnabled)
        {
            this.rightCAN = new CANJaguar(1);
            this.right = this.rightCAN;
        }
        else
        {
            this.rightJag = new Jaguar(1);
            this.right = this.rightCAN;  
        }
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
        pistons.set(position);
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
        if (speed < -1)
            speed = -1;
        if ( speed > 1)
            speed = 1;
        this.speed = speed;
        if (this.speed > 0)
            this.setDirection(true);
        if (this.speed < 0)
            this.setDirection(false);
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
        if (speedIncrement < -1)
            speedIncrement = -1;
        if ( speedIncrement > 1)
            speedIncrement = 1;
        this.speedIncrement = Math.abs(speedIncrement);
    }
    
    
    
    
   
    
}
