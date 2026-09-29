package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ControlaMotores {

    //Variáveis de motor e do IMU
    private DcMotor motorFE, motorTE, motorFD, motorTD;

    private DcMotor[] motores;
    //private IMU imu;

    public void init(HardwareMap hwMap){

        //Inicialização dos motores e assimilação ao nome no drive
        motorFE = hwMap.get(DcMotor.class, "motorFE");
        motorTE = hwMap.get(DcMotor.class, "motorTE");
        motorFD = hwMap.get(DcMotor.class, "motorFD");
        motorTD = hwMap.get(DcMotor.class, "motorTD");

        //acertamento das direções dos motores
        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);

        motores = new DcMotor[] {
                motorFE,
                motorTE,
                motorFD,
                motorTD
        };
        for(DcMotor motor : motores) {

            motor.setMode(
                    DcMotor.RunMode.RUN_USING_ENCODER
            );
        }
    }


    // 1. CALCULAR AS POTÊNCIAS
    public double[] calcularPotencias(
            double forward,
            double strafe,
            double rotate) {

        double potenciaFE = forward + strafe + rotate;
        double potenciaTE = forward - strafe + rotate;
        double potenciaFD = forward - strafe - rotate;
        double potenciaTD = -forward - strafe + rotate;

        return new double[] {
                potenciaFE,
                potenciaTE,
                potenciaFD,
                potenciaTD
        };
    }


    // 2. NORMALIZAR AS POTÊNCIAS
    public double[] normalizarPotencias(double[] potencias) {

        double potenciaMax = 1.0;

        // Encontra a maior potência absoluta
        for (double potencia : potencias) {

            potenciaMax = Math.max(
                    potenciaMax,
                    Math.abs(potencia)
            );
        }

        // Divide todas as potências pelo maior valor
        for (int i = 0; i < potencias.length; i++) {

            potencias[i] = potencias[i] / potenciaMax;
        }

        return potencias;
    }


    // 3. APLICAR AS POTÊNCIAS NOS MOTORES
    public void aplicarPotencias(
            double[] potencias,
            double velocidadeMax) {

        for (int i = 0; i < motores.length; i++) {

            double potenciaFinal =
                    velocidadeMax * potencias[i];

            motores[i].setPower(potenciaFinal);
        }
    }


    // 4. METODO PRINCIPAL DE DRIVE
    public void orientadoAoRobo(
            double forward,
            double strafe,
            double rotate) {

        double velocidadeMax = 1.0;

        // Calcula as potências
        double[] potencias = calcularPotencias(
                forward,
                strafe,
                rotate
        );

        // Normaliza as potências
        potencias = normalizarPotencias(potencias);

        // Aplica as potências nos motores
        aplicarPotencias(
                potencias,
                velocidadeMax
        );
    }

}
