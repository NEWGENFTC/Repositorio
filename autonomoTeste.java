package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Autonomous
public class autonomoTeste extends OpMode {

    DcMotor motorFE = null;
    DcMotor motorFD = null;
    DcMotor motorTE = null;
    DcMotor motorTD = null;

    double ticks = 728;
    double novaPosicao;

    public void encoder(double cm){
        motorFE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorTD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorTE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        novaPosicao = Math.round((ticks / 30.15) * cm);

        motorTE.setTargetPosition((int)novaPosicao);
        motorFD.setTargetPosition((int)novaPosicao);
        motorFE.setTargetPosition((int)novaPosicao);
        motorTD.setTargetPosition((int)novaPosicao);

        motorTE.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFD.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFE.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorTD.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    public void Frente(long s) {
        try {
            motorFE.setPower(0.8);
            motorFD.setPower(0.8);
            motorTD.setPower(0.8);
            motorTE.setPower(0.8);
            sleep(s);

        }catch (InterruptedException e){
        e.printStackTrace();
        }
    }
    public void Tras(long s){
        try {
            motorFE.setPower(-0.8);
            motorFD.setPower(-0.8);
            motorTD.setPower(-0.8);
            motorTE.setPower(-0.8);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void Virar_90_D(long s){
        try {
            motorFE.setPower(-1);
            motorFD.setPower(1);
            motorTD.setPower(-1);
            motorTE.setPower(1);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void Virar_90_E(long s){
        try {
            motorFE.setPower(1);
            motorFD.setPower(-1);
            motorTD.setPower(1);
            motorTE.setPower(-1);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void Virar_180(){
        try {
            motorFE.setPower(-1);
            motorFD.setPower(1);
            motorTD.setPower(-1);
            motorTE.setPower(1);
            sleep(1500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    public void Strafe_E(long s){
        try {
            motorFE.setPower(1);
            motorFD.setPower(-1);
            motorTD.setPower(-1);
            motorTE.setPower(1);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    public void Strafe_D(long s){
        try {
            motorFE.setPower(1);
            motorFD.setPower(-1);
            motorTD.setPower(1);
            motorTE.setPower(-1);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    public void Parar(long s){
        try {
            motorFE.setPower(0);
            motorFD.setPower(0);
            motorTD.setPower(0);
            motorTE.setPower(0);
            sleep(s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void motorfe(long s) {
        try {
            motorFE.setPower(1);
            motorFD.setPower(0);
            motorTD.setPower(0);
            motorTE.setPower(0);
            sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void motorfd(long s) {
        try {
            motorFE.setPower(0);
            motorFD.setPower(1);
            motorTD.setPower(0);
            motorTE.setPower(0);
            sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void motortd(long s) {
        try {
            motorFE.setPower(0);
            motorFD.setPower(0);
            motorTD.setPower(1);
            motorTE.setPower(0);
            sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void motorte(long s) {
        try {
            motorFE.setPower(0);
            motorFD.setPower(0);
            motorTD.setPower(0);
            motorTE.setPower(1);
            sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() {

        motorFE = hardwareMap.get(DcMotor.class, "motorFE");
        motorTE = hardwareMap.get(DcMotor.class, "motorTE");
        motorFD = hardwareMap.get(DcMotor.class, "motorFD");
        motorTD = hardwareMap.get(DcMotor.class, "motorTD");

        motorTE.setDirection(DcMotor.Direction.REVERSE);
        motorFD.setDirection(DcMotor.Direction.REVERSE);
        motorFE.setDirection(DcMotor.Direction.REVERSE);
        motorTD.setDirection(DcMotor.Direction.REVERSE);

        motorFE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorTE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //counts per rev = 28 -> 28 * 36 = 728
        //Fórmula da distância CM por ticks ->  728 / 30,15 -> Cada cm vale




    }
    public void loop() {

       /* motorfe(3000);
        Parar(1000);
        motorfd(3000);
        Parar(1000);
        motortd(3000);
        Parar(1000);
        motorte(3000);
        Parar(10000);*/

        Frente(1500);
        Parar(1000);
        /*Virar_90_D(450);
        Frente(1000);
        Parar(200);
        Virar_90_E(430);
        Parar(200);
        Frente(650);
        Parar(250);
        Virar_90_E(430);
        Frente(2500);
        Parar(10000);*/

    }
}
