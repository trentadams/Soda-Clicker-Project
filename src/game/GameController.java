package game;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class GameController {
    private int sodaCount = 0;
    private int clickPower = 1;
    private int totalPassiveIncome = 0;
    private int clickUpgradeCost = 50;

    private Label countLabel;
    private Label incomeLabel;

    private VBox layout;
    private Timeline incomeTimer;
    private ProgressBar incomeProgress;

    private String[] passiveNames = {"Cherry Cola", "Lemon-Lime Soda", "Soda Fountains", "Orange Soda", 
    								"Diet Variants", "New Bottling Plants", "Road-side Billboards", "Fortnite Collab", 
    								"Zero Sugar Variants", "Soda Theme Park"};
    
    // Tooltip descriptions for each passive upgrade
    private String[] passiveTooltips = {
        "A classic favorite.",
        "Daring today, aren't we?",
        "Install soda fountains in local restraunts.",
        "Not a fan, but the kids will love it!",
        "Now even grandpa can enjoy your empire.",
        "Build new plants to double your bottling output.",
        "Will certainly boost roadside accidents.",
        "Gen Alpha erupts in applause.",
        "Tastes horrible!",
        "Build a theme park around your new soda empire!"
    };

    private int[] baseIncome = {1, 5, 20, 50, 80, 100, 125, 250, 500, 1000};
    private int[] baseCosts = {100, 500, 2000, 5000, 7500, 10000, 12000, 25000, 45000, 60000};
    private int[] levels = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] costs = {100, 500, 2000, 5000, 7500, 10000, 12000, 25000, 45000, 60000};
    private Button[] passiveUpgradeButtons = new Button[passiveNames.length];

    // System I found online that creates boolean variables to check if a button has been previously revealed
    private boolean[] upgradeRevealed = new boolean[passiveNames.length]; 

    public GameController() {
    	// UI Setup
        countLabel = new Label("Sodas: 0");
        incomeLabel = new Label("Income/sec: 0");
        incomeProgress = new ProgressBar(0);
        incomeProgress.setPrefWidth(200);

        // Load soda image
        ImageView sodaImage = new ImageView(new Image("file:assets/soft_drink.png"));
        sodaImage.setFitHeight(150);
        sodaImage.setPreserveRatio(true);

        // Click logic
        sodaImage.setOnMouseClicked(e -> {
            sodaCount += clickPower;
            updateUI();
            playClickAnimation(sodaImage); // Visual feedback on click
        });

        // Click upgrade
        Button clickUpgradeBtn = new Button(getClickUpgradeText());
        clickUpgradeBtn.setOnAction(e -> {
            if (sodaCount >= clickUpgradeCost) {
                sodaCount -= clickUpgradeCost;
                clickPower++;
                clickUpgradeCost = (int) (clickUpgradeCost * 1.5);
                clickUpgradeBtn.setText(getClickUpgradeText());
                updateUI();
            }
        });

        // Passive upgrades
        VBox upgradesBoxWrapper = new VBox(10);
        upgradesBoxWrapper.getChildren().add(new Label("Upgrades:"));

        GridPane upgradesGrid = new GridPane();
        upgradesGrid.setHgap(15); // Horizontal space between columns
        upgradesGrid.setVgap(10); // Vertical space between rows

        for (int i = 0; i < passiveNames.length; i++) {
            int index = i;
            Button upgradeBtn = new Button(getUpgradeText(index));
            
            // Tooltip setup for each upgrade button
            upgradeBtn.setTooltip(new Tooltip(passiveTooltips[index]));

            passiveUpgradeButtons[i] = upgradeBtn;

            if (i != 0) {
                upgradeBtn.setVisible(false);
                upgradeBtn.setManaged(false);
            } else {
                upgradeRevealed[i] = true;
            }

            upgradeBtn.setOnAction(e -> {
                if (sodaCount >= costs[index]) {
                    sodaCount -= costs[index];
                    levels[index]++;
                    totalPassiveIncome += baseIncome[index];
                    costs[index] = (int) (costs[index] * 1.25);
                    upgradeBtn.setText(getUpgradeText(index));
                    updateUI();
                    startPassiveIncome();

                    if (index + 1 < passiveUpgradeButtons.length && !upgradeRevealed[index + 1]) {
                        upgradeRevealed[index + 1] = true;
                        animateButtonFadeIn(passiveUpgradeButtons[index + 1]);
                    }
                }
            });

            // Add button to grid: 4 upgrades per column
            int row = i % 4;
            int col = i / 4;
            upgradesGrid.add(upgradeBtn, col, row);
        }

        upgradesBoxWrapper.getChildren().add(upgradesGrid);
        layout = new VBox(20, countLabel, incomeLabel, incomeProgress, sodaImage, clickUpgradeBtn, upgradesBoxWrapper);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Image bgImage = new Image("file:assets/background.png");
        BackgroundImage backgroundImage = new BackgroundImage(
            bgImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, true, false)
        );
        layout.setBackground(new Background(backgroundImage));

        setupIncomeTimer();
        startPassiveIncome(); // Ensure income starts even if 0
    }

    public VBox getView() {
        return layout;
    }

    private String getUpgradeText(int index) {
        return passiveNames[index] + " - $" + costs[index] + "   (Owned: " + levels[index] + ")";
    }

    private String getClickUpgradeText() {
        return "Upgrade Click (+1) - $" + clickUpgradeCost;
    }

    // Method to update the labels text
    private void updateUI() {
        countLabel.setText("Cash: $" + sodaCount);
        incomeLabel.setText("Income/sec: $" + totalPassiveIncome);
    }

    private void setupIncomeTimer() {
        incomeTimer = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> {
                sodaCount += totalPassiveIncome;
                updateUI();
                incomeProgress.setProgress(0);
            }),
            new KeyFrame(Duration.seconds(1))
        );
        incomeTimer.setCycleCount(Timeline.INDEFINITE);

        Timeline progressBarTimer = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                double progress = incomeProgress.getProgress() + 0.05;
                incomeProgress.setProgress(Math.min(progress, 1.0));
            })
        );
        progressBarTimer.setCycleCount(Timeline.INDEFINITE);
        progressBarTimer.play();
    }

    private void startPassiveIncome() {
        if (incomeTimer != null && incomeTimer.getStatus() != Timeline.Status.RUNNING) {
            incomeTimer.play();
        }
    }
    
    // Animation to fade in new upgrades
    private void animateButtonFadeIn(Button button) {
        button.setOpacity(0);
        button.setVisible(true);
        button.setManaged(true);

        FadeTransition ft = new FadeTransition(Duration.millis(500), button);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
    
    // The logic for the animation played on click
    // May be tweaked to be repeatable on upgrade buttons
    private void playClickAnimation(ImageView image) {
        ScaleTransition st = new ScaleTransition(Duration.millis(80), image);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
}