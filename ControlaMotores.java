package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class ControlaMotores {

    private DcMotor motorFE, motorTE, motorFD, motorTD;
    private IMU imu;

    private double anguloAlvo = 0; //onde a gente quer ficar (PID)
    private double anguloGraus = 0; // IMU
    private double anguloRadianos = 0; // calculo do IMU

    private final double Kp = 0.03; //constante de proporção (PID)
    private final double Kd = 0.001; //constante de derivada (PID)
    private double erroAnterior = 0; //calculo da derivada (PID)


    private double output = 0;

    private double potenciaFE = 0.0;
    private double potenciaTE = 0.0;
    private double potenciaFD = 0.0;
    private double potenciaTD = 0.0;

    //Variaveis para a rampa
    private final ElapsedTime timer = new ElapsedTime(); // Delta t (PID)
    private double xAnterior = 0.0; //
    private double yAnterior = 0.0;
    private final double LIMITE_ACELERACAO = 3.5;

    public void init(HardwareMap hwMap) {
        motorFE = hwMap.get(DcMotor.class, "motorFE");
        motorTE = hwMap.get(DcMotor.class, "motorTE");
        motorFD = hwMap.get(DcMotor.class, "motorFD");
        motorTD = hwMap.get(DcMotor.class, "motorTD");

        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);

        motorFE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorFE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hwMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientacaoControl = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        );
        imu.initialize(new IMU.Parameters(orientacaoControl));
        imu.resetYaw();

        timer.reset();
    }

    public void resetarRumo() {
        if (imu != null) {
            imu.resetYaw();
            anguloAlvo = 0.0;
        }
    }

    public void atualizarSensores() {
        if (imu != null) {
            YawPitchRollAngles orientacao = imu.getRobotYawPitchRollAngles();
            anguloGraus = orientacao.getYaw(AngleUnit.DEGREES);
            anguloRadianos = orientacao.getYaw(AngleUnit.RADIANS);
        }
    }

    private void aplicarRampa(double forward, double strafe) {
        xAnterior = strafe;
        yAnterior = forward;
    }

    private double calcularCorrecaoGiro(double rotate) {
        // Captura o tempo decorrido global do ciclo do robô e reseta IMEDIATAMENTE fora do else
        double dt = timer.seconds();
        timer.reset();

        // Evita que divisões por zero ou tempos bizarros quebrem a matemática do Kd
        if (dt <= 0.0) {
            dt = 0.001;
        }

       /* if (rotate > 0.05) {
            output = rotate;
            anguloAlvo = anguloGraus; // Atualiza o alvo para a posição atual enquanto o piloto gira manualmente
            erroAnterior = 0;
        } else {
            double erroDeAngulo = anguloAlvo - anguloGraus;

            // CORREÇÃO 1: A normalização angular do erro DEVE vir antes do cálculo da derivada
            while (erroDeAngulo > 180) erroDeAngulo -= 360;
            while (erroDeAngulo < -180) erroDeAngulo += 360;

            // CORREÇÃO 2: Cálculo limpo da variação com o dt estabilizado globalmente
            double derivada = (erroDeAngulo - erroAnterior) / dt;

            output = (Kp * erroDeAngulo) + (Kd * derivada);
            erroAnterior = erroDeAngulo;
        }*/
        return 0;
    }

    public void calcularPotencias(double forward, double strafe, double rotate) {
        // CORREÇÃO 3: Verifique se os sinais de rotação somam e subtraem nos lados certos.
        // O padrão Mecanum para rotação para a direita é somar na Esquerda e subtrair na Direita:
        potenciaFE = forward + strafe + calcularCorrecaoGiro(rotate); //output = calcularCorrecaoGiro()
        potenciaTE = forward - strafe + calcularCorrecaoGiro(rotate);
        potenciaFD = forward - strafe - calcularCorrecaoGiro(rotate);
        potenciaTD = -forward - strafe + calcularCorrecaoGiro(rotate); // Ajuste este sinal se o motor TD andar invertido no teste de translação puro
    }

    public void normalizarPotencias() {
        double potenciaMax = Math.max(1.0, Math.abs(potenciaFE));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaTE));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaFD));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaTD));

        if (potenciaMax > 1.0) {
            potenciaFE /= potenciaMax;
            potenciaTE /= potenciaMax;
            potenciaFD /= potenciaMax;
            potenciaTD /= potenciaMax;
        }
    }

    public void aplicarPotencias(double velocidadeMax) {
        motorFE.setPower(potenciaFE * velocidadeMax);
        motorTE.setPower(potenciaTE * velocidadeMax);
        motorFD.setPower(potenciaFD * velocidadeMax);
        motorTD.setPower(potenciaTD * velocidadeMax);
    }

    public void orientadoAoRobo(double forward, double strafe, double rotate) {
        atualizarSensores();
        calcularCorrecaoGiro(rotate);
        calcularPotencias(forward, strafe, rotate);
        normalizarPotencias();
        aplicarPotencias(1.0);
    }

    public void orientadoAoCampo(double forward, double strafe, double rotate) {
        atualizarSensores();
        aplicarRampa(forward, strafe);
        calcularCorrecaoGiro(rotate);

        double xRotacionado = xAnterior * Math.cos(-anguloRadianos) - yAnterior * Math.sin(-anguloRadianos);
        double yRotacionado = xAnterior * Math.sin(-anguloRadianos) + yAnterior * Math.cos(-anguloRadianos);

        calcularPotencias(yRotacionado, xRotacionado, rotate);
        normalizarPotencias();
        aplicarPotencias(1.0);
    }
}
