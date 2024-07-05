package listeners;

import models.Database;
import models.Model;
import views.View;
import views.panels.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonSend implements ActionListener {
        private Model model;
        private View view;
        private Database database;



    public ButtonSend(Model model, View view) {
        this.model = model;
        this.view = view;
        this.database = new Database(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (Database.checkWord(view.getGameBoard().getTxtChar().getText())) {
            // System.out.println("Correct"); // Testimiseks
            view.getGameBoard().setLblResultText(Database.getDisplayWord());
        } else {
            view.gameBoard.setLblImage();
            model.wrongCounter += 1;
            view.gameBoard.setLblError(view.getGameBoard().getTxtChar().getText());

        }
    view.getGameBoard().resetTxtChar(); // Teeb sisestus kast tühjaks
    view.gameBoard.getTxtChar().requestFocusInWindow();

    if (view.gameBoard.imageValue == 12) { // Kui viimane pilt on näitatud siis määng on läbi
        // System.out.println("lost"); // TEST

        view.getGameTimer().setRunning(false);
        view.getGameTimer().stopTime();

        Database.right_guesses.setLength(0);
        Database.displayWord = "_";
        view.showButtons();
        view.gameBoard.imageValue = 0;

    } else if (!Database.getDisplayWord().contains("_")) { // Kui ühtegi "_" ei ole enam sõnas siis mäng on läbi
        // System.out.println("won"); // TEST

        view.getGameTimer().setRunning(false);
        view.getGameTimer().stopTime();

        view.askUserName();

        Database.insertUserInfo(view.getPlayerName(), view.gameBoard.getLblError().getText().substring(15), view.getGameTimer().getPlayedTimeInSeconds());
        database.selectScores();
        view.updateScoresTable();
        // TODO After new info is inserted into the table it should refresh and the info should be visible

        Database.right_guesses.setLength(0);
        Database.displayWord = "_";
        view.showButtons();
        view.gameBoard.imageValue = 0;


    }

    }
}
