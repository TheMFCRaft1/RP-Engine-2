package me.themfcraft.rpengine;

import com.mojang.logging.LogUtils;
import me.themfcraft.rpengine.character.CharacterManager;
import me.themfcraft.rpengine.chat.ChatManager;
import me.themfcraft.rpengine.data.DatabaseManager;
import me.themfcraft.rpengine.economy.EconomyManager;
import me.themfcraft.rpengine.economy.Shop;
import me.themfcraft.rpengine.economy.ShopItem;
import me.themfcraft.rpengine.economy.ShopManager;
import me.themfcraft.rpengine.interaction.InteractionManager;
import me.themfcraft.rpengine.interaction.KeyManager;
import me.themfcraft.rpengine.job.Job;
import me.themfcraft.rpengine.job.JobManager;
import me.themfcraft.rpengine.job.Rank;
import me.themfcraft.rpengine.economy.EconomyCommands;
import me.themfcraft.rpengine.economy.CorporateCommands;
import me.themfcraft.rpengine.economy.CorporateManager;
import me.themfcraft.rpengine.illegal.IllegalCommands;
import me.themfcraft.rpengine.illegal.IllegalManager;
import me.themfcraft.rpengine.attributes.AttributeCommands;
import me.themfcraft.rpengine.attributes.AttributeManager;
import me.themfcraft.rpengine.chat.ChatCommands;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.registry.ItemRegistry;
import me.themfcraft.rpengine.registry.BlockRegistry;
import me.themfcraft.rpengine.medical.MedicalManager;
import me.themfcraft.rpengine.chat.RadioManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

@Mod(RPEngine.MODID)
public class RPEngine {

    public static final String MODID = "rpengine";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static CharacterManager characterManager;
    private static EconomyManager economyManager;
    private static DatabaseManager databaseManager;
    private static ChatManager chatManager;
    private static JobManager jobManager;
    private static InteractionManager interactionManager;
    private static KeyManager keyManager;
    private static ShopManager shopManager;
    private static RadioManager radioManager;
    private static CorporateManager corporateManager;
    private static IllegalManager illegalManager;
    private static AttributeManager attributeManager;
    private static MedicalManager medicalManager;

    public RPEngine() {
        File dataDir = new File("mods/rpengine");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        databaseManager = new DatabaseManager(new File(dataDir, "database.db").getAbsolutePath());
        databaseManager.connect();

        characterManager = new CharacterManager();
        economyManager = new EconomyManager();
        chatManager = new ChatManager();
        jobManager = new JobManager();
        interactionManager = new InteractionManager();
        keyManager = new KeyManager();
        shopManager = new ShopManager();
        radioManager = new RadioManager();
        corporateManager = new CorporateManager();
        illegalManager = new IllegalManager();
        attributeManager = new AttributeManager();
        medicalManager = new MedicalManager();

        registerDefaultJobs();
        registerDefaultShops();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        LOGGER.info("RP Engine 2 initialized!");
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        RPCommands.register(event.getDispatcher());
        EconomyCommands.register(event.getDispatcher());
        ChatCommands.register(event.getDispatcher());
        CorporateCommands.register(event.getDispatcher());
        IllegalCommands.register(event.getDispatcher());
        AttributeCommands.register(event.getDispatcher());
    }

    public static CharacterManager getCharacterManager() {
        return characterManager;
    }

    public static EconomyManager getEconomyManager() {
        return economyManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static ChatManager getChatManager() {
        return chatManager;
    }

    public static JobManager getJobManager() {
        return jobManager;
    }

    public static InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public static KeyManager getKeyManager() {
        return keyManager;
    }

    public static ShopManager getShopManager() {
        return shopManager;
    }

    public static RadioManager getRadioManager() {
        return radioManager;
    }

    public static CorporateManager getCorporateManager() {
        return corporateManager;
    }

    public static IllegalManager getIllegalManager() {
        return illegalManager;
    }

    public static AttributeManager getAttributeManager() {
        return attributeManager;
    }

    public static MedicalManager getMedicalManager() {
        return medicalManager;
    }

    private void registerDefaultJobs() {
        Map<String, Rank> policeRanks = Map.of(
                "recruit", new Rank("recruit", "Rekrut", 500),
                "officer", new Rank("officer", "Polizist", 800),
                "chief", new Rank("chief", "Polizeichef", 1200)
        );
        jobManager.registerJob(new Job("police", "Polizei", policeRanks));

        Map<String, Rank> medicRanks = Map.of(
                "recruit", new Rank("recruit", "Sanitäter-Azubi", 550),
                "medic", new Rank("medic", "Notarzt", 900)
        );
        jobManager.registerJob(new Job("medic", "Rettungsdienst", medicRanks));
    }

    private void registerDefaultShops() {
        Shop market = new Shop("market", "Supermarkt");
        market.addItem(new ShopItem(new ItemStack(net.minecraft.world.item.Items.BREAD), 2.50, true));
        market.addItem(new ShopItem(new ItemStack(net.minecraft.world.item.Items.APPLE), 1.50, true));
        market.addItem(new ShopItem(new ItemStack(net.minecraft.world.item.Items.COOKED_BEEF), 12.00, true));
        shopManager.registerShop(market);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        LOGGER.info("RP Engine 2 common setup.");
        NetworkHandler.register();
    }
}
