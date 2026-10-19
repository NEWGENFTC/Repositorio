package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp(name = "Teste2026")
public class Teste2026 extends OpMode {

    private DcMotor motorFE;
    private DcMotor motorFD;
    private DcMotor motorTE;
    private DcMotor motorTD;

    @Override
    public void init() {

        motorFE = hardwareMap.get(DcMotor.class, "motorFE");
        motorTE = hardwareMap.get(DcMotor.class, "motorTE");
        motorFD = hardwareMap.get(DcMotor.class, "motorFD");
        motorTD = hardwareMap.get(DcMotor.class, "motorTD");

        motorTE.setDirection(DcMotorSimple.Direction.REVERSE);
        motorTD.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFD.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFE.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {

        // Frente/Ré
        double y = -gamepad1.left_stick_y;

        // Strafe
        double x = gamepad1.left_stick_x;

        // Rotação
        double rx = gamepad1.right_stick_x;

        double motorFEPower = y + x + rx;
        double motorFDPower = y - x - rx;
        double motorTEPower = y - x + rx;
        double motorTDPower = y + x - rx;

        // Normalização
        double max = Math.max(
                Math.max(Math.abs(motorFEPower), Math.abs(motorFDPower)),
                Math.max(Math.abs(motorTEPower), Math.abs(motorTDPower))
        );

        if (max > 1.0) {
            motorFEPower /= max;
            motorFDPower /= max;
            motorTEPower /= max;
            motorTDPower /= max;
        }

        motorFE.setPower(motorFEPower);
        motorFD.setPower(motorFDPower);
        motorTE.setPower(motorTEPower);
        motorTD.setPower(motorTDPower);

        telemetry.addData("Frente/Ré", y);
        telemetry.addData("Strafe", x);
        telemetry.addData("Rotação", rx);
        telemetry.update();
    }
}
