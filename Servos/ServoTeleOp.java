package org.firstinspires.ftc.teamcode.Servos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ServoTeleOp extends OpMode{

    ServoTeste servo = new ServoTeste();

    public double Valor_Atual = 0.4;
    public double Devagar = 0.2;
    public double Medio = 0.5;
    public double Rapido = 0.8;

    @Override
    public void init () {
        servo.init(hardwareMap);
    }

    @Override
    public void loop () {
        if (gamepad2.x){
            Valor_Atual = Devagar;
        }
        else if (gamepad2.y){
            Valor_Atual = Medio;
        }
        else if (gamepad2.b){
            Valor_Atual = Rapido;
        }

        if (gamepad2.a) {
            servo.setServoPos(0.47);
        }
        else {
            servo.setServoPos(0);
        }

        if (gamepad2.left_stick_y == 0 && gamepad2.left_stick_x == 0){
            servo.setServoRot1(0);
            servo.setServoRot2(0);
        }

        if (gamepad2.left_stick_y == (1.0)) {
            servo.setServoRot1(-Valor_Atual);
            servo.setServoRot2(Valor_Atual);
        } else if (gamepad2.left_stick_y == (-1.0)) {
            servo.setServoRot1(Valor_Atual);
            servo.setServoRot2(-Valor_Atual);
        }

        if (gamepad2.left_stick_x == (1.0)) {
            servo.setServoRot1(Valor_Atual);
            servo.setServoRot2(Valor_Atual);
        } else if (gamepad2.left_stick_x == (-1.0)) {
            servo.setServoRot1(-Valor_Atual);
            servo.setServoRot2(-Valor_Atual);
        }


        telemetry.addData("Analógico esquerdo X: ", gamepad2.left_stick_x);
        telemetry.addData("Analógico esquerdo Y: ", gamepad2.left_stick_y);
        telemetry.addData("Garra: ", gamepad2.a);
        telemetry.addData("Velocidade: ", Valor_Atual);
        telemetry.update();
    }
}
