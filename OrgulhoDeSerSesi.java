package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Servos.ServoTeste;

@TeleOp(name = "OrgulhoDeSerGay", group = "Linear TesteOp")
public class OrgulhoDeSerSesi extends LinearOpMode {
    // Objeto que conta tempo
    ElapsedTime runtime = new ElapsedTime();

    // Objeto que guarda os nossos acionadores e sensores
    HardwareClassTracao hard = new HardwareClassTracao(this);

    // Respectivamente eixos do gamepad y, x, x2 (outro analógico)
    double drive, turn, giro, cotovelo, ombro;

    // Vetor para acionamentos dos motores;
    private double[] poder = new double[4];

    // Variável que guarda o ângulo do robô (radianos)
    double angle;

    ServoTeste servo = new ServoTeste();

    public double Valor_Atual = 0.4;
    public double Devagar = 0.2;
    public double Medio = 0.5;
    public double Rapido = 0.8;

    @Override
    public void runOpMode() {
        // Telemetria de inicio
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Inicia o hardware do robô
        //hard.hardwareGeral();

        runtime.reset();
        servo.init(hardwareMap);

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
            drive = -gamepad1.right_stick_x; // Negativo por causa do gamepad
            turn = -gamepad1.left_stick_y; // Multiplicamos por um valor para melhorar o strafe
            giro = -gamepad1.left_stick_x;

            cotovelo = gamepad2.right_stick_y;
            ombro = gamepad2.right_stick_x;

            /*
             * Calculo para orientação no campo
             * caso queira desativar, apenas apague ou comente a linha abaixo
             */
            //fieldOriented(drive, turn);

            // Valores para movimentação com mecanum (lados espelhados)
            //Motor Esquerda Frente;
            poder[0] = drive + turn + giro;
            // Motor Esquerda trás;
            poder[1] = drive - turn + giro;
            // Motor Direita Frente;
            poder[2] = drive - turn - giro;
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

            //Motores do ombro e cotovelo (braço da garra)
           /* if (gamepad2.right_stick_y == 0 && gamepad2.right_stick_x == 0){
                hard.setMotorOmbro(0);
                hard.setMotorCotovelo(0);
            }

            if (gamepad2.right_stick_y == 1){
                hard.setMotorOmbro(0.6);
            }
            else if(gamepad2.right_stick_y == -1){
                hard.setMotorOmbro(-0.6);
            }
            if (gamepad2.right_stick_x == 1){
                hard.setMotorCotovelo(0.6);
            }
            else if(gamepad2.right_stick_x == -1){
                hard.setMotorCotovelo(-0.6);
            }*/

            //Configuração da Velocidade dos servos
            if (gamepad2.x){
                Valor_Atual = Devagar;
            } else if (gamepad2.y){
                Valor_Atual = Medio;
            } else if (gamepad2.b){
                Valor_Atual = Rapido;
            }

            //Garra
            if (gamepad2.a) {
                servo.setServoPos(0.47);
            } else {
                servo.setServoPos(0);
            }

            //Desligar os servos
            if (gamepad2.left_stick_y == 0 && gamepad2.left_stick_x == 0){
                servo.setServoRot1(0);
                servo.setServoRot2(0);
            }

            //Movimentação perpendicular
            if (gamepad2.left_stick_y == (1.0)) {
                servo.setServoRot1(-Valor_Atual);
                servo.setServoRot2(Valor_Atual);
            } else if (gamepad2.left_stick_y == (-1.0)) {
                servo.setServoRot1(Valor_Atual);
                servo.setServoRot2(-Valor_Atual);
            }

            //Rotação no próprio eixo
            if (gamepad2.left_stick_x == (1.0)) {
                servo.setServoRot1(Valor_Atual);
                servo.setServoRot2(Valor_Atual);
            } else if (gamepad2.left_stick_x == (-1.0)) {
                servo.setServoRot1(-Valor_Atual);
                servo.setServoRot2(-Valor_Atual);
            }

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
            telemetry.addData("Motor Esquerdo ", poder[0]);
            telemetry.addData("Motor EsquerdoTras ", poder[1]);
            telemetry.addData("Motor Direita ", poder[2]);
            telemetry.addData("Motor DireitaTras ", poder[3]);
            telemetry.addData("Orientação ", Math.toDegrees(angle));
            telemetry.addData("Analógico esquerdo X: ", gamepad2.left_stick_x);
            telemetry.addData("Analógico esquerdo Y: ", gamepad2.left_stick_y);
            telemetry.addData("Garra: ", gamepad2.a);
            telemetry.addData("Velocidade: ", Valor_Atual);
            telemetry.addData("O diaxo do código atualizou!!", runtime.toString());
            //telemetry.addData("--> RPM (Via DcMotorEx)", "%.2f RPM", rpm);
            telemetry.update();
        }
    }

}