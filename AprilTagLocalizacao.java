package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class AprilTagLocalizacao{

    // Criar objetos para as classes do IMU e Limelight //
    private IMU imu;
    private Limelight3A limelight;

    // Informações da telemetria //
    private double tx = 0;
    private double ty = 0;
    private double ta = 0;
    private Pose3D pose;
    private LLResult results;

    // Variáveis PD //
   /* double Kp = 0.003;
    double erro = 0;
    double ultimoErro = 0;
    double alvoX = 0;
    double tolerancia = 1;
    double Kd = 0.0001;
    double tempoAtual = 0;
    double ultimoTempo = 0;
    */



    public void init(HardwareMap hwMap){
        limelight = hwMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(60);
        limelight.start();
        limelight.pipelineSwitch(3);

        imu = hwMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }

    //BOTAR ESSA PARTE NO CADERNO (IMU, LIMELIGHT E ODOMETRIA JUNTOS)
    public void aprilTag() {
        double anguloRobo = imu.getRobotYawPitchRollAngles().getYaw();
        limelight.updateRobotOrientation(anguloRobo);
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null && llResult.isValid()) {

            results = limelight.getLatestResult();
            tx = llResult.getTx();
            ty = llResult.getTy();
            ta = llResult.getTa();
            pose = llResult.getBotpose_MT2();

        }
    }

    public void mostrarTelemetria(Telemetry telemetry){
        telemetry.addData("Tx", tx);
        telemetry.addData("Ty", ty);
        telemetry.addData("Ta", ta);
        telemetry.addData("Posição do robô", pose);
    }

    public double getTx() { return tx; }

    public LLResult getResults() { return results; }
}