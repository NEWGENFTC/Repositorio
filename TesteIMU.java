package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class TesteIMU {

    private DcMotor motorFE, motorTE, motorFD, motorTD;
    private DcMotor[] motores;
    private IMU imu;

    private double anguloAlvo = 0;
    private final double Kp = 0.015;
    private final double Kd = 0.003;
    double erro = 0;
    double erroPassado;
    private double rxFinal = 0;
    private double anguloGraus = 0;
    private double anguloRadianos = 0;

    private final ElapsedTime dt = new ElapsedTime();
    double tempo = dt.seconds();
    private double xAnterior = 0.0;
    private double yAnterior = 0.0;
    private final double LIMITE_ACELERACAO = 3.5; // Ajustado para resposta mais rápida ao controle

    public void init(HardwareMap hwMap) {
        motorFE = hwMap.get(DcMotor.class, "motorFE");
        motorTE = hwMap.get(DcMotor.class, "motorTE");
        motorFD = hwMap.get(DcMotor.class, "motorFD");
        motorTD = hwMap.get(DcMotor.class, "motorTD");

        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);

        motores = new DcMotor[] { motorFE, motorTE, motorFD, motorTD };

        for (DcMotor motor : motores) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        imu = hwMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientacaoControl = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );
        imu.initialize(new IMU.Parameters(orientacaoControl));
        imu.resetYaw();

        dt.reset();
    }

    public void resetarRumo() {
        if (imu != null) {
            imu.resetYaw();
            anguloAlvo = 0.0;
        }
    }

    /**
     * CORREÇÃO DE LAG: Lemos o IMU uma única vez por ciclo e guardamos na memória RAM.
     * Isso reduz o tempo de execução do loop drasticamente, eliminando o delay.
     */
    public void atualizarSensores() {
        if (imu != null) {
            YawPitchRollAngles orientacao = imu.getRobotYawPitchRollAngles();
            anguloGraus = orientacao.getYaw(AngleUnit.DEGREES);
            anguloRadianos = orientacao.getYaw(AngleUnit.RADIANS);
        }
    }

    private double[] aplicarRampa(double forward, double strafe) {
        dt.reset();

        // Proteção contra saltos gigantes de tempo caso o código trave por algum motivo externo
        if (tempo > 0.1) tempo = 0.01;

        double mudancaMaxima = LIMITE_ACELERACAO * tempo;

        double difY = forward - yAnterior;
        double yFiltrado = yAnterior + Math.max(-mudancaMaxima, Math.min(mudancaMaxima, difY));

        double difX = strafe - xAnterior;
        double xFiltrado = xAnterior + Math.max(-mudancaMaxima, Math.min(mudancaMaxima, difX));

        yAnterior = yFiltrado;
        xAnterior = xFiltrado;

        return new double[]{yFiltrado, xFiltrado};
    }

    private void calcularCorrecaoGiro(double rotate) {
      /*  anguloAlvo = 0;
        rxFinal = rotate;
        if(anguloAlvo != anguloGraus && rotate <0.05){
            erro = anguloGraus - anguloAlvo;
            erroPassado = erro;
            double P = erro * Kp;
            double D = (erroPassado - erro) / tempo;
            rxFinal = P + D;
        }
        else{
            anguloAlvo = anguloGraus;
            rxFinal = rotate;
        }*/

    }

    // Equações originais preservadas milimetricamente
    public double[] calcularPotencias(double forward, double strafe) {
        double potenciaFE = forward + strafe + rxFinal;
        double potenciaTE = forward - strafe + rxFinal;
        double potenciaFD = forward - strafe - rxFinal;
        double potenciaTD = - forward - strafe + rxFinal;

        return new double[] { potenciaFE, potenciaTE, potenciaFD, potenciaTD };
    }

    public double[] normalizarPotencias(double[] potencias) {
        double potenciaMax = 1.0;
        for (double potencia : potencias) {
            potenciaMax = Math.max(potenciaMax, Math.abs(potencia));
        }
        if (potenciaMax > 1.0) {
            for (int i = 0; i < potencias.length; i++) {
                potencias[i] /= potenciaMax;
            }
        }
        return potencias;
    }

    public void aplicarPotencias(double[] potencias, double velocidadeMax) {
        for (int i = 0; i < motores.length; i++) {
            motores[i].setPower(velocidadeMax * potencias[i]);
        }
    }

    public void orientadoAoRobo(double forward, double strafe, double rotate) {
        atualizarSensores(); // Executa o bloco de leitura única
        double[] inputsFiltrados = aplicarRampa(forward, strafe);
        calcularCorrecaoGiro(rotate);

        double[] potencias = calcularPotencias(inputsFiltrados[0], inputsFiltrados[1]);
        potencias = normalizarPotencias(potencias);
        aplicarPotencias(potencias, 1.0);
    }

    public void orientadoAoCampo(double forward, double strafe, double rotate) {
        atualizarSensores(); // Executa o bloco de leitura única
        double[] inputsFiltrados = aplicarRampa(forward, strafe);
        calcularCorrecaoGiro(rotate);

        // Aplicação correta e sincronizada dos inputs filtrados na matriz de rotação
        double xRotacionado = inputsFiltrados[1] * Math.cos(-anguloRadianos) - inputsFiltrados[0] * Math.sin(-anguloRadianos);
        double yRotacionado = inputsFiltrados[1] * Math.sin(-anguloRadianos) + inputsFiltrados[0] * Math.cos(-anguloRadianos);

        double[] potencias = calcularPotencias(yRotacionado, xRotacionado);
        potencias = normalizarPotencias(potencias);
        aplicarPotencias(potencias, 1.0);
    }

    public double getAnguloGraus() { return anguloGraus; }
    public double getAnguloAlvo() { return anguloAlvo; }
    public double getCorrecaoGiro() { return rxFinal; }
}
