package yio.tro.antiyoy.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import yio.tro.antiyoy.gameplay.DebugFlags;

public class LanguagesManager {
    private static LanguagesManager _instance = null;

    private static final String LANGUAGES_FILE = "languages.xml";
    private static final String DEFAULT_LANGUAGE = "en_UK";

    private HashMap<String, String> _language = null;
    private String _languageName = null;


    private LanguagesManager() {
        // Create language map
        _language = new HashMap<>();

        // Try to load system language
        // If it fails, fallback to default language
        _languageName = java.util.Locale.getDefault().toString();
        if (!loadLanguage(_languageName)) {
            loadLanguage(DEFAULT_LANGUAGE);
            _languageName = DEFAULT_LANGUAGE;
        }
    }


    public static void initialize() {
        _instance = null;
    }


    public static LanguagesManager getInstance() {
        if (_instance == null) {
            _instance = new LanguagesManager();
        }

        return _instance;
    }


    public String getLanguage() {
        return _languageName;
    }


    public void setLanguage(String langName) {
        loadLanguage(langName);
        _languageName = langName;
    }


    public String getString(String key) {
        if (DebugFlags.forceKeys) return key;

        String string;

        if (_language != null) {
            // Look for string in selected language
            string = _language.get(key);

            if (string != null) {
                return string;
            }
        }

        // Key not found, return the key itself
        return key;
    }


    public String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }


    public ArrayList<LanguageChooseItem> getChooseListItems() {
        ArrayList<LanguageChooseItem> result = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            FileHandle fileHandle = Gdx.files.internal(LANGUAGES_FILE);
            Document doc = db.parse(fileHandle.read());

            Element root = doc.getDocumentElement();

            NodeList languages = root.getElementsByTagName("language");
            int numLanguages = languages.getLength();

            for (int i = 0; i < numLanguages; i++) {
                Node language = languages.item(i);

                LanguageChooseItem chooseItem = new LanguageChooseItem();
                chooseItem.name = language.getAttributes().getNamedItem("name").getTextContent();
                chooseItem.title = language.getAttributes().getNamedItem("title").getTextContent();
                chooseItem.author = language.getAttributes().getNamedItem("author").getTextContent();

                result.add(chooseItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public boolean loadLanguage(String languageName) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            FileHandle fileHandle = Gdx.files.internal(LANGUAGES_FILE);
            Document doc = db.parse(fileHandle.read());

            Element root = doc.getDocumentElement();

            NodeList languages = root.getElementsByTagName("language");
            int numLanguages = languages.getLength();

            for (int i = 0; i < numLanguages; ++i) {
                Node language = languages.item(i);

                Node secondName = language.getAttributes().getNamedItem("second_name");
                if (    language.getAttributes().getNamedItem("name").getTextContent().equals(languageName) ||
                        (secondName != null && secondName.getTextContent().equals(languageName))) {
                    _language.clear();
                    Element languageElement = (Element) language;
                    NodeList strings = languageElement.getElementsByTagName("string");
                    int numStrings = strings.getLength();

                    for (int j = 0; j < numStrings; ++j) {
                        NamedNodeMap attributes = strings.item(j).getAttributes();
                        String key = attributes.getNamedItem("key").getTextContent();
                        String value = attributes.getNamedItem("value").getTextContent();
                        value = value.replace("<br />", "\n");
                        _language.put(key, value);
                    }

                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading languages file " + LANGUAGES_FILE);
            return false;
        }

        return false;
    }
}
