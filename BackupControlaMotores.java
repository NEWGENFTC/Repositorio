package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class BackupControlaMotores {

    //Variáveis de motor e do IMU
    private DcMotor motorFE, motorTE, motorFD, motorTD;

    private DcMotor[] motores;
    //private IMU imu;

    private IMU imu;

    public void init(HardwareMap hwMap){

        //Inicialização dos motores e assimilação ao nome no drive
        motorFE = hwMap.get(DcMotor.class, "motorFE");
        motorTE = hwMap.get(DcMotor.class, "motorTE");
        motorFD = hwMap.get(DcMotor.class, "motorFD");
        motorTD = hwMap.get(DcMotor.class, "motorTD");

        //acertamento das direções dos motores
        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);

        //Meio para usar o PID do encoder do motor para mínimas correções
        motorFE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        motores = new DcMotor[] {
                motorFE,
                motorTE,
                motorFD,
                motorTD
        };

        imu = hwMap.get(IMU.class, "imu");

        //Inicialização e direções do IMU
        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT);

        imu.initialize(new IMU.Parameters(RevOrientation));

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

    //Equação para o uso da potência de cada motor para mover o robô para uma direção específica (acertamento dos vetores)
    public void drive(double forward, double strafe, double rotate){
        double potenciaFE = forward + strafe + rotate;
        double potenciaTE = forward - strafe + rotate;
        double potenciaFD = forward - strafe - rotate;
        double potenciaTD = - forward - strafe + rotate;

        //Variáveis para controle de potência
        double potenciaMax = 1.0;
        double velocidadeMax = 1.0;

        //Verificação de extravazamento de potência
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaFE));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaTE));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaFD));
        potenciaMax = Math.max(potenciaMax, Math.abs(potenciaTD));

        motorFE.setPower(velocidadeMax * (potenciaFE / potenciaMax));
        motorTE.setPower(velocidadeMax * (potenciaTE / potenciaMax));
        motorFD.setPower(velocidadeMax * (potenciaFD / potenciaMax));
        motorTD.setPower(velocidadeMax * (potenciaTD / potenciaMax));

    }

    //Função que vai estabelecer as funções trigonométricas do IMU com a arena
    public void fieldRelative(double forward, double strafe, double rotate){
         double anguloRad = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

         double teta = Math.atan2(strafe, forward);
         double raio = Math.hypot(forward, strafe);

         teta = AngleUnit.normalizeRadians(teta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

         double frente = raio * Math.sin(teta);
         double lado = raio * Math.cos(teta);

         frente = strafe * Math.sin(-anguloRad) + forward * Math.cos(-anguloRad);
         lado = strafe * Math.cos(-anguloRad) - forward * Math.sin(-anguloRad);

         this.drive(frente, lado, rotate);
    }

    public IMU getImu() {
        return imu;
    }

    public void setImu(IMU imu) {
        this.imu = imu;
    }
}
