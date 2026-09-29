package org.firstinspires.ftc.teamcode.Servos;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class ServoTeste {
    public Servo servoPos;

    public CRServo servoRot1;
    public CRServo servoRot2;


    public void init(HardwareMap hwMap) {
        servoPos = hwMap.get(Servo.class, "servo_pos");
        servoRot1 = hwMap.get(CRServo.class, "servoA");
        servoRot2 = hwMap.get(CRServo.class, "servoB");
    }

    public void setServoPos(double angle) {
        servoPos.setPosition(angle);
    }

    public void setServoRot1(double power) {
        servoRot1.setPower(power);
    }

    public void setServoRot2(double power) {
        servoRot2.setPower(power);
    }
}




