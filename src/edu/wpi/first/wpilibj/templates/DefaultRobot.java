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
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
/**
 * Default 1745 Robot.
 */
public class DefaultRobot extends IterativeRobot {
	//Robot Wiring Diagram
        final static boolean CANENABLED = true;
        //Jaguars
        //CAN IDs
        final static int RIGHTFRONTMOTORCAN_ID = 5;
        final static int LEFTFRONTMOTORCAN_ID = 6;
        final static int RIGHTREARMOTORCAN_ID = 7;
        final static int LEFTREARMOTORCAN_ID = 8;
        //PWM IDs
        final static int RIGHTFRONTMOTORPWM_ID = 0;
        final static int LEFTFRONTMOTORPWM_ID = 1;
        final static int RIGHTREARMOTORPWM_ID = 2;
        final static int LEFTREARMOTORPWM_ID = 3;
        //Victors
        //details the port that each victor is attached to on the relay modual
        final static int COMPRESSORVICTOR_ID = 1;
        final static int LEDSVICTOR_ID = 2;
        //SOLINOIDS
        //details the port on the relay module that each solonoid is connected
        final static int LAUNCHERSOLINOID_ID = 1;
        //DIO
        final static int PNUMATICPRESSURESENSOR = 1;
        final static int FRONTSONICSENSOR = 2;
        final static int REARSONICSENSOR = 3;
        
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
        Compressor m_Compressor;
        Solenoid launcherSolenoid;
        Victor LEDVictor;
             
	int m_dsPacketsReceivedInCurrentSecond;	// keep track of the ds packets received in the current second
        
	// Declare variables for the two joysticks being used
	Joystick m_rightStick;			// joystick 1 (arcade stick or right tank stick)
	Joystick m_leftStick;			// joystick 2 (tank left stick)	

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
                 
        //ASSIGN CAN/PWM IDS BASED ON WEATHER CAN IS ENABLED ON THE ROBOT. 
        if (CANENABLED)
        {
            //try catch loop all the things ( because we want to know where the can cable is unplugged)
            System.out.println("CAN Enabled\n");
            try {
                    m_rightFrontMotor = new CANJaguar(RIGHTFRONTMOTORCAN_ID);
                } 
            catch (CANTimeoutException ex) 
                {
                    System.out.println("CAN TIMEOUT EXCEPTION ID:"+ RIGHTFRONTMOTORCAN_ID +"\n");
                }
            try {
                    m_rightBackMotor = new CANJaguar(RIGHTREARMOTORCAN_ID);
                } 
            catch (CANTimeoutException ex) 
                {
                    System.out.println("CAN TIMEOUT EXCEPTION ID:"+ RIGHTREARMOTORCAN_ID +"\n");
                }
            try {
                    m_leftFrontMotor = new CANJaguar(LEFTFRONTMOTORCAN_ID);
                } 
            catch (CANTimeoutException ex) 
                {
                    System.out.println("CAN TIMEOUT EXCEPTION ID:"+ LEFTFRONTMOTORCAN_ID +"\n");
                }
            try {
                    m_leftBackMotor = new CANJaguar(LEFTREARMOTORCAN_ID);
                } 
            catch (CANTimeoutException ex) 
                {
                    System.out.println("CAN TIMEOUT EXCEPTION ID:"+ LEFTREARMOTORCAN_ID +"\n");
                }
        }
        else // if CAN is not enabled Declare all the Jags with PWMs
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
        m_Compressor = new Compressor(PNUMATICPRESSURESENSOR,COMPRESSORVICTOR_ID);
        launcherSolenoid= new Solenoid(LAUNCHERSOLINOID_ID);
        LEDVictor = new Victor(LEDSVICTOR_ID);
        
        //invert all the motors (our electrical set up)
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
	
        m_dsPacketsReceivedInCurrentSecond = 0;

        // Define joysticks being used at USB port #1 and USB port #2 on the Drivers Station
	m_rightStick = new Joystick(1);
	m_leftStick = new Joystick(2);
        
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
                m_Compressor.start();
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
                        System.out.println("Pressure Switch: " + m_Compressor.getPressureSwitchValue());
                        System.out.println("Compressor enabled?: " + m_Compressor.enabled());
			printSec++;
		}
               
               checkCompressor();
               
	}
        private void checkCompressor()
        {
            if(!m_Compressor.getPressureSwitchValue())
               {
                   m_Compressor.setRelayValue(Relay.Value.kOn);
               }
               else
               {
                   m_Compressor.setRelayValue(Relay.Value.kOff);
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
                checkCompressor();
                //@todo write Autocode
		/* below code commented out for safety
                 * 
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
        m_dsPacketsReceivedInCurrentSecond++;					// increment DS packets received

        // put Driver Station-dependent code here
        m_robotDrive.tankDrive(m_leftStick, m_rightStick);	// drive with tank style
        checkCompressor(); //turn the compressor on and off
        
        //actuate the launcher.
        if(launcherSolenoid.get() != m_rightStick.getButton(Joystick.ButtonType.kTrigger))
        {
            System.out.println(launcherSolenoid.get());
            launcherSolenoid.set(m_rightStick.getButton(Joystick.ButtonType.kTrigger));
        }
        //@todo add eltoro code
        }

	


	
    int GetLoopsPerSec() {
        return 20;
    }	
}
