# CustomPlants

_A Minecraft (Java Edition / Forge) mod which lets admins add configurable plants to the game_


## Requirements

- Required on both client and server
- No dependencies (Mods, Libs, ...)
- Written with Java 8
- Written for [Forge 1.12.2 - 14.23.5.2860](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html)


## Features

NullPointerException :P


## Planned Features

- Plant options:
  - type: (vine-like, flower-like, tree-like)
  - breakable by water: (Y/N)
  - harvest tools: (list of item names e.g. `minecraft:shears`)
  - drops: (list of objects containing drop chance and item name)
  - crafting: (list of crafting recipes)
  - needs seeds to grow: (Y/N) - disables generation - _place seeds on farmland (or maybe different, configurable, material?) to grow like wheat or place the plant itself like potatoes_
  - can be grown in [Garden Cloche](https://ftb.fandom.com/wiki/Garden_Cloche): (Y/N) 
- Plant generation options:
  - dimension whitelist (list of dimension IDs)
  - dimension blacklist (list of dimension IDs)
  - dimension whitelist (list of biomes)
  - dimension blacklist (list of biomes)
  - loot - _currently, I have no idea how loot tables are working so this may be a feature for later versions_
- Mod support
  - [Immersive Engineering/ Garden Cloche](https://ftb.fandom.com/wiki/Garden_Cloche)


## Use in Modpacks

You can use this mod in modpacks under the following conditions:
- If the modpack does not cost money or the like (donations are fine of course).
- When you give credits to the contributors (e.g. a [link](/graphs/contributors) somewhere in the modpacks description or list of mods).
- When the mod is not downloaded from the official repository directly to the players machine you have to inform the players on the modpack page about it.
  A general mention (without naming this mod explicitly) is enough.<br />
  _Downloading code or programs from official sources directly to the end user's computer makes it more difficult for manipulated code to spread, thus increasing security - you wouldn't want to download Microsoft Windows from a questionable .to website, would you?_


## Contributing

First of all, any kind of contribution is welcome. However, there are two small guidelines:

This mod is intended to add plants as freely configurable as possible. No more, no less.
Features that other mods cover better, these mods should take care of.
To give an example; To ensure that only the owner can harvest the plant, you should use a mod that allows players to claim a chunk.

Before making a pull request, please document your code - Don't overdo it but add at least some javadoc comments.
It can be a pain to read undocumented code from other people. In the worst case, historically grown code. \*cough\* Forge &lt; 1.16 \*cough\*
