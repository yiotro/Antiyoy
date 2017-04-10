package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.CustomLanguageLoader;

public class RbSetLanguage extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.clear();
        switch (buttonYio.id) {
            case 332: CustomLanguageLoader.setAndSaveLanguage("en_UK"); break; // English
            case 333: CustomLanguageLoader.setAndSaveLanguage("ru_RU"); break; // Russian
            case 334: CustomLanguageLoader.setAndSaveLanguage("uk_UA"); break; // Ukrainian
            case 335: CustomLanguageLoader.setAndSaveLanguage("de_DE"); break; // German
            case 336: CustomLanguageLoader.setAndSaveLanguage("cs_CZ"); break; // Czech
            case 337: CustomLanguageLoader.setAndSaveLanguage("pl_PL"); break; // Polish
            case 338: CustomLanguageLoader.setAndSaveLanguage("it_IT"); break; // Italian
            case 340: CustomLanguageLoader.setAndSaveLanguage("fr_FR"); break; // French
            case 341: CustomLanguageLoader.setAndSaveLanguage("es_MX"); break; // Spanish (Mexico)
            case 342: CustomLanguageLoader.setAndSaveLanguage("sk_SK"); break; // Slovak
        }
        buttonYio.menuControllerYio.createMainMenu();
    }
}
