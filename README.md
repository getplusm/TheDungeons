![Overview Image](https://cdn.discordapp.com/attachments/810884357543165963/1130614435854159872/overview.png)
[Discord](https://discord.gg/ajnPb3fdKq) | [Documentation](https://github.com/getplusm/Dungeons/wiki) | [Donate](https://boosty.to/p1azmer)

*Imagine a world in Minecraft where every step uncovers new riddles and mysteries.*

*This plugin provides an endless stream of diverse and captivating dungeons. Never know what awaits you around the corner or behind a mysterious door. With its help, you can create a multitude of different types of dungeons: from ancient underground mazes to eerie castles, from caverns with unexplored treasures to grim dungeons with monsters ready to test your courage.*

![Requirements Image](https://cdn.discordapp.com/attachments/810884357543165963/1130609855468679228/requirements.png)

- **Spigot 1.17** or higher
- **Java 16** or higher
- [PLAZMER-ENGINE](https://github.com/getplusm/Engine)

![Dependencies Image](https://cdn.discordapp.com/attachments/810884357543165963/1130617182359928933/depend.png)
**required**
- [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays/files) or [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-20-1-papi-support-no-dependencies.96927/)
- [WorldEdit](https://dev.bukkit.org/projects/worldedit/files) or [FAWE](https://www.spigotmc.org/resources/fastasyncworldedit.13932/)
**support**
- WorldGuard
- MMOItems
- ItemsAdder
- GriefDefender
- GriefPrevention
- KingdomX
- MythicMobs and others with commands
- PartyAndFriends (Party system)

The plugin creates a specified and configured dungeon based on your settings in the configuration file. It places the dungeon schematic at a random location in the world, checking if the location is within a region and if the block is not lava or water. If the plugin fails to find a suitable location after 5 attempts, it sets the dungeon stage to a waiting mode and it will only spawn after the specified timer expires (Refresh in the cfg). The dungeon schematic must include a chest block, which you will specify in the configuration, otherwise, the plugin won't be able to process the schematic.

![Features Image](https://cdn.discordapp.com/attachments/810884357543165963/1130614437506715738/features.png)

**Party System**
- You can use a third-party plugin for the party system so that players can only enter the dungeon in a party.

*Disclaimer*: If your party plugin is not supported, please inform the developer!

**Commands**
- You can add an unlimited number of commands for the two dungeon stages:
  - Open Stage
  - Close Stage

**Custom Mobs**
- You can add your own custom mobs for each dungeon. Mobs have customization options:
  - Potions
  - Entity type
  - Rider
  - Silent mode
  - Name and customize its display
  - Reward for a kill and the chance of the reward falling out
  - Equipment

**Chest Items**
- You can create multiple rewards for chests. You can specify that all chests in a dungeon should be counted as one or have different rewards. The plugin supports NBT-TAG's of items, allowing you to add any item from third-party plugins. Items have customization settings:
  - Chance - allows you to set the chance of it going into the chest
  - Amount - allows you to set how much of the item will be added from min to max value

**Effects**
- You can add any effect for players who will be within a radius of 15 blocks

**Access Keys**
- You can create keys to access the dungeon

**Any Blocks**
- You can specify any block as a chest to open. All this can be done using the dungeon config or the command /dungeon editor

**Opening Types**
- At the moment there are two types of discoveries:
  - Click - Players need to click on the chest so that it starts to open
  - Timer - The dungeon will open itself after the time expires

**Any Worlds**
- You can create a dungeon for any world and customize it as you like

**Fully Customizable Text**
- You can customize any text, holograms, menus, and everything else in the /lang/ folder

**Infinity Schematics**
- Each dungeon has a list of schemes that they will use. In addition, you can choose the order of selection of these schemes, randomly or by list

**Region Settings**
- You can set up a region within the radius of the dungeon. Its name or disable it altogether

**Timer Settings**
- You can configure any time parameter available:
  - Refresh (300 def) - Frequency of occurrence or recharge
  - Wait (60 def) - Waiting time before appearance
  - Open (15 def) - After how many seconds the dungeon will be opened after the appearance/activation
  - Close (15 def) - How long will the dungeon be closed after opening

> [SETUP GUIDE](https://github.com/getplusm/Dungeons/wiki)

![Showcase Image](https://cdn.discordapp.com/attachments/810884357543165963/1130614436168745042/showcase.png)

Screenshots
- /dungeon editor preview
  ![Editor Preview](https://cdn.discordapp.com/attachments/810884357543165963/1128602812092252200/2023-07-12_13.23.49.png)
- BossBar & ActionBar notification
  ![BossBar & ActionBar](https://cdn.discordapp.com/attachments/810884357543165963/1128602812482343002/2023-07-12_13.23.08.png)
- Key preview
  ![Key Preview](https://cdn.discordapp.com/attachments/810884357543165963/1128605093021876304/2023-07-12_13.33.37.png)
- Default Schematic preview
  ![Schematic Preview](https://cdn.discordapp.com/attachments/810884357543165963/1127846224255533157/2023-07-09_00.26.53.png)

[Any questions or found an error? Come to our Discord and we will help you.](https://discord.gg/ajnPb3fdKq)
