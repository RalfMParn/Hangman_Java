package controllers;

import listeners.*;
import models.Model;
import views.View;
import views.panels.GameBoard;

public class Controller {
    public Controller(Model model, View view) {

        // Comboboxi funktsionaalsus
        view.getSettings().getCmbCategory().addItemListener(new ComboboxChange(model));

        // Uus mäng funktisonaalsus
        view.getSettings().getBtnNewGame().addActionListener(new ButtonNew(model, view));

        // Katkesta funktionaalsus
        view.getGameBoard().getBtnCancel().addActionListener(new ButtonCancel(model, view));

        // Saada funktionaalsus
        view.getGameBoard().getBtnSend().addActionListener(new ButtonSend(model, view));

        // Edetabel funktionaalsus
        view.getSettings().getBtnLeaderboard().addActionListener(new ButtonLeaderboard(model, view));
    }
}
