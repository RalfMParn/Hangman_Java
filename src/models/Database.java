package models;

import models.datastructures.DataScore;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * See klass tegeleb andmebaasi ühenduse ja "igasuguste" päringutega tabelitest.
 * Alguses on ainult ühenduse jaoks funktsionaalsus
 */
public class Database {
    /**
     * Algselt ühendust pole
     */
    private static Connection connection = null;
    /**
     * Andmebaasi ühenduse string
     */
    private static String databaseUrl;
    /**
     * Loodud mudel
     */
    private static Model model;

    /**
     * Klassi andmebaas konstruktor
     * @param model loodud mudel
     */
    public Database(Model model) {
        this.model = model;
        this.databaseUrl = "jdbc:sqlite:" + model.getDatabaseFile();
        this.selectUniqueCategories();
    }


    /**
     * Loob andmebaasiga ühenduse
     * @return andmebaasi ühenduse
     */
    private static Connection dbConnection() throws SQLException {
        // https://stackoverflow.com/questions/13891006/
        if(connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(databaseUrl);
        return connection;
    }
    static String databaseWord;

    // Valib juhusliku sõna tabelist valitud katergooria all
    public static String newRandomWord() {
        Random rand = new Random();
        int randomId;
        String sql;
        String categorySelection = model.getSelectedCategory();
        // System.out.println(model.getSelectedCategory()); // TEST
        if (model.getSelectedCategory().equals("Hoone")) {
            randomId = rand.nextInt(1,6);  // valib juhusliku numbri 1-6
            sql = "SELECT word FROM words WHERE id = " + randomId + " AND category = '" + categorySelection + "';";
        } else if (model.getSelectedCategory().equals("Elukutse")) {
            randomId = rand.nextInt(6, 10); // valib juhusliku numbri 6-10
            sql = "SELECT word FROM words WHERE id = " + randomId + " AND category = '" + categorySelection + "';";
        } else {
            randomId = rand.nextInt(1, 11); // valib juhusliku numbri 1-10
            sql = "SELECT word FROM words WHERE id = " + randomId + ";";
            // System.out.println("Kõik kategooriad selected"); // TEST
        }
        // System.out.println(randomId); // TEST

        try (Connection connection = dbConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String word = rs.getString("word");
                StringBuilder hiddenWordBuilder = new StringBuilder();
                databaseWord = word;
                //System.out.println(word); // TEST
                for (char letter : word.toCharArray()) { // iga tähe asemel valitud sõnas paneb "_"
                    hiddenWordBuilder.append("_ ");
                }
                String hiddenWord = hiddenWordBuilder.toString();
                connection.close();  // Paneb ühendus databaasiga kinni
                return hiddenWord;
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return "Viga sõna saamisega andmebaasist.";
    }

    // paneb võitja info tabelise vaatamiseks
    public static void insertUserInfo(String playername, String wrongcharacters, int playertimeInSeconds) {
        if (playername != null) {
            String sql = "INSERT INTO scores ( playertime, playername, guessword, wrongcharacters, gametime) VALUES (?, ?, ?, ?, ?)";

            try (Connection connection = dbConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {

                 // paneb paika mis info läheb tabelise
                pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pstmt.setString(2, playername);
                pstmt.setString(3, databaseWord);
                pstmt.setString(4, wrongcharacters);
                pstmt.setInt(5, playertimeInSeconds);

                pstmt.executeUpdate();
                // System.out.println("User info inserted successfully!"); // TEST

            } catch (SQLException e) {
                e.printStackTrace();
                //System.out.println("Failed to insert user info."); // TEST
        }

        }
    }


    public static StringBuilder right_guesses = new StringBuilder();
    public static String displayWord = "_";

    public static boolean checkWord(String chosenWord) {
    chosenWord = chosenWord.toLowerCase();

         if (chosenWord.isEmpty()) {
            return false;  // Saadab "False" kui sõna on tühi
    }

         StringBuilder displayWordBuilder = new StringBuilder();

        if (databaseWord.toLowerCase().contains(chosenWord)) {
            // Paneb õigesti arvatud sõnad "right guesses" listi
            if (right_guesses.isEmpty() || !right_guesses.toString().contains(chosenWord)) {
                right_guesses.append(" ").append(chosenWord);

                for (char letter : databaseWord.toCharArray()) {
                    if (right_guesses.toString().contains(String.valueOf(letter).toLowerCase())) {
                        displayWordBuilder.append(letter).append(" ");
                    } else {
                        displayWordBuilder.append("_ ");
                    }
                }
                displayWord = displayWordBuilder.toString();

            }

            return true;
        } else {
            return false;
        }
    }

    private void selectUniqueCategories() {
        String sql = "SELECT DISTINCT(category) as category FROM words ORDER BY category;";
        List<String> categories = new ArrayList<>();  // Tühi kategooriate list
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String category = rs.getString("category");
                categories.add(category); // Lisa kategooria listi kategooriad (categories)
            }
            categories.add(0, model.getChooseCategory()); // Esimeseks "Kõik kategooriad"
            // System.out.println(categories.toString());  // Test kas kategooriad on olemas
            String[] result = categories.toArray(new String[0]);  // List<String> => String[]
            model.setCmbCategories(result); // Määra kategooriad mudelisse
            connection.close();  // Andmebaasi ühendus sulgeda
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectScores() {
        String sql = "SELECT * FROM scores ORDER BY gametime, playertime DESC, playername;";
        List<DataScore> data = new ArrayList<>();
        try {
            Connection connection = this.dbConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            model.getDataScores().clear(); // Tühjenda mudelis listi sisu

            while (rs.next()) {
                String datetime = rs.getString("playertime");
                LocalDateTime playerTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String playerName = rs.getString("playername");
                String guessWord = rs.getString("guessword");
                String wrongChar = rs.getString("wrongcharacters");
                int timeSeconds = rs.getInt("gametime");
                // System.out.println(datetime + " | " + playerTime);

                // Lisa listi kirje
                data.add(new DataScore(playerTime, playerName, guessWord, wrongChar, timeSeconds));
            }
            model.setDataScores(data); // Muuda andmed mudelis
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getDisplayWord() {
        return displayWord;
    }
}
