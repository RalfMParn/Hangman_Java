package listeners;

import models.Database;
import models.Model;
import views.View;
import views.panels.GameBoard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonLeaderboard implements ActionListener {
        private Model model;
        private View view;



    public ButtonLeaderboard(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        view.switchToLeaderBoardTab(); // lihtsab läheb edetabeli leheküljele


    }
}
