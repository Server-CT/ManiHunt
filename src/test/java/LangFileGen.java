import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ib67.manhunt.setting.I18n;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LangFileGen {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void gen() throws Exception {
        I18n i18n = new I18n();
        String textToWrite = gson.toJson(i18n);
        Files.write(Paths.get("./build/libs/zh_CN.json"), textToWrite.getBytes(StandardCharsets.UTF_8));
    }
}
