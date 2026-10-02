package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class ControlaMotores {

    //Variáveis de motor e do IMU
    private DcMotor motorFE, motorTE, motorFD, motorTD;
    private DcMotor[] motores;
    private IMU imu;
    double anguloAlvo = 0;
    private final double Kp = 0.005;
    double rxFinal;
    private double anguloGraus;

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
                    DcMotor.RunMode.RUN_WITHOUT_ENCODER
            );
        }
    }

    // 1. CALCULAR AS POTÊNCIAS
    public double[] calcularPotencias(
            double forward,
            double strafe,
            double rotate) {

        double potenciaFE = forward + strafe + rxFinal;
        double potenciaTE = forward - strafe + rxFinal;
        double potenciaFD = forward - strafe - rxFinal;
        double potenciaTD = - forward - strafe + rxFinal;

        return new double[] {
                potenciaFE,
                potenciaTE,
                potenciaFD,
                potenciaTD
        };
    }


    // 2. NORMALIZAR AS POTÊNCIAS
    public double[] normalizarPotencias(double[] potencias) {

        double potenciaMax = 1;

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

        if (imu != null) {
            anguloGraus = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        }
        if (Math.abs(rotate) > 0.05) {
            //Estado 1: O piloto ESTÁ forçando um giro manual        }
            rxFinal = rotate; //O robô obedece o controle
            anguloAlvo = anguloGraus; //Atualiza constantemente o alvo para
        } else { //Estado 2: O piloto SOLTOU o analógico de giro. Ativar correção automática!

            //Calcula o erro do ângulo (distância de onde ele deveria estar e onde está

            double erroDeAngulo = anguloAlvo - anguloGraus;

            //Normalização do erro 180 - 180 graus (evita que o robô dê uma volta completa)

            while (erroDeAngulo > 180) erroDeAngulo -= 360;
            while (erroDeAngulo < -180) erroDeAngulo += 360;

            //Força de correção proporcional ao erro

            rxFinal = erroDeAngulo * Kp;

            //Limita a força de correção automática para não assustar o piloto (ex: máximo %50 de energia)
            rxFinal = Math.max(-0.5, Math.min(0.5, rxFinal));

            // Zona morta (Deadband): Se o erro for insignificante (menos de 1 grau), não faça nada
            if (Math.abs(erroDeAngulo) < 1.0) {
                rxFinal = 0.0;
            }
        }
    }
    public void IMU(HardwareMap HwMap){
        imu = HwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot VariaveisDeControl = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );
        imu.initialize(new IMU.Parameters(VariaveisDeControl));

        imu.resetYaw();

    }

    public IMU getImu() {
        return imu;
    }

    public void setImu(IMU imu) {
        this.imu = imu;
    }

}
