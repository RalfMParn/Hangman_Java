package listeners;

import models.Database;
import models.Model;
import views.View;
import views.panels.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonNew implements ActionListener {
    private Model model;
    private View view;

    public ButtonNew(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // System.out.println("Klikk"); // Test
        view.hideButtons();
        model.wrongCounter = 0;
        view.gameBoard.setLblError("Reset");

        String randomWord = Database.newRandomWord(); // Iga kord kui "Uus mäng" on vajutatud siis paneb uus sõna ette
        view.gameBoard.setLblResultText(randomWord);

        view.gameBoard.setLblImage();
        view.gameBoard.getTxtChar().requestFocusInWindow();

        if(!view.getGameTimer().isRunning()) { // Mängu aeg ei kookse
            view.getGameTimer().setSeconds(0); // sekundid nullida
            view.getGameTimer().setMinutes(0); // minutid nullida
            view.getGameTimer().setRunning(true); // Aeg jooksma
            view.getGameTimer().startTime(); // Käivita aeg
        } else {
            view.getGameTimer().stopTime();
            view.getGameTimer().setRunning(false);

        }
    }
}
