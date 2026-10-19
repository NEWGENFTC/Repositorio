package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LancadorMotores {


    // Declaração dos dois motores do lançador e do servo
    private DcMotorEx lancadorEsquerdo = null;
    private DcMotorEx lancadorDireito = null;
    private DcMotorEx indexer = null;

    public void init(HardwareMap hardwareMap) {

        // 1. Mapeamento do Hardware (Configure com esses nomes exatos no Control Hub)
        lancadorEsquerdo = hardwareMap.get(DcMotorEx.class, "lancadorEsquerdo");
        lancadorDireito = hardwareMap.get(DcMotorEx.class, "lancadorDireito");
        indexer = hardwareMap.get(DcMotorEx.class, "indexer");

        // 2. CONFIGURAÇÃO DA DIREÇÃO (O segredo das duas rodas)
        // Um motor gira normal e o outro invertido para que ambos empurrem a peça para frente
        lancadorEsquerdo.setDirection(DcMotor.Direction.FORWARD);
        lancadorDireito.setDirection(DcMotor.Direction.REVERSE);
        indexer.setDirection(DcMotor.Direction.REVERSE);

        // 3. Configurações de Encoder e Freio para ambos os motores
        lancadorEsquerdo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lancadorDireito.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        indexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lancadorEsquerdo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lancadorDireito.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        indexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lancadorEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lancadorDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lancadorDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Posição inicial do servo
        //indexerServo.setPosition(POSICAO_RECOLHIDO);

    }
            // ================================================================
            // CONTROLE DOS DOIS DC MOTORS (LANÇADOR DUPLO)
            // ================================================================
         public void lancar(double controleLancador) {
             if (controleLancador > 0.1) {
                 // Ativa ambos os motores com 95% de potência
                 lancadorEsquerdo.setPower(1);
                 lancadorDireito.setPower(1);
             } else {
                 lancadorEsquerdo.setPower(0.0);
                 lancadorDireito.setPower(0.0);
             }
         }

            // ================================================================
            // CONTROLE DO SERVO INDEXADOR
            // ================================================================

             public void indexer(double indexerControle){
                 if (indexerControle > 0.1) {
                     //indexerServo.setPosition(POSICAO_EMPURRAR);
                     indexer.setPower(0.3);
                 } else if(indexerControle < -0.1){
                     //indexerServo.setPosition(POSICAO_RECOLHIDO);
                     indexer.setPower(-0.3);
                 }

             }

            // ================================================================
            // TELEMETRIA
            // ================================================,================

         public void telemetriaLancador(Telemetry telemetry){
            telemetry.addData("Motor esquerdo potência", lancadorEsquerdo.getPower());
            telemetry.addData("Motor direito potência ", lancadorDireito.getPower());
            telemetry.addData("Velocidade Esq (ticks/s)", lancadorEsquerdo.getVelocity());
            telemetry.addData("Velocidade Dir (ticks/s)", lancadorDireito.getVelocity());
            telemetry.addData("Servo Posicao", indexer.getVelocity());
            telemetry.update();
        }
    }


