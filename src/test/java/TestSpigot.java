import com.github.kevinsawicki.http.HttpRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class TestSpigot {
    @Test
    public void onCITest() throws IOException, InterruptedException {
        if(!System.getProperty("user.name").equals("runner")){
            System.out.println("Not CI User,Skip!");
            return;
        }
        File SPIGOT_ROOT = new File("./spigot");
        SPIGOT_ROOT.mkdir();

        System.out.println("Downloading Spigot...");
        File SPIGOT_JAR = new File("./spigot/spigot.jar");
        HttpRequest.get("https://serverjars.com/api/fetchJar/spigot/1.16").receive(SPIGOT_JAR);
        System.out.println("Setting up...");
        new File("./spigot/eula.txt").createNewFile();
        Files.write(new File("./spigot/eula.txt").toPath(),("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).\n" +
                "#Sat May 15 14:07:13 CST 2021\n" +
                "eula=true\n").getBytes());
        File PLUGIN_DIR=new File("./spigot/plugins");
        File OUR_JAR = new File("./build/libs/ManHunt-1.2-all.jar");
        PLUGIN_DIR.mkdir();
        Files.copy(OUR_JAR.toPath(),new File(PLUGIN_DIR,"ManiHunt.jar").toPath());
        boolean b = new ProcessBuilder().directory(SPIGOT_ROOT).command("java","-jar","spigot.jar").start().waitFor(400, TimeUnit.SECONDS);
      
        boolean a=Files.readAllLines(new File(SPIGOT_ROOT,"logs/latest.log").toPath()).stream().anyMatch(e->e.contains("ManHunt Started! We're waiting for more players."))?false:true;
        if(a){
            throw new RuntimeException("ManiHunt didn't load successfully.");
        }

    }
}
