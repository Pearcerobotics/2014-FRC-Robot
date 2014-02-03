/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package P51Classes;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
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

    

    private int position;
    private int direction;
    private double speed;
    private double speedIncrement;
    private SpeedController right, left;

    public P51_RollerManipulator() {
        this.setPosition(0);
        this.setDirection(1);
        this.setSpeed(0);
        this.setSpeedIncrement(0);
    }
    
    /*
    * Increased the Speed of the Rollers by the Speed increment in the direction of the direction (will not go past 1)
    */
    public void speedUp()
    {
       double temp = 0;
       temp = this.getSpeed()+this.getDirection()*this.getSpeedIncrement();
       this.setSpeed(temp);
    }
    /*
    * Decrese the Speed of the Rollers by the Speed increment in the direction of the direction (will not go past 0)
    */
    public void speedDown()
    {
        this.setSpeed(this.getSpeed()-this.getDirection()*this.getSpeedIncrement());
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDirection() {
        return direction;
    }

    private void setDirection(int direction) {
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
            this.setDirection(1);
        if (this.speed < 0)
            this.setDirection(-1);
        this.right.set(this.speed);
        this.left.set(-this.speed);
    }

    public double getSpeedIncrement() {
        return speedIncrement;
    }

    public void setSpeedIncrement(double speedIncrement) {
        if (speedIncrement < -1)
            speedIncrement = -1;
        if ( speedIncrement > 1)
            speedIncrement = 1;
        this.speedIncrement = Math.abs(speedIncrement);
    }
    
    
    
    
   
    
}
