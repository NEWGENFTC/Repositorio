package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

// Classe usada para inicializar todos os sensores e acionadores usados no robô
public class HardwareClassTracao {
    OpMode opMode;
    //Declaração dos motores
    DcMotor motorFE= null;
    DcMotor motorFD= null;
    DcMotor motorTE= null;
    DcMotor motorTD = null;

    private IMU imu;


    // Objeto da interface de giroscópio


    // Método construtor da classe, usado para obter uma variável importante na inicialização
    public HardwareClassTracao(OpMode opMode) {
        this.opMode = opMode;
    }

    // Método que usamos para inicializar o hardware do sistema
    public void hardwareGeral(HardwareMap hwMap) {

        /*
         * =============================================================================
         *                                  GYRO
         * =============================================================================
         */
        //
        //imu.hwMap.get(IMU.class, "imu");


        /*
         * =============================================================================
         *                                  ACIONADORES
         * =============================================================================
         */

        // Motores que usaremos para mechanum
        motorFE = opMode.hardwareMap.get(DcMotor.class, "motorFE");
        motorTE = opMode.hardwareMap.get(DcMotor.class, "motorTE");
        motorFD = opMode.hardwareMap.get(DcMotor.class, "motorFD");
        motorTD = opMode.hardwareMap.get(DcMotor.class, "motorTD");

        motorTE.setDirection(DcMotor.Direction.FORWARD);
        motorTD.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);
        motorFE.setDirection(DcMotor.Direction.REVERSE);

        motorTE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


    }

}