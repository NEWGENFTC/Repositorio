package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "MainMecanum", group = "Linear TesteOp")
public class MainMecanum extends OpMode {

    ControlaMotores controla = new ControlaMotores();
    double forward, strafe, rotate;

    @Override
    public void init(){
        controla.init(hardwareMap);
        telemetry.addData("Status", "inicializado. Esperando comando");
        telemetry.update();
    }

    @Override
    public void loop(){

        //rotate
        forward = -gamepad1.left_stick_y;
        //forward
        strafe = -gamepad1.left_stick_x;
        //strafe
        rotate = gamepad1.right_stick_x;

      /*  if (gamepad1.options) { // Botão Start/Options do comando
            tracao.getImu().resetYaw();
        }*/

        controla.orientadoAoRobo(forward, strafe, rotate);

        //telemetry.addData("Angulação", tracao.getImu().getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        telemetry.addData("Tudo dando certo! Programação orientada a robo ligada.", 0);
    }
}
