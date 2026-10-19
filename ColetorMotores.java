package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ColetorMotores {

    // 1. Declaração dos dois motores do coletor
    private DcMotor motorColetor = null;
    //private DcMotor coletorDireito = null;

    public void init(HardwareMap hardwareMap) {
        // 2. Mapeamento do hardware (Configure estes nomes no Driver Station)

        motorColetor = hardwareMap.get(DcMotor.class, "motorColetor");
        //coletorDireito = hardwareMap.get(DcMotor.class, "coletor_direito");

        // 3. Inverte o motor direito para que ambos girem sincronizados no mesmo sentido físico
        motorColetor.setDirection(DcMotorSimple.Direction.REVERSE);
        //coletorDireito.setDirection(DcMotorSimple.Direction.REVERSE);

        //coletorDireito.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //telemetry.addData("Status", "Dois Motores Configurados!");
    }

    public void puxar(double controle) {
        if (controle > 0.1) {
            // Sugar bolinhas: velocidade proporcional ao gatilho direito (RT)
            motorColetor.setPower(0.8);

            //coletorDireito.setPower(potencia);
            //telemetry.addData("Coletor", "Sugando | Potência: " + potencia);+-/
        }
        else {
            // Se soltar os gatilhos, os motores desligam e rodam livres até parar
            motorColetor.setPower(0);

            //coletorDireito.setPower(0.0);
            //telemetry.addData("Coletor", "Parado (Livre)");
        }

    }
    public void empurrar(double controle){
        if (controle < -0.1) {

            // Expulsar bolinhas: velocidade invertida proporcional ao gatilho esquerdo (LT)
            motorColetor.setPower(-0.8);

            //coletorDireito.setPower(potencia);
            //telemetry.addData("Coletor", "Expulsando | Potência: " + potencia);
        }
        else {
            // Se soltar os gatilhos, os motores desligam e rodam livres até parar
            motorColetor.setPower(0.0);

            //coletorDireito.setPower(0.0);
            //telemetry.addData("Coletor", "Parado (Livre)");
        }

    }
}
