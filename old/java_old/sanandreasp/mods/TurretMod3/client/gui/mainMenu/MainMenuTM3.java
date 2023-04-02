package sanandreasp.mods.TurretMod3.client.gui.mainMenu;
//
//import java.awt.image.BufferedImage;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URI;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import net.aetherteam.mainmenu_api.GuiAetherButton;
//import net.aetherteam.mainmenu_api.MainMenuAPI;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiButtonLanguage;
//import net.minecraft.client.gui.GuiConfirmOpenLink;
//import net.minecraft.client.gui.GuiLanguage;
//import net.minecraft.client.gui.GuiMultiplayer;
//import net.minecraft.client.gui.GuiOptions;
//import net.minecraft.client.gui.GuiScreenOnlineServers;
//import net.minecraft.client.gui.GuiSelectWorld;
//import net.minecraft.client.gui.GuiYesNo;
//import net.minecraft.client.gui.ScaledResolution;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.StringTranslate;
//import net.minecraft.world.demo.DemoWorldServer;
//import net.minecraft.world.storage.ISaveFormat;
//import net.minecraft.world.storage.WorldInfo;
//
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.util.glu.GLU;
//
//import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//
//import net.minecraftforge.fml.client.GuiModList;
//import net.minecraftforge.fml.common.FMLCommonHandler;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import sanandreasp.mods.managers.SAP_LanguageManager;
//
//@SideOnly(Side.CLIENT)
//public class MainMenuTM3 extends net.aetherteam.mainmenu_api.MenuBase {
//	private SAP_LanguageManager langman;
//
//    private static final String[] titlePanoramaPaths = new String[] {
//    	"/mods/TurretMod3/textures/mainmenu/panorama_01.png",
//    	"/mods/TurretMod3/textures/mainmenu/panorama_02.png",
//    	"/mods/TurretMod3/textures/mainmenu/panorama_03.png",
//    	"/mods/TurretMod3/textures/mainmenu/panorama_04.png",
//    	"/mods/TurretMod3/textures/mainmenu/panorama_05.png",
//    	"/mods/TurretMod3/textures/mainmenu/panorama_06.png"
//    };
//
//    private static final String[] tm3Splashes = new String[] {
//    	"Shoot those damn zombies!",
//    	"Base defense activated!",
//    	"A turret is under attack!",
//    	"No more fear for Creeper-Wear!",
//    	"Now with new turrets!",
//    	"Build a turret, get more loot!",
//    	"Now in green!",
//    	"A Forcefield does not protect you from rain!",
//    	"Burn those squids!"
//    };
//
//    /** The RNG used by the Main Menu Screen. */
//    private static final Random rand = new Random();
//
//    /** Counts the number of screen updates. */
//    private float updateCounter = 0.0F;
//
//    /** The splash message. */
//    private String splashText = "missingno";
//    private GuiButton buttonResetDemo;
//
//    /** Timer used to rotate the panorama, increases every tick. */
//    private int panoramaTimer = 0;
//
//    /**
//     * Texture allocated for the current viewport of the main menu's panorama background.
//     */
//    private int viewportTexture;
//    private boolean field_96141_q = true;
//    private static boolean field_96140_r = false;
//    private static boolean field_96139_s = false;
//    private String field_92025_p;
//
//    /** An array of all the paths to the panorama pictures. */
//    public static final String field_96138_a = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
//    private int field_92024_r;
//    private int field_92023_s;
//    private int field_92022_t;
//    private int field_92021_u;
//    private int field_92020_v;
//    private int field_92019_w;
//
//    private GuiButton fmlModButton = null;
//
//    public MainMenuTM3()
//    {
//        if (this.rand.nextInt(2) == 0) {
//        	this.splashText = "\247a" + this.tm3Splashes[this.rand.nextInt(this.tm3Splashes.length)];
//        } else {
//	        BufferedReader bufferedreader = null;
//
//	        try {
//	            ArrayList arraylist = new ArrayList();
//	            bufferedreader = new BufferedReader(new InputStreamReader(MainMenuTM3.class.getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8")));
//	            String s;
//
//	            while ((s = bufferedreader.readLine()) != null) {
//	                s = s.trim();
//
//	                if (s.length() > 0) {
//	                    arraylist.add(s);
//	                }
//	            }
//
//	            do {
//	                this.splashText = (String)arraylist.get(rand.nextInt(arraylist.size()));
//	            }
//	            while (this.splashText.hashCode() == 125780783);
//	        }
//	        catch (IOException ioexception) {
//	            ;
//	        }
//	        finally {
//	            if (bufferedreader != null) {
//	                try {
//	                    bufferedreader.close();
//	                }
//	                catch (IOException ioexception1) {
//	                    ;
//	                }
//	            }
//	        }
//        }
//
//        this.updateCounter = rand.nextFloat();
//    }
//
//    /**
//     * Called from the main game loop to update the screen.
//     */
//    public void updateScreen()
//    {
//        ++this.panoramaTimer;
//    }
//
//    /**
//     * Returns true if this GUI should pause the game when it is displayed in single-player
//     */
//    public boolean doesGuiPauseGame()
//    {
//        return false;
//    }
//
//    /**
//     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
//     */
//    protected void keyTyped(char par1, int par2) {}
//
//    /**
//     * Adds the buttons (and other controls) to the screen in question.
//     */
//    public void initGui()
//    {
//    	super.initGui();
//
//        this.viewportTexture = this.mc.renderEngine.allocateAndSetupTexture(new BufferedImage(256, 256, 2));
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//
//        if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
//        {
//            this.splashText = "Happy birthday, ez!";
//        }
//        else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
//        {
//            this.splashText = "Happy birthday, Notch!";
//        }
//        else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
//        {
//            this.splashText = "Merry X-mas!";
//        }
//        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
//        {
//            this.splashText = "Happy new year!";
//        }
//        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
//        {
//            this.splashText = "OOoooOOOoooo! Spooky!";
//        }
//        else if (calendar.get(2) + 1 == 8 && calendar.get(5) == 26)
//        {
//        	this.splashText = "\247aHappy birthday, SanAndreasP!";
//        }
//
//        StringTranslate stringtranslate = StringTranslate.getInstance();
//        int i = this.height / 4 + 48;
//
//        if (this.mc.isDemo())
//        {
//            this.addDemoButtons(i, 24, stringtranslate);
//        }
//        else
//        {
//            this.addSingleplayerMultiplayerButtons(i, 24, stringtranslate);
//        }
//
//        fmlModButton = new GuiButtonMainMenu(6, this.width / 2 - 100, i + 48, "Mods", 0x00F000);
//        this.buttonList.add(fmlModButton);
//
//        this.func_96137_a(stringtranslate, i, 24);
//
//        if (this.mc.hideQuitButton)
//        {
//            this.buttonList.add(new GuiButtonMainMenu(0, this.width / 2 - 100, i + 72, stringtranslate.translateKey("menu.options"), 0x00F000));
//        }
//        else
//        {
//            this.buttonList.add(new GuiButtonMainMenu(0, this.width / 2 - 100, i + 72 + 12, 98, 20, stringtranslate.translateKey("menu.options"), 0x00F000));
//            this.buttonList.add(new GuiButtonMainMenu(4, this.width / 2 + 2, i + 72 + 12, 98, 20, stringtranslate.translateKey("menu.quit"), 0x00F000));
//        }
//
//        this.buttonList.add(new GuiButtonLanguage(5, width - 48, 4));
//        this.field_92025_p = "";
//        String s = System.getProperty("os_architecture");
//        String s1 = System.getProperty("java_version");
//
//        if ("ppc".equalsIgnoreCase(s))
//        {
//            this.field_92025_p = "" + TextFormatting.BOLD + "Notice!" + TextFormatting.RESET + " PowerPC compatibility will be dropped in Minecraft 1.6";
//        }
//        else if (s1 != null && s1.startsWith("1.5"))
//        {
//            this.field_92025_p = "" + TextFormatting.BOLD + "Notice!" + TextFormatting.RESET + " Java 1.5 compatibility will be dropped in Minecraft 1.6";
//        }
//
//        this.field_92023_s = this.fontRenderer.getStringWidth(this.field_92025_p);
//        this.field_92024_r = this.fontRenderer.getStringWidth(field_96138_a);
//        int j = Math.max(this.field_92023_s, this.field_92024_r);
//        this.field_92022_t = (this.width - j) / 2;
//        this.field_92021_u = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
//        this.field_92020_v = this.field_92022_t + j;
//        this.field_92019_w = this.field_92021_u + 24;
//    }
//
//    private void func_96137_a(StringTranslate par1StringTranslate, int par2, int par3)
//    {
//        if (this.field_96141_q)
//        {
//            if (!field_96140_r)
//            {
//                field_96140_r = true;
//                //(new MenuThreadTitleScreen(this, par1StringTranslate, par2, par3)).start();
//            }
//            else if (field_96139_s)
//            {
//                this.func_98060_b(par1StringTranslate, par2, par3);
//            }
//        }
//    }
//
//    private void func_98060_b(StringTranslate par1StringTranslate, int par2, int par3)
//    {
//        //If Minecraft Realms is enabled, halve the size of both buttons and set them next to eachother.
//        //fmlModButton.width = 98;
//        fmlModButton.xPosition = this.width / 2 + 2;
//
//        GuiButton realmButton = new GuiButtonMainMenu(3, this.width / 2 - 100, par2 + par3 * 2, par1StringTranslate.translateKey("menu.online"), 0x00F000);
//        //realmButton.width = 98;
//        realmButton.xPosition = this.width / 2 - 100;
//        this.buttonList.add(realmButton);
//    }
//
//    /**
//     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
//     */
//    private void addSingleplayerMultiplayerButtons(int par1, int par2, StringTranslate par3StringTranslate)
//    {
//        this.buttonList.add(new GuiButtonMainMenu(1, this.width / 2 - 100, par1, par3StringTranslate.translateKey("menu.singleplayer"), 0x00F000));
//        this.buttonList.add(new GuiButtonMainMenu(2, this.width / 2 - 100, par1 + par2 * 1, par3StringTranslate.translateKey("menu.multiplayer"), 0x00F000));
//    }
//
//    /**
//     * Adds Demo buttons on Main Menu for players who are playing Demo.
//     */
//    private void addDemoButtons(int par1, int par2, StringTranslate par3StringTranslate)
//    {
//        this.buttonList.add(new GuiButtonMainMenu(11, this.width / 2 - 100, par1, par3StringTranslate.translateKey("menu.playdemo"), 0x00F000));
//        this.buttonList.add(this.buttonResetDemo = new GuiButtonMainMenu(12, this.width / 2 - 100, par1 + par2 * 1, par3StringTranslate.translateKey("menu.resetdemo"), 0x00F000));
//        ISaveFormat isaveformat = this.mc.getSaveLoader();
//        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
//
//        if (worldinfo == null)
//        {
//            this.buttonResetDemo.enabled = false;
//        }
//    }
//
//    /**
//     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
//     */
//    protected void actionPerformed(GuiButton par1GuiButton)
//    {
//        if (par1GuiButton.id == 0)
//        {
//            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
//        }
//
//        if (par1GuiButton.id == 5)
//        {
//            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings));
//        }
//
//        if (par1GuiButton.id == 1)
//        {
//            this.mc.displayGuiScreen(new GuiSelectWorld(this));
//        }
//
//        if (par1GuiButton.id == 2)
//        {
//            this.mc.displayGuiScreen(new GuiMultiplayer(this));
//        }
//
//        if (par1GuiButton.id == 3)
//        {
//            this.mc.displayGuiScreen(new GuiScreenOnlineServers(this));
//        }
//
//        if (par1GuiButton.id == 4)
//        {
//            this.mc.shutdown();
//        }
//
//        if (par1GuiButton.id == 6)
//        {
//            this.mc.displayGuiScreen(new GuiModList(this));
//        }
//
//        if (par1GuiButton.id == 11)
//        {
//            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
//        }
//
//        if (par1GuiButton.id == 12)
//        {
//            ISaveFormat isaveformat = this.mc.getSaveLoader();
//            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
//
//            if (worldinfo != null)
//            {
//                GuiYesNo guiyesno = GuiSelectWorld.getDeleteWorldScreen(this, worldinfo.getWorldName(), 12);
//                this.mc.displayGuiScreen(guiyesno);
//            }
//        }
//    }
//
//    public void confirmClicked(boolean par1, int par2)
//    {
//        if (par1 && par2 == 12)
//        {
//            ISaveFormat isaveformat = this.mc.getSaveLoader();
//            isaveformat.flushCache();
//            isaveformat.deleteWorldDirectory("Demo_World");
//            this.mc.displayGuiScreen(this);
//        }
//        else if (par2 == 13)
//        {
//            if (par1)
//            {
//                try
//                {
//                    Class oclass = Class.forName("java.awt.Desktop");
//                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
//                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI("http://tinyurl.com/javappc")});
//                }
//                catch (Throwable throwable)
//                {
//                    throwable.printStackTrace();
//                }
//            }
//
//            this.mc.displayGuiScreen(this);
//        }
//    }
//
//    /**
//     * Draws the main menu panorama
//     */
//    private void drawPanorama(int par1, int par2, float par3)
//    {
//        Tessellator tessellator = Tessellator.instance;
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glPushMatrix();
//        GL11.glLoadIdentity();
//        GLU.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glPushMatrix();
//        GL11.glLoadIdentity();
//        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_ALPHA_TEST);
//        GL11.glDisable(GL11.GL_CULL_FACE);
//        GL11.glDepthMask(false);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        byte b0 = 8;
//
//        for (int k = 0; k < b0 * b0; ++k)
//        {
//            GL11.glPushMatrix();
//            float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
//            float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
//            float f3 = 0.0F;
//            GL11.glTranslatef(f1, f2, f3);
//            GL11.glRotatef(MathHelper.sin(((float)this.panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
//            GL11.glRotatef(-((float)this.panoramaTimer + par3) * 0.1F, 0.0F, 1.0F, 0.0F);
//
//            for (int l = 0; l < 6; ++l)
//            {
//                GL11.glPushMatrix();
//
//                if (l == 1)
//                {
//                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
//                }
//
//                if (l == 2)
//                {
//                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//                }
//
//                if (l == 3)
//                {
//                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
//                }
//
//                if (l == 4)
//                {
//                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
//                }
//
//                if (l == 5)
//                {
//                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
//                }
//
//                this.mc.func_110434_K().func_110577_a(titlePanoramaPaths[l]);
//                tessellator.startDrawingQuads();
//                tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
//                float f4 = 0.0F;
//                tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + f4), (double)(0.0F + f4));
//                tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - f4), (double)(0.0F + f4));
//                tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - f4), (double)(1.0F - f4));
//                tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + f4), (double)(1.0F - f4));
//                tessellator.draw();
//                GL11.glPopMatrix();
//            }
//
//            GL11.glPopMatrix();
//            GL11.glColorMask(true, true, true, false);
//        }
//
//        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
//        GL11.glColorMask(true, true, true, true);
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glPopMatrix();
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glPopMatrix();
//        GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//    }
//
//    /**
//     * Rotate and blurs the skybox view in the main menu
//     */
//    private void rotateAndBlurSkybox(float par1)
//    {
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.viewportTexture);
//        this.mc.renderEngine.resetBoundTexture();
//        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glColorMask(true, true, true, false);
//        Tessellator tessellator = Tessellator.instance;
//        tessellator.startDrawingQuads();
//        byte b0 = 1;
//
//        for (int i = 0; i < b0; ++i)
//        {
//            tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(i + 1));
//            int j = this.width;
//            int k = this.height;
//            float f1 = (float)(i - b0 / 2) / 256.0F;
//            tessellator.addVertexWithUV((double)j, (double)k, (double)this.zLevel, (double)(0.0F + f1), 0.0D);
//            tessellator.addVertexWithUV((double)j, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 0.0D);
//            tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 1.0D);
//            tessellator.addVertexWithUV(0.0D, (double)k, (double)this.zLevel, (double)(0.0F + f1), 1.0D);
//        }
//
//        tessellator.draw();
//        GL11.glColorMask(true, true, true, true);
//        this.mc.renderEngine.resetBoundTexture();
//    }
//
//    /**
//     * Renders the skybox in the main menu
//     */
//    private void renderSkybox(int par1, int par2, float par3)
//    {
//        GL11.glViewport(0, 0, 256, 256);
//        this.drawPanorama(par1, par2, par3);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        this.rotateAndBlurSkybox(par3);
//        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
//        Tessellator tessellator = Tessellator.instance;
//        tessellator.startDrawingQuads();
//        float f1 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
//        float f2 = (float)this.height * f1 / 256.0F;
//        float f3 = (float)this.width * f1 / 256.0F;
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
//        int k = this.width;
//        int l = this.height;
//        tessellator.addVertexWithUV(0.0D, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F + f3));
//        tessellator.addVertexWithUV((double)k, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F - f3));
//        tessellator.addVertexWithUV((double)k, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F - f3));
//        tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F + f3));
//        tessellator.draw();
//    }
//
//    /**
//     * Draws the screen and all the components in it.
//     */
//    public void drawScreen(int par1, int par2, float par3)
//    {
//        this.renderSkybox(par1, par2, par3);
//        Tessellator tessellator = Tessellator.instance;
//        short short1 = 274;
//        int k = this.width / 2 - short1 / 2;
//        byte b0 = 30;
//        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
//        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
//        this.mc.func_110434_K().func_110577_a("/mods/TurretMod3/textures/mainmenu/mclogo.png");
//        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        if ((double)this.updateCounter < 1.0E-4D)
//        {
//            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 99, 44);
//            this.drawTexturedModalRect(k + 99, b0 + 0, 129, 0, 27, 44);
//            this.drawTexturedModalRect(k + 99 + 26, b0 + 0, 126, 0, 3, 44);
//            this.drawTexturedModalRect(k + 99 + 26 + 3, b0 + 0, 99, 0, 26, 44);
//            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
//        }
//        else
//        {
//            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 155, 44);
//            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
//        }
//
//        tessellator.setColorOpaque_I(16777215);
//        GL11.glPushMatrix();
//        GL11.glTranslatef((float)((this.width) / 2), 66.0F, 0.0F);
////        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
//        float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
//        f1 = f1 * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
//        GL11.glScalef(f1, f1, f1);
//        this.drawCenteredString(this.fontRenderer, this.splashText, 0, 0, 16776960);
//        GL11.glPopMatrix();
//        String s = "Minecraft 1.5.1";
//
//        if (this.mc.isDemo())
//        {
//            s = s + " Demo";
//        }
//
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings());
//        for (int i = 0; i < brandings.size(); i++)
//        {
//            String brd = brandings.get(i);
//            if (!Strings.isNullOrEmpty(brd))
//            {
//                this.drawString(this.fontRenderer, brd, 2, this.height - ( 10 + i * (this.fontRenderer.FONT_HEIGHT + 1)), 0x80FFFFFF);
//            }
//        }
//
//        String s1 = "Copyright Mojang AB. Do not distribute!";
//        this.drawString(this.fontRenderer, s1, this.width - this.fontRenderer.getStringWidth(s1) - 2, this.height - 10, 0x80FFFFFF);
//
//        if (this.field_92025_p != null && this.field_92025_p.length() > 0)
//        {
//            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
//            this.drawString(this.fontRenderer, this.field_92025_p, this.field_92022_t, this.field_92021_u, 0x80FFFFFF);
//            this.drawString(this.fontRenderer, field_96138_a, (this.width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12, 0x80FFFFFF);
//        }
//        GL11.glDisable(GL11.GL_BLEND);
//
//        super.drawScreen(par1, par2, par3);
//    }
//
//    /**
//     * Called when the mouse is clicked.
//     */
//    protected void mouseClicked(int par1, int par2, int par3)
//    {
//        super.mouseClicked(par1, par2, par3);
//
//        if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t && par1 <= this.field_92020_v && par2 >= this.field_92021_u && par2 <= this.field_92019_w)
//        {
//            GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, "http://tinyurl.com/javappc", 13);
//            guiconfirmopenlink.func_92026_h();
//            this.mc.displayGuiScreen(guiconfirmopenlink);
//        }
//    }
//
//    static Minecraft func_98058_a(MainMenuTM3 field_98132_d)
//    {
//        return field_98132_d.mc;
//    }
//
//    static void func_98061_a(MainMenuTM3 par0GuiMainMenu, StringTranslate par1StringTranslate, int par2, int par3)
//    {
//        par0GuiMainMenu.func_98060_b(par1StringTranslate, par2, par3);
//    }
//
//    static boolean func_98059_a(boolean par0)
//    {
//        field_96139_s = par0;
//        return par0;
//    }
//
//	public int getListButtonX()
//	{
//		Minecraft mc = Minecraft.getMinecraft();
//		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
//        int width = scaledresolution.getScaledWidth();
//        int height = scaledresolution.getScaledHeight();
//
//		return 5;
//	}
//
//	public int getListButtonY()
//	{
//		Minecraft mc = Minecraft.getMinecraft();
//		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
//        int width = scaledresolution.getScaledWidth();
//        int height = scaledresolution.getScaledHeight();
//
//		return 4;
//	}
//
//	public int getJukeboxButtonX()
//	{
//		return width - 24;
//	}
//
//	public int getJukeboxButtonY()
//	{
//		return 4;
//	}
//
//	public String getName()
//	{
//		return "Turret Mod";
//	}
//
//	public String getVersion()
//	{
//		return "3.0.0";
//	}
//
//	public String getIconPath()
//	{
//		return "/net/aetherteam/mainmenu_api/icons/minecraft.png";
//	}
//
//	@Override
//	public String getMusicFileName() {
//		return "Tidal Force";
//	}
//}
