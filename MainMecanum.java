package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "MainMecanum", group = "Linear TesteOp")
public class MainMecanum extends OpMode {

    RefazendoCodigo controla = new RefazendoCodigo();
    AprilTagLocalizacao camera = new AprilTagLocalizacao();
    ColetorMotores coletor = new ColetorMotores();
    LancadorMotores lancador = new LancadorMotores();

    //   ------------------------- PD ----------------------------------- //

    double Kp = 0.03;
    double erro = 0;
    double ultimoErro = 0;
    //Compensador de ângulo
    double alvoX = 0;
    // ===================
    double tolerancia = 5;
    double Kd = 0.0005;
    double tempoAtual = 0;
    double ultimoTempo = 0;

    // ----------------------------------------------------------------- //

    double forward, strafe, rotate, controleColetor, controleLancador, indexerControle;

    @Override
    public void init() {
        controla.init(hardwareMap);
        camera.init(hardwareMap);
        coletor.init(hardwareMap);
        lancador.init(hardwareMap);
        telemetry.addData("Status", "inicializado. Esperando comando");
        telemetry.update();
    }

    @Override
    public void loop() {

        //Drivetrain (Mecanum) gamepad 1
        forward = -gamepad1.left_stick_y;
        strafe = -gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        //Mecanismos (lançador e coletor) gamepad 2
        controleColetor = gamepad2.right_stick_x;
        indexerControle = gamepad2.left_stick_y;
        controleLancador = gamepad2.right_trigger;

        //Inicializar a função de detecção de AprilTags
        camera.aprilTag();

        //Botão de reiniciar o yaw (ângulo do sensor IMU)
        if (gamepad1.options) { // Botão Start/Options do comando
            controla.getImu().resetYaw();
        }

        //Funções do coletor
        coletor.puxar(controleColetor);
        coletor.empurrar(controleColetor);

        //Funções do lançador
        lancador.lancar(controleLancador);
        lancador.indexer(indexerControle);

        if(gamepad1.left_trigger > 0.3){
            if(camera.getResults() != null && camera.getResults().isValid()) {
                erro = camera.getTx() - alvoX;

                if (Math.abs(erro) < tolerancia ) {
                    rotate = 0;

                } else {
                    double P = erro * Kp;

                    tempoAtual = getRuntime();
                    double dt = tempoAtual - ultimoTempo;
                    double D = ((erro - ultimoErro) / dt) * Kd;

                    rotate = Range.clip(P + D, -0.5, 0.5);

                    ultimoErro = erro;
                    ultimoTempo = tempoAtual;
                }
            }else {
                ultimoErro = 0;
                ultimoTempo = getRuntime();
            }
        } else{
            ultimoErro = 0;
           ultimoTempo = getRuntime();
        }



       controla.orientadoAoRobo(forward, strafe, rotate);
        //controla.orientadoAoCampo(forward, strafe, rotate);

        telemetry.addLine("// ============= Gamepads ============== //");
        telemetry.addData("Indo para frente", forward);
        telemetry.addData("Indo para o lado", strafe);
        telemetry.addData("Rotacionando", rotate);

        telemetry.addLine("// ============= Odometria das rodas  ============== //");
        controla.mostrarTelemetria(telemetry);

        /*telemetry.addData("Coletor", controleColetor);
        telemetry.addData("Lançador", controleLancador);
        telemetry.addData("Indexer", indexerControle);*/

        telemetry.addLine("// ============= Sensores ============== //");
        telemetry.addData("Ângulo do IMU", controla.getAnguloGraus());
        camera.mostrarTelemetria(telemetry);
        lancador.telemetriaLancador(telemetry);

        telemetry.addLine("// ============= Debugging ============== //");
        telemetry.addData("erro", erro);
        telemetry.addData("alvo", alvoX);
        telemetry.addData("Tempo discorrido", tempoAtual);
        telemetry.addData("ultimo tempo", ultimoTempo);
        }
    }

