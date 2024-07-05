package models.datastructures;

/**
 * See klass on andmebaasist äraarvatava sõna jaoks
 *
 * @param id       Unikaalne id tabelist
 * @param word     Äraarvatav sõna
 * @param category Sõna kategooria
 */
public record DataWords(int id, String word, String category) {
    /**
     * Konstruktor
     *
     * @param id       unikaalne id
     * @param word     äraarvatav sõna
     * @param category sõna kategooria
     */
    public DataWords {
    }

    /**
     * Unikaalne id
     *
     * @return sõna unikaalse id
     */
    @Override
    public int id() {
        return id;
    }

    /**
     * Sõna
     *
     * @return tagastab äraarvatava sõna
     */
    @Override
    public String word() {
        return word;
    }

    /**
     * Kategooria
     *
     * @return tagastab sõna kategooria
     */
    @Override
    public String category() {
        return category;
    }
}
