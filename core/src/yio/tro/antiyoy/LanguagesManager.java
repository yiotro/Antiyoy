package yio.tro.antiyoy;

import java.util.HashMap;
import javax.xml.parsers.*;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import org.w3c.dom.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class LanguagesManager {
    private static LanguagesManager _instance = null;

    private static final String LANGUAGES_FILE = "languages.xml";
    private static final String DEFAULT_LANGUAGE = "en_UK";

    //private HashMap<String, HashMap<String, String>> _strings = null;
    private HashMap<String, String> _language = null;
    private String _languageName = null;


    private LanguagesManager() {
        // Create language map
        _language = new HashMap<String, String>();

        // Try to load system language
        // If it fails, fallback to default language
        _languageName = java.util.Locale.getDefault().toString();
        if (!loadLanguage(_languageName)) {
            loadLanguage(DEFAULT_LANGUAGE);
            _languageName = DEFAULT_LANGUAGE;
        }
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


    @TargetApi(Build.VERSION_CODES.FROYO)
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

                if (language.getAttributes().getNamedItem("name").getTextContent().equals(languageName)) {
                    _language.clear();
                    Element languageElement = (Element) language;
                    NodeList strings = languageElement.getElementsByTagName("string");
                    int numStrings = strings.getLength();

                    for (int j = 0; j < numStrings; ++j) {
                        NamedNodeMap attributes = strings.item(j).getAttributes();
                        String key = attributes.getNamedItem("key").getTextContent();
                        String value = attributes.getNamedItem("value").getTextContent();
//                        System.out.println(value);
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
