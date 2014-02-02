/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 * This "BuiltinDefaultCode" provides the "default code" functionality as used in the "Benchtop Test."
 *
 * The BuiltinDefaultCode extends the IterativeRobot base class to provide the "default code"
 * functionality to confirm the operation and usage of the core control system components, as
 * used in the "Benchtop Test" described in Chapter 2 of the 2009 FRC Control System Manual.
 *
 * This program provides features in the Disabled, Autonomous, and Teleop modes as described
 * in the benchtop test directions, including "once-a-second" debugging printouts when disabled,
 * a "KITT light show" on the solenoid lights when in autonomous, and elementary driving
 * capabilities and "button mapping" of joysticks when teleoperated.  This demonstration
 * program also shows the use of the user watchdog timer.
 *
 * This demonstration is not intended to serve as a "starting template" for development of
 * robot code for a team, as there are better templates and examples created specifically
 * for that purpose.  However, teams may find the techniques used in this program to be
 * interesting possibilities for use in their own robot code.
 *
 * The details of the behavior provided by this demonstration are summarized below:
 *
 * Disabled Mode:
 * - Once per second, print (on the console) the number of seconds the robot has been disabled.
 *
 * Autonomous Mode:
 * - Flash the solenoid lights like KITT in Knight Rider
 * - Example code (commented out by default) to drive forward at half-speed for 2 seconds
 *
 * Teleop Mode:
 * - Select between two different drive options depending upon Z-location of Joystick1
 * - When "Z-Up" (on Joystick1) provide "arcade drive" on Joystick1
 * - When "Z-Down" (on Joystick1) provide "tank drive" on Joystick1 and Joystick2
 * - Use Joystick buttons (on Joystick1 or Joystick2) to display the button number in binary on
 *   the solenoid LEDs (Note that this feature can be used to easily "map out" the buttons on a
 *   Joystick.  Note also that if multiple buttons are pressed simultaneously, a "15" is displayed
 *   on the solenoid LEDs to indicate that multiple buttons are pressed.)
 *
 * This code assumes the following connections:
 * - Driver Station:
 *   - USB 1 - The "right" joystick.  Used for either "arcade drive" or "right" stick for tank drive
 *   - USB 2 - The "left" joystick.  Used as the "left" stick for tank drive
 *
 * - Robot:
 *   - Digital Sidecar 1:
 *     - PWM 1/3 - Connected to "left" drive motor(s)
 *     - PWM 2/4 - Connected to "right" drive motor(s)
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class DefaultRobot extends IterativeRobot {
	//Robot Wiring Diagram
        final static boolean CANENABLED = true;
        //Jaguars
        //CAN IDs
        final static int RIGHTFRONTMOTORCAN_ID = 4;
        final static int LEFTFRONTMOTORCAN_ID = 5;
        final static int RIGHTREARMOTORCAN_ID = 8;
        final static int LEFTREARMOTORCAN_ID = 9;
        //PWM IDs
        final static int RIGHTFRONTMOTORPWM_ID = 0;
        final static int LEFTFRONTMOTORPWM_ID = 1;
        final static int RIGHTREARMOTORPWM_ID = 2;
        final static int LEFTREARMOTORPWM_ID = 3;
        //Victors
        //details the port that each victor is attached to on the relay modual
        final static int COMPRESSORVICTOR_ID = 0;
        //Sulenoids
        //details the port on the relay module that each sulonid is connected
        final static int LAUNCHERSOLINOID_ID = 0;
        
        //DIO
        final static int PNUMATICPRESSURESENSOR = 0;
        //AIO
        
        //JOYSTICKS
        final static int RIGHTJOYSTICK = 1;
        final static int LEFTJOYSTICK = 2;
        final static int GAMEPAD = 3;
        //Buttons
        final static int FIREBUTTON = 1;
        final static int GRABBUTTON = 2;
        final static int DEPLOYBUTTON = 3;
                
        // Declare variable for the robot drive system
	RobotDrive m_robotDrive;		// robot will use Can Devices 1 2 3 and 4 for drive motors
        SpeedController m_rightFrontMotor, m_rightBackMotor,
                  m_leftFrontMotor, m_leftBackMotor;
        

        
      
	int m_dsPacketsReceivedInCurrentSecond;	// keep track of the ds packets received in the current second
        
	// Declare variables for the two joysticks being used
	Joystick m_rightStick;			// joystick 1 (arcade stick or right tank stick)
	Joystick m_leftStick;			// joystick 2 (tank left stick)

	static final int NUM_JOYSTICK_BUTTONS = 16;
	boolean[] m_rightStickButtonState = new boolean[(NUM_JOYSTICK_BUTTONS+1)];
	boolean[] m_leftStickButtonState = new boolean[(NUM_JOYSTICK_BUTTONS+1)];

	// Declare variables for each of the eight solenoid outputs
	static final int NUM_SOLENOIDS = 8;
	Solenoid[] m_solenoids = new Solenoid[NUM_SOLENOIDS];

	// drive mode selection
	static final int UNINITIALIZED_DRIVE = 0;
	static final int ARCADE_DRIVE = 1;
	static final int TANK_DRIVE = 2;
	int m_driveMode;

	// Local variables to count the number of periodic loops performed
	int m_autoPeriodicLoops;
	int m_disabledPeriodicLoops;
	int m_telePeriodicLoops;

    /**
     * Constructor for this "BuiltinDefaultCode" Class.
     *
     * The constructor creates all of the objects used for the different inputs and outputs of
     * the robot.  Essentially, the constructor defines the input/output mapping for the robot,
     * providing named objects for each of the robot interfaces.
     */
    public DefaultRobot() {
        System.out.println("BuiltinDefaultCode Constructor Started\n");
            
        //declaring CAN Jaguar Variable
        
        //ASSIGN CAN/PWM IDS BASED ON WEATHER CAN IS ENABLED ON THE ROBOT. 
        if (CANENABLED)
        {
            System.out.println("CAN Enabled\n");
            try {
                m_rightFrontMotor = new CANJaguar(RIGHTFRONTMOTORCAN_ID);
                m_rightBackMotor = new CANJaguar(RIGHTREARMOTORCAN_ID);
                m_leftFrontMotor = new CANJaguar(LEFTFRONTMOTORCAN_ID);
                m_leftBackMotor = new CANJaguar(LEFTREARMOTORCAN_ID);
            } catch (CANTimeoutException ex) {
               System.out.println("CAN TIMEOUT EXCEPTION!\n");
               ex.printStackTrace();
            }
        }
        else
        {
             System.out.println("PWM enabled\n");
             m_rightFrontMotor = new Jaguar(RIGHTFRONTMOTORPWM_ID);
             m_rightBackMotor = new Jaguar(RIGHTREARMOTORPWM_ID);
             m_leftFrontMotor = new Jaguar(LEFTFRONTMOTORPWM_ID);
             m_leftBackMotor = new Jaguar(LEFTREARMOTORPWM_ID);
        }
        // Create a robot using standard right/left robot drive on 
		m_robotDrive = new RobotDrive(m_leftFrontMotor, m_leftBackMotor, 
                                                m_rightFrontMotor, m_rightBackMotor);

		m_dsPacketsReceivedInCurrentSecond = 0;

		// Define joysticks being used at USB port #1 and USB port #2 on the Drivers Station
		m_rightStick = new Joystick(1);
		m_leftStick = new Joystick(2);
                
                

		// Iterate over all the buttons on each joystick, setting state to false for each
		int buttonNum = 1;						// start counting buttons at button 1
		for (buttonNum = 1; buttonNum <= NUM_JOYSTICK_BUTTONS; buttonNum++) {
			m_rightStickButtonState[buttonNum] = false;
			m_leftStickButtonState[buttonNum] = false;
		}

		// Iterate over all the solenoids on the robot, constructing each in turn
		int solenoidNum = 1;						// start counting solenoids at solenoid 1
		for (solenoidNum = 0; solenoidNum < NUM_SOLENOIDS; solenoidNum++) {
			m_solenoids[solenoidNum] = new Solenoid(solenoidNum + 1);
		}

		// Set drive mode to uninitialized
		m_driveMode = UNINITIALIZED_DRIVE;

		// Initialize counters to record the number of loops completed in autonomous and teleop modes
		m_autoPeriodicLoops = 0;
		m_disabledPeriodicLoops = 0;
		m_telePeriodicLoops = 0;

		System.out.println("BuiltinDefaultCode Constructor Completed\n");
	}


	/********************************** Init Routines *************************************/

	public void robotInit() {
		// Actions which would be performed once (and only once) upon initialization of the
		// robot would be put here.

		System.out.println("RobotInit() completed.\n");
	}

	public void disabledInit() {
		m_disabledPeriodicLoops = 0;			// Reset the loop counter for disabled mode
        startSec = (int)(Timer.getUsClock() / 1000000.0);
		printSec = startSec + 1;
	}

	public void autonomousInit() {
		m_autoPeriodicLoops = 0;				// Reset the loop counter for autonomous mode
	}

	public void teleopInit() {
		m_telePeriodicLoops = 0;				// Reset the loop counter for teleop mode
		m_dsPacketsReceivedInCurrentSecond = 0;	// Reset the number of dsPackets in current second
		m_driveMode = UNINITIALIZED_DRIVE;		// Set drive mode to uninitialized
	}

	/********************************** Periodic Routines *************************************/
	static int printSec;
	static int startSec;

	public void disabledPeriodic()  {
		// feed the user watchdog at every period when disabled
		Watchdog.getInstance().feed();

		// increment the number of disabled periodic loops completed
		m_disabledPeriodicLoops++;

		// while disabled, printout the duration of current disabled mode in seconds
		if ((Timer.getUsClock() / 1000000.0) > printSec) {
			System.out.println("Disabled seconds: " + (printSec - startSec));
			printSec++;
		}
	}

	public void autonomousPeriodic() {
		// feed the user watchdog at every period when in autonomous
		Watchdog.getInstance().feed();

		m_autoPeriodicLoops++;
		/* the below code (if uncommented) would drive the robot forward at half speed
		 * for two seconds.  This code is provided as an example of how to drive the
		 * robot in autonomous mode, but is not enabled in the default code in order
		 * to prevent an unsuspecting team from having their robot drive autonomously!
		 */
		/* below code commented out for safety
		if (m_autoPeriodicLoops == 1) {
			// When on the first periodic loop in autonomous mode, start driving forwards at half speed
			m_robotDrive->Drive(0.5, 0.0);			// drive forwards at half speed
		}
		if (m_autoPeriodicLoops == (2 * GetLoopsPerSec())) {
			// After 2 seconds, stop the robot
			m_robotDrive->Drive(0.0, 0.0);			// stop robot
		}
		*/
	}

        public void teleopPeriodic() {
        // feed the user watchdog at every period when in autonomous
        Watchdog.getInstance().feed();

        // increment the number of teleop periodic loops completed
        m_telePeriodicLoops++;

        /*
         * Code placed in here will be called only when a new packet of information
         * has been received by the Driver Station.  Any code which needs new information
         * from the DS should go in here
         */

        m_dsPacketsReceivedInCurrentSecond++;					// increment DS packets received

        // put Driver Station-dependent code here

        // Demonstrate the use of the Joystick buttons

        Solenoid[] firstGroup = new Solenoid[4];
        Solenoid[] secondGroup = new Solenoid[4];
        for (int i = 0; i < 4; i++) {
            firstGroup[i] = m_solenoids[i];
            secondGroup[i] = m_solenoids[i + 4];
        }


        // determine if tank or arcade mode, based upon position of "Z" wheel on kit joystick
        if (m_rightStick.getZ() <= 0) {    // Logitech Attack3 has z-polarity reversed; up is negative
            // use arcade drive
            m_robotDrive.arcadeDrive(m_rightStick, false);			// drive with arcade style (use right stick)
            if (m_driveMode != ARCADE_DRIVE) {
                // if newly entered arcade drive, print out a message
                System.out.println("Arcade Drive\n");
                m_driveMode = ARCADE_DRIVE;
            }
        } else {
            // use tank drive
            m_robotDrive.tankDrive(m_leftStick, m_rightStick);	// drive with tank style
            if (m_driveMode != TANK_DRIVE) {
                // if newly entered tank drive, print out a message
                System.out.println("Tank Drive\n");
                m_driveMode = TANK_DRIVE;
            }
        }
    }

	


	
    int GetLoopsPerSec() {
        return 20;
    }	
}
