package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class RefazendoCodigo { // Date: 09/10
    private DcMotor motorFE, motorFD, motorTE, motorTD;
    private DcMotor[] motores;
    private IMU imu;
    private double anguloGraus;
    private double anguloRadianos;
    private int ticksO;
    private int ticksFE;
    private int ticksFD;
    private int ticksTD;

    public void init (HardwareMap hwMap){
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
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Chamando o IMU
        imu = hwMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientacaoControl = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );
        imu.initialize(new IMU.Parameters(orientacaoControl));
        imu.resetYaw();

    }

    //Atualização de sensores IMU no início da partida
    public void atualizarSensores() {
        if (imu != null) {
            YawPitchRollAngles orientacao = imu.getRobotYawPitchRollAngles();
            anguloGraus = orientacao.getYaw(AngleUnit.DEGREES);
            anguloRadianos = orientacao.getYaw(AngleUnit.RADIANS);
        }
    }

    //Cálculo de vetores para movimento Mecanum
    public double[] calcularPotencias(
    double forward,
    double strafe,
    double rotate) {

        double potenciaFE = - forward - strafe - rotate;
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

    //Filtro de potências (Para todas se moverem igual)
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

    //Aplicação de potências em cada motor
    public void aplicarPotencias(double[] potencias, double velocidadeMax) {
        for (int i = 0; i < motores.length; i++) {
            motores[i].setPower(potencias[i] * velocidadeMax);
        }
    }

    //Robot Oriented
    public void orientadoAoRobo(
            double forward,
            double strafe,
            double rotate){
        // PASSO 3: Calcula, normaliza e envia a potência aos motores USANDO o rxFinal
        atualizarSensores();

        double velocidadeMax = 1.0;
        double[] potencias = calcularPotencias(forward, strafe, rotate);
        potencias = normalizarPotencias(potencias);
        aplicarPotencias(potencias, velocidadeMax);
    }

    //Field Oriented
    public void orientadoAoCampo(double forward, double strafe, double rotate) {
        atualizarSensores();


        double xRotacionado = strafe * Math.cos(anguloRadianos) - forward * Math.sin(anguloRadianos);
        double yRotacionado = strafe * Math.sin(anguloRadianos) + forward * Math.cos(anguloRadianos);

        double[] potencias = calcularPotencias(yRotacionado, xRotacionado, rotate);
        potencias = normalizarPotencias(potencias);
        aplicarPotencias(potencias, 1);
    }

    public void mostrarTelemetria(Telemetry telemetry){
        ticksO = motorTE.getCurrentPosition();
        ticksFE = motorFE.getCurrentPosition();
        ticksFD = motorFD.getCurrentPosition();
        ticksTD = motorTD.getCurrentPosition();

        telemetry.addData("Roda lado", ticksO);
        telemetry.addData("Roda FE", ticksFE);
        telemetry.addData("Roda FD", ticksFD);
        telemetry.addData("Roda TD", ticksTD);
    }

    public IMU getImu() {
        return imu;
    }

    public double getAnguloGraus() {
        return anguloGraus;
    }
}