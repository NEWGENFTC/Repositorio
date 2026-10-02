package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TesteLançador", group = "Linear TesteOp")
public class TesteLancador extends LinearOpMode {

    // Declaração dos dois motores do lançador e do servo
    private DcMotorEx shooterEsquerdo = null;
    private DcMotorEx shooterDireito = null;
    private Servo indexerServo = null;

    // Constantes para as posições do servo (ajuste no seu robô físico)
    final double POSICAO_RECOLHIDO = 0.1;
    final double POSICAO_EMPURRAR  = 0.8;

    @Override
    public void runOpMode() {

        // 1. Mapeamento do Hardware (Configure com esses nomes exatos no Control Hub)
        shooterEsquerdo = hardwareMap.get(DcMotorEx.class, "shooter_esquerdo");
        shooterDireito  = hardwareMap.get(DcMotorEx.class, "shooter_direito");
        indexerServo    = hardwareMap.get(Servo.class, "indexer_servo");

        // 2. CONFIGURAÇÃO DA DIREÇÃO (O segredo das duas rodas)
        // Um motor gira normal e o outro invertido para que ambos empurrem a peça para frente
        shooterEsquerdo.setDirection(DcMotor.Direction.FORWARD);
        shooterDireito.setDirection(DcMotor.Direction.REVERSE);

        // 3. Configurações de Encoder e Freio para ambos os motores
        shooterEsquerdo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterDireito.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shooterEsquerdo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterDireito.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooterDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Posição inicial do servo
        indexerServo.setPosition(POSICAO_RECOLHIDO);

        telemetry.addData("Status", "Aguardando Inicializacao (Init)");
        telemetry.update();

        waitForStart();

        // Loop principal do TeleOp
        while (opModeIsActive()) {

            // ================================================================
            // CONTROLE DOS DOIS DC MOTORS (LANÇADOR DUPLO)
            // ================================================================
            if (gamepad1.aWasPressed()) {
                // Ativa ambos os motores com 85% de potência
                shooterEsquerdo.setPower(0.85);
                shooterDireito.setPower(0.85);
            } else {
                // Desliga ambos os motores quando o botão é solto
                shooterEsquerdo.setPower(0.0);
                shooterDireito.setPower(0.0);
            }

            // ================================================================
            // CONTROLE DO SERVO INDEXADOR
            // ================================================================
            if (gamepad1.b) {
                indexerServo.setPosition(POSICAO_EMPURRAR);
            } else {
                indexerServo.setPosition(POSICAO_RECOLHIDO);
            }

            // ================================================================
            // TELEMETRIA
            // ================================================================
            telemetry.addData("Motor Esq Power", shooterEsquerdo.getPower());
            telemetry.addData("Motor Dir Power", shooterDireito.getPower());
            telemetry.addData("Velocidade Esq (ticks/s)", shooterEsquerdo.getVelocity());
            telemetry.addData("Velocidade Dir (ticks/s)", shooterDireito.getVelocity());
            telemetry.addData("Servo Posicao", indexerServo.getPosition());
            telemetry.update();
        }
    }
}

