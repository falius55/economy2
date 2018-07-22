package jp.gr.java_conf.falius.economy2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private Stage mStage = null;
    private FXMLLoader mLoader = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mStage = primaryStage;
        // Stage: ウィンドウに対応するクラス
        // Scene: ウィンドウ内の表示領域に対応するクラス。
        //     シーングラフのコンテナで、表示領域のサイズとシーングラフのルートノードを決定
        // SceneGraph: 表示オブジェクトに対応するクラス

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        mLoader = loader;
        BorderPane root = loader.load();
        // シーングラフを作成(Paneをnewするかfxmlから取得するか)
        //        BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("main.fxml"));
        // root.getChildren().add(node)で子ノードを追加
        // ルートノードとサイズ, 背景色をSceneに与える(背景色は任意)
        Scene scene = new Scene(root, 800, 400, Color.WHITE);
        //                    scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("sample");

        primaryStage.show();
    }

    @Override
    public void stop() {
        Object controller = mLoader.getController();
        if (controller instanceof AutoCloseable) {
            try {
                ((AutoCloseable) controller).close();
            } catch (Exception e) {
                log.error("close error", e);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
