package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "MecanumTeste", group = "Linear TesteOp")
public class MecanumTeste extends LinearOpMode {
    // Objeto que conta tempo
    ElapsedTime runtime = new ElapsedTime();

    // Objeto que guarda os nossos acionadores e sensores
    HardwareClassTracao hard = new HardwareClassTracao(this);

    // Respectivamente eixos do gamepad y, x, x2 (outro analógico)
    double drive, turn, giro;

    // Vetor para acionamentos dos motores;
    private double[] poder = new double[4];

    // Variável que guarda o ângulo do robô (radianos)
    double angle;

    @Override
    public void runOpMode() {
        // Telemetria de inicio
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Inicia o hardware do robô
        //hard.hardwareGeral();

        runtime.reset();

        // Espera o botão start na Ds
        waitForStart();

        while (opModeIsActive()) {
            /*
             * drive controla a translação do robô (frente e trás)
             * turn controla a translação do robô (esquerda e direita)
             * giro controla a rotação do robô
             *
             *
             */
            drive = -gamepad1.right_stick_x;  // Negativo por causa do gamepad
            turn = -gamepad1.left_stick_y; // Multiplicamos por um valor para melhorar o strafe
            giro = gamepad1.left_stick_x;

            /*
             * Calculo para orientação no campo
             * caso queira desativar, apenas apague ou comente a linha abaixo
             */
            //fieldOriented(drive, turn);

            // Valores para movimentação com mecanum (lados espelhados) NÃO MEXER!!!!!!!!!
            //Motor Esquerda Frente;
            poder[0] = drive + turn + giro;
            // Motor Esquerda trás;
            poder[1] = drive - turn - giro;
            // Motor Direita Frente;
            poder[2] = drive - turn + giro;
            // Motor Direita trás;
            poder[3] = drive + turn - giro;

            // Verificar se algum valor é maior que 1
            if (Math.abs(poder[0]) > 1 || Math.abs(poder[1]) > 1
                    || Math.abs(poder[2]) > 1 || Math.abs(poder[3]) > 1) {

                //Achar o maior valor
                double max;
                max = Math.max(Math.abs(poder[0]), Math.abs(poder[1]));
                max = Math.max(Math.abs(poder[2]), max);
                max = Math.max(Math.abs(poder[3]), max);

                //Não ultrapassar +/-1 (proporção);
                poder[0] /= max;
                poder[1] /= max;
                poder[2] /= max;
                poder[3] /= max;
            }

            // Metodo setPower que manda a potência para os motores.
            hard.motorFE.setPower(poder[0]);
            hard.motorTE.setPower(poder[1]);
            hard.motorFD.setPower(poder[2]);
            hard.motorTD.setPower(poder[3]);

            DcMotorEx motor = (DcMotorEx) hardwareMap.get(DcMotor.class, "motorTE");

// Para motor REV HD Hex + UltraPlanetary 125:1 (3x cartuchos 5:1 reais)
            final double TICKS_PER_REV = 4007.326;

// Retorna ticks por segundo
            double ticksPerSecond = motor.getVelocity();

// Converte ticks/s para RPM do eixo final
            double rpm = (ticksPerSecond / TICKS_PER_REV) * 60.0;

            telemetry.addData("Velocidade (RPM)", rpm);

            // {
            //                }
            // Telemetria com os valores de cada roda
            telemetry.addData("Motor Esquerdo %.2f ", poder[0]);
            telemetry.addData("Motor EsquerdoTras %.2f ", poder[1]);
            telemetry.addData("Motor Direita %.2f ", poder[2]);
            telemetry.addData("Motor DireitaTras %.2f ", poder[3]);
            telemetry.addData("Orientação %.2f ", Math.toDegrees(angle));
            telemetry.addData("Tempo passado ", runtime.toString());
            telemetry.addData("O diaxo do código atualizou!!", runtime.toString());
            telemetry.addData("--> RPM (Via DcMotorEx)", "%.2f RPM", rpm);
            telemetry.update();
        }
    }


    }
