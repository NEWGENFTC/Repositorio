package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TesteBacukp { // Date: 09/10

    // Variáveis de motor e do IMU
    private DcMotor motorFE, motorTE, motorFD, motorTD;
    private DcMotor[] motores;
    private IMU imu;

    private double anguloAlvo = 0;
    // SE O ROBÔ CONTINUAR OSCILANDO, INVERTA O SINAL DE KP PARA -0.005
    private final double Kp = -0.005;
    private double rxFinal;
    private double anguloGraus;

    public void init(HardwareMap hwMap) {

        // Inicialização do IMU
        IMU(hwMap);

        // Inicialização dos motores
        motorFE = hwMap.get(DcMotor.class, "motorFE");
        motorTE = hwMap.get(DcMotor.class, "motorTE");
        motorFD = hwMap.get(DcMotor.class, "motorFD");
        motorTD = hwMap.get(DcMotor.class, "motorTD");

        // Inversões de motor mantidas conforme sua configuração física
        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);

        motores = new DcMotor[] {
                motorFE,
                motorTE,
                motorFD,
                motorTD
        };

        for (DcMotor motor : motores) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // 1. CALCULAR AS POTÊNCIAS (Usa o parâmetro rotate repassado)
    public double[] calcularPotencias(
            double forward,
            double strafe,
            double rotate) {

        double potenciaFE = forward + strafe + rotate;
        double potenciaTE = forward - strafe + rotate;
        double potenciaFD = forward - strafe - rotate;
        double potenciaTD = - forward - strafe + rotate;

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

        for (double potencia : potencias) {
            potenciaMax = Math.max(potenciaMax, Math.abs(potencia));
        }

        for (int i = 0; i < potencias.length; i++) {
            potencias[i] = potencias[i] / potenciaMax;
        }

        return potencias;
    }

    // 3. APLICAR AS POTÊNCIAS NOS MOTORES
    public void aplicarPotencias(double[] potencias, double velocidadeMax) {
        for (int i = 0; i < motores.length; i++) {
            motores[i].setPower(potencias[i] * velocidadeMax);
        }
    }

    // 4. MÉTODOS DE CONTROLE DO IMU
    public void IMU(HardwareMap hwMap) {
        imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot variaveisDeControl = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );
        imu.initialize(new IMU.Parameters(variaveisDeControl));
        imu.resetYaw();
    }

    // 5. METODO PRINCIPAL DE DRIVE
    public void orientadoAoRobo(
            double forward,
            double strafe,
            double rotate) {

        // PASSO 1: Atualiza o ângulo vindo do IMU
        if (imu != null) {
            anguloGraus = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        }

        // PASSO 2: Calcula se o giro é manual ou se deve ativar a correção
        if (Math.abs(rotate) > 0.05) {
            // Piloto movimentando o giro
            rxFinal = rotate;
            anguloAlvo = anguloGraus;
        } else {
            // Piloto soltou o analógico de giro -> Ativa correção
            double erroDeAngulo = anguloAlvo - anguloGraus;

            // Normalização do erro entre -180 e 180 graus
            while (erroDeAngulo > 180) erroDeAngulo -= 360;
            while (erroDeAngulo < -180) erroDeAngulo += 360;

            // Força de correção proporcional
            rxFinal = erroDeAngulo * Kp;

            // Limita a força de correção automática em 50%
            rxFinal = Math.max(-0.5, Math.min(0.5, rxFinal));

            // Zona morta (Deadband)
            if (Math.abs(erroDeAngulo) < 1.0) {
                rxFinal = 0.0;
            }
        }

        // PASSO 3: Calcula, normaliza e envia a potência aos motores USANDO o rxFinal
        double velocidadeMax = 1.0;
        double[] potencias = calcularPotencias(forward, strafe, rxFinal);
        potencias = normalizarPotencias(potencias);
        aplicarPotencias(potencias, velocidadeMax);
    }

    public IMU getImu() {
        return imu;
    }

    public void setImu(IMU imu) {
        this.imu = imu;
    }
}