package org.firstinspires.ftc.teamcode;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name="Enhanced TeleOp", group="Linear Opmode")
public class TeleOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Use GamepadManager from your library
        GamepadManager gamepadManager = PanelsGamepad.INSTANCE.getFirstManager();

        // 1. Hardware Mapping - Using DcMotorEx for better velocity control if needed later
        DcMotorEx leftFront  = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx leftBack   = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        DcMotorEx rightBack  = hardwareMap.get(DcMotorEx.class, "rightBack");

        // 2. Configuration & Braking
        // BRAKE mode makes the robot stop instantly when power is 0 (essential for precision)
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse the left side (Standard for most drivetrains)
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addLine("Ready to Start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Get the wrapped gamepad for consistent input handling
            Gamepad g1 = gamepadManager.asCombinedFTCGamepad(gamepad1);

            // 3. Drive Speed Multiplier
            // Holding Left Bumper = 30% speed. Normal = 100%.
            // Tip: Use a "Toggle" for slow mode if your drivers prefer not to hold the button!
            double speedMod = g1.left_bumper ? 0.3 : 1.0;

            // 4. Joystick Inputs
            // Mecanum physics: Y is forward, X is strafe, RX is rotation
            double y  = -g1.left_stick_y;
            double x  = g1.left_stick_x * 1.1; // 1.1 multiplier helps fix "strafing drag"
            double rx = g1.right_stick_x;

            // 5. Mecanum Math
            // Normalize values so they never exceed 1.0 power
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            double frontLeftPower  = (y + x + rx) / denominator;
            double backLeftPower   = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower  = (y + x - rx) / denominator;

            // 6. Apply Powers with Multiplier
            leftFront.setPower(frontLeftPower * speedMod);
            leftBack.setPower(backLeftPower * speedMod);
            rightFront.setPower(frontRightPower * speedMod);
            rightBack.setPower(backRightPower * speedMod);

            // 7. Feedback for the Driver
            telemetry.addData("Drive Mode", g1.left_bumper ? "SLOW (30%)" : "FULL SPEED");
            telemetry.addData("Sticks", "Y: %.2f | X: %.2f | RX: %.2f", y, x, rx);
            telemetry.update();
        }
    }
}