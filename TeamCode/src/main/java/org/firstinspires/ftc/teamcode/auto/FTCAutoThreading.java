package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.ftcdevcommon.Threading;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Autonomous(name = "Auto Threading Demo", group = "Robot")
//@Disabled
public class FTCAutoThreading extends LinearOpMode {

    private static final String TAG = FTCAutoThreading.class.getSimpleName();

    private enum ElevatorLevel {
        LEVEL_1, LEVEL_2, LEVEL_3
    }

    @Override
    public void runOpMode() {

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        Callable<Void> callableDriveToPosition = () -> {
            straightLineMotion(1560, 90.0, .75); // clicks, angle, velocity
            return null;
        };

        ElevatorLevel targetLevel = ElevatorLevel.LEVEL_2;
        Callable<ElevatorLevel> callableMoveElevator = () -> {
            return moveElevator(targetLevel); // returns the actual level
        };

        CompletableFuture<Void> asyncMoveRobot = Threading.launchAsync(callableDriveToPosition);
        RobotLog.dd(TAG, "Starting thread to move the robot");
        telemetry.addLine("Starting thread to move the robot");
        telemetry.update();

        CompletableFuture<ElevatorLevel> asyncMoveElevator = Threading.launchAsync(callableMoveElevator);
        RobotLog.dd(TAG, "Starting thread to move the elevator");
        telemetry.addLine("Starting thread to move the elevator");
        telemetry.update();

        RobotLog.dd(TAG, "Wait for both threads to complete");
        telemetry.addLine("Wait for both threads to complete");
        telemetry.update();

        // Wait for both threads to complete:
        ElevatorLevel actualLevel;
        try {
            Threading.getFutureCompletion(asyncMoveRobot);
            actualLevel = Threading.getFutureCompletion(asyncMoveElevator);
        } catch (Exception ex) {
            RobotLog.ee(TAG, "Threading demo completed with an error " + ex.getMessage());
            telemetry.addData("Threading demo", "Completed with and error " + ex.getMessage());
            telemetry.update();
            sleep(1000);  // pause to display final telemetry message.
            return;
        }

        RobotLog.dd(TAG, "Both threads to complete; final elevator level " + actualLevel);
        telemetry.addData("Threading demo", "Complete");
        telemetry.addLine("Final elevator level " + actualLevel);
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    // Where “angle” is 0.0 (forward), -180.0 (back), 90.0 (strafe left), or -90.0 (strafe right).
    private void straightLineMotion(int pTargetClicks, double pAngle, double pVelocity) {
        sleep(3000); // here is where you will actually move the robot
        telemetry.addLine("Robot motion is complete");
        telemetry.update();
    }

    private ElevatorLevel moveElevator(ElevatorLevel pElevatorLevel) {
        sleep(2000); // here is where you will actually move the elevator
        telemetry.addLine("Elevator movement is complete");
        telemetry.update();
        return ElevatorLevel.LEVEL_1; // example: didn't go as far as we asked
    }

}
