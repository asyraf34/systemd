package engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import engine.DrawManager.SpriteType;

/**
 * Manages files used in the application.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class FileManager {
    /**
     * Singleton instance of the class.
     */
    private static FileManager instance;
    /**
     * Application logger.
     */
    private static Logger logger;

    // coins.csv file to save changes about coin content
    private static final String COIN_FILENAME = "coins.csv";

    /**
     * private constructor.
     */
    private FileManager() {
        logger = Core.getLogger();
    }

    /**
     * Returns shared instance of FileManager.
     *
     * @return Shared instance of FileManager.
     */
    protected static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    /**
     * Loads sprites from disk.
     *
     * @param spriteMap
     *            Mapping of sprite type and empty boolean matrix that will
     *            contain the image.
     * @throws IOException
     *             In case of loading problems.
     */
    public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap)
            throws IOException {
        InputStream inputStream = null;

        try {
            inputStream = DrawManager.class.getClassLoader()
                    .getResourceAsStream("graphics");
            char c;

            // Sprite loading.
            for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
                    .entrySet()) {
                for (int i = 0; i < sprite.getValue().length; i++)
                    for (int j = 0; j < sprite.getValue()[i].length; j++) {
                        do
                            c = (char) inputStream.read();
                        while (c != '0' && c != '1');

                        if (c == '1')
                            sprite.getValue()[i][j] = true;
                        else
                            sprite.getValue()[i][j] = false;
                    }
                logger.fine("Sprite " + sprite.getKey() + " loaded.");
            }
            if (inputStream != null)
                inputStream.close();
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Loads a font of a given size.
     *
     * @param size
     *            Point size of the font.
     * @return New font.
     * @throws IOException
     *             In case of loading problems.
     * @throws FontFormatException
     *             In case of incorrect font format.
     */
    public Font loadFont(final float size) throws IOException,
            FontFormatException {
        InputStream inputStream = null;
        Font font;

        try {
            // Font loading.
            inputStream = FileManager.class.getClassLoader()
                    .getResourceAsStream("font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
                    size);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return font;
    }

    /**
     * Returns the filepath
     *
     * @param fileName
     *      file to get path
     * @return full file path
     * @throws IOException
     *      In case of loading problems
     * */
    private static String getFilePath(String fileName) throws IOException {
        String filePath = System.getProperty("user.dir");
        filePath += File.separator + "res" + File.separator + fileName;
        return filePath;
    }

    /**
     * Returns the application default scores if there is no user high scores
     * file.
     *
     * @return Default high scores.
     * @throws IOException
     *             In case of loading problems.
     */
    private List<Score> loadDefaultHighScores() throws IOException {
        List<Score> highScores = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader reader;

        try {
            inputStream = FileManager.class.getClassLoader().getResourceAsStream("1Pscores.csv");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // except first line
            reader.readLine();
            String input;
            while ((input = reader.readLine()) != null) {
                String[] pair = input.split(",");
                String name = pair[0], score = pair[1];
                String mode = pair[2];
                Score highScore = new Score(name, Integer.parseInt(score), mode);
                highScores.add(highScore);
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return highScores;
    }

    /**
     * Loads high scores from file, and returns a sorted list of pairs score -
     * value.
     * @param mode
     *      get game mode 1P/2P.
     * @return Sorted list of scores - players.
     * @throws IOException
     *             In case of loading problems.
     */
    public List<Score> loadHighScores(String mode) throws IOException {
        List<Score> highScores = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String scoresPath = getFilePath(mode+"scores.csv");

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            logger.info("Loading user high scores.");
            // except first line
            bufferedReader.readLine();
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] pair = input.split(",");
                String name = pair[0], score = pair[1];
                Score highScore = new Score(name, Integer.parseInt(score), mode);
                highScores.add(highScore);
            }
        } catch (FileNotFoundException e) {
            // loads default if there's no user scores.
            logger.info("Loading default high scores.");
            highScores = loadDefaultHighScores();
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }

        Collections.sort(highScores);
        return highScores;
    }

    /**
     * Saves user high scores to disk.
     *
     * @param highScores
     *            High scores to save.
     * @param mode
     *            get game mode 1P/2P.
     *
     * @throws IOException
     *             In case of loading problems.
     */
    public void saveHighScores(final List<Score> highScores, String mode) throws IOException {
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String scoresPath = getFilePath(mode+"scores.csv");

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists())
                scoresFile.createNewFile();

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            logger.info("Saving user high scores.");
            bufferedWriter.write("player,score");
            bufferedWriter.newLine();

            for(Score score : highScores) {
                bufferedWriter.write(score.getName() + "," + score.getScore());
                bufferedWriter.newLine();
            }

        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }
    }

    /**
     * Loads the coin count from the coins.csv file.
     *
     * @return The saved coin count, or 0 if the file is not found or empty.
     * @throws IOException In case of loading problems.
     */
    public int[] loadCoins() throws IOException {
        InputStream inputStream;
        BufferedReader bufferedReader = null;
        // base coins are 0.
        int[] result = {0, 0};
        try {
            String coinsPath = getFilePath(COIN_FILENAME);
            File coinsFile = new File(coinsPath);
            inputStream = new FileInputStream(coinsFile);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            logger.info("Loading coin count from " + COIN_FILENAME + ".");

            // coins.csv Read the first line of the file to get the number of coins
            String line = bufferedReader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                try {
                    // Attempt to convert by extracting only numbers
                    String[] parse = line.split(",");
                    result = new int[parse.length];
                    for(int i = 0; i < parse.length; i++)
                        result[i] = Integer.parseInt(parse[i].trim().replaceAll("[^0-9]", ""));
                    return result;
                } catch (NumberFormatException e) {
                    logger.warning("Coin count line is not a valid number. Returning 0.");
                    return null;
                }
            }

        } catch (FileNotFoundException e) {
            logger.info(COIN_FILENAME + " not found. Returning 0 coins.");
            return result;
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }

        return result;
    }

    /**
     * Saves the current coin count to the coins.csv file.
     *
     * @param coins The total number of coins acquired.
     * @throws IOException In case of saving problems.
     */
    public void saveCoins(final int[] coins) throws IOException {
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String coinsPath = getFilePath(COIN_FILENAME);
            File coinsFile = new File(coinsPath);

            if (!coinsFile.exists())
                coinsFile.createNewFile();

            outputStream = new FileOutputStream(coinsFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            logger.info("Saving new coin count (" + coins + ") to " + COIN_FILENAME + ".");

            for(int coin : coins)
                bufferedWriter.write(Integer.toString(coin)+",");

        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }
    }

    /**
     * Search Achievement list of user
     *
     * @param userName user's name to search.
     * @throws IOException In case of loading problems.
     */
    public List<Boolean> searchAchievementsByName(String userName) throws IOException {
        List<Boolean> achievementList = new ArrayList<Boolean>();

        try {
            String achievementPath = getFilePath("achievement.csv");

            InputStream iStream = new FileInputStream(achievementPath);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));

            bReader.readLine(); // Dump header
            String line;
            boolean flag = false;
            while ((line = bReader.readLine()) != null) {
                String[] playRecord = line.split(",");
                if (playRecord[0].equals(userName)) {
                    flag = true;
                    logger.info("Loading user achievements.");
                    for (int i = 1; i < playRecord.length; i++) {
                        achievementList.add(playRecord[i].equals("1"));
                    }
                    break;
                }
            }
            if (!flag)
                for (int i = 0; i < 5; i++) {
                    logger.info("Loading default achievement.");
                    achievementList.add(false);
                }
        } catch (FileNotFoundException e) {
            logger.info("Loading default achievement.");
            for (int i = 0; i < 5; i++) {
                achievementList.add(false);
            }
        }
        return achievementList;
    }

    /**
     * Unlocks an achievement for the given user.
     *
     * @param userName user's name to search.
     * @param unlockedAchievement A list of booleans representing which achievements
     * @throws IOException In case of loading problems.
     */
    public void unlockAchievement(String userName, List<Boolean> unlockedAchievement) throws IOException {
        List<String[]> records = new ArrayList<>();
        try {
            String achievementPath = getFilePath("achievement.csv");

            InputStream iStream = new FileInputStream(achievementPath);
            BufferedReader bReader = new BufferedReader(
                    new InputStreamReader(iStream, StandardCharsets.UTF_8));

            String line;
            boolean flag = false;
            List<String[]> recorder = new ArrayList<>();
            while ((line = bReader.readLine()) != null) {
                String[] playRecord = line.split(",");
                if (playRecord[0].equals(userName)) {
                    flag = true;
                    logger.info("Achievement has been updated");
                    for (int i = 1; i < playRecord.length; i++) {
                        if (playRecord[i].equals("0") && unlockedAchievement.get(i))
                            playRecord[i] = "1";
                    }
                }
                recorder.add(playRecord);
            }
            if (!flag){
                logger.info("User not found, creating new record.");
                String[] newRecord = new String[unlockedAchievement.size() + 1];
                newRecord[0] = userName;
                for (int i = 0; i < unlockedAchievement.size(); i++)
                    newRecord[i+1] = unlockedAchievement.get(i) ? "1" : "0";
                recorder.add(newRecord);
            }


            OutputStream outStream = new FileOutputStream(achievementPath);
            BufferedWriter bWriter = new BufferedWriter(
                    new OutputStreamWriter(outStream, StandardCharsets.UTF_8));

            for (String[] record : recorder) {
                bWriter.write(String.join(",", record));
                bWriter.newLine();
            }

            bWriter.close();

        } catch (FileNotFoundException e) {
            logger.info("No achievements to save");
        }
    }

    /**
     * Unlocks an achievement for the given user.
     *
     * @param achievement achievement's name to search.
     * @throws IOException In case of loading problems.
     *
     * [2025-10-09] Added in commit: feat: add method to retrieve achievement completer
     */
    public List<String> getAchievementCompleter(Achievement achievement) throws IOException {
        List<String> completer = new ArrayList<String>();
        try {
                String achievementPath = getFilePath("achievement.csv");

                InputStream iStream = new FileInputStream(achievementPath);
                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(iStream, StandardCharsets.UTF_8));

                String line;
                String[] header = bReader.readLine().split(",");
                int idx = -1;
                for(int i = 0; i < header.length; i++){
                    if(header[i].equals(achievement.getName())){
                        idx = i;
                        break;
                    }
                }
                if (idx == -1){
                    logger.info("No such achievement");
                    return completer;
                }
                while ((line = bReader.readLine()) != null) {
                    String[] tmp = line.split(",");
                    if(tmp[idx].equals("1")) completer.add(tmp[0]);
                }
        } catch (IOException e) {
            logger.info("Error reading achievement file, using default users.");
            completer.add("ABC");
            completer.add("CDF");
            completer.add("EFG");
        }
        return completer;
    }
}
