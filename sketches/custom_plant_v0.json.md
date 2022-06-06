# Plant Configuration File
_v0_

## Generic Plants

```json
{
  "_file": {
    "format": "<int/enum[0]>",
    "version": "<string>"
  },
  "behavior": {
    "active": "<array<object/Behavior>>",
    "constant": "<array<object/Behavior>>",
    "inactive": "<array<object/Behavior>>"
  },
  "plant": {
    "bounding_box": "<object/AABB>",
    "bounding_boxes": "<array<object/AABB>>",
    "burn_time": "<int[0~]",
    "color": "<int/enum[0~63]>",
    "drops": {
      "name": "<string/ResourceLocation>",
      "amount": "<int[0~64]>"
    },
    "facings": "<array<int/enum[0~5]>>",
    "ore_dict": "<string>",
    "type": "<int/enum[-1~6]>",
    "soils": "<array<string/ResourceLocation>>",
    "soils_allowed": "<boolean>",
    "soils_enabled": "<boolean>"
  },
  "type": "<object>"
}
```
- `_file.format`: The format of this file. ALWAYS 0!
- `_file.version`: The version of this file/ plant. Choose what you want, this value isn't used at the moment.
- `behavior`: The behavior of the plant (actions and features).
- `behavior.active`: The behavior of the plant when it is active.
- `behavior.constant`: The constant behavior of the plant (values might get overridden when the plant is (in)active).
- `behavior.inactive`: The behavior of the plant when it is inactive.
- `behavior.constant`: Constantly active behavior.
- `plant.bounding_box`: The axis aligned bounding box of the plant.<br />
  Will be ignored if the plant grows.
- `plant.bounding_boxes`: The axis aligned bounding boxes for each growth stage/ age of the plant.<br />
  Will be ignored if the plant doesn't grow.
- `type.burn_time`: The time in ticks the plant as item burns. 0 means that the item is no fuel.
- `color`: The color of the plant (used for maps). <TODO> ADD color object!
- `plant.drops.name`: The item to be dropped when the plant breaks.
- `plant.drops.amount`: The amount of items to be dropped when the plant breaks.
- `plant.facings`: The facings where the plant is allowed to be placed on;
  - 0 Down (Ceiling)
  - 1 Up (Floor - Default)
  - 2 North (Side)
  - 3 South (Side)
  - 4 West (Side)
  - 5 East (Side)
- `plant.ore_dict`: The ore dictionary name of the plant.
- `plant.type`: The type of the plant;<br />
  Requires `plant.soils_enabled` to be `false`;
  - -1 CustomPlant (Suggested when using `plant.soils`)
  - 0 Plains (Default; Plants growing in forests, ... - e.g. flowers, bushes, grass)
  - 1 Desert (Plants growing in deserts - e.g. cactus, dead bushes)
  - 2 Beach (Plants growing at the beach - e.g. sugar canes)
  - 3 Cave (Plants growing in caves - e.g. mushrooms)
  - 4 Water (Plants growing underwater)
  - 5 Nether (Plants growing in the nether dimension)
  - 6 Crop (Plants growing on farmland - e.g. wheat, carrots, ...)
- `plant.soils`: A list of soils where the plant can or can't be planted on.<br />
  Requires `plant.soils_enabled` to be `true`.
- `plant.soils_allowed`: Whether `plant.soils` is an allow- or denylist i.e. the plant can or can't be planted on one of the blocks.<br />
  Requires `plant.soils_enabled` to be `true`.
- `plant.soils_enabled`: Whether to use the allow-/ denylist system or the default vanilla system of plant types.
- `type`: Config options only applicable for some plant types.


## Specific Plant Types
```json5
{
  // ...
  "type": {
    "can_use_bonemeal": "<boolean>" // Crops, Overlays, Extendables, Saplings
  }
}
```
}


## Datatypes

### Datatypes (Common)

#### `<object/AABB>`
_AxisAlignedBoundingBox_

```json
{
  "x1": "<double>",
  "x2": "<double>",
  "y1": "<double>",
  "y2": "<double>",
  "z1": "<double>",
  "z2": "<double>"
}
```
The coordinates of the corners of the cuboid bounding box ('hit box').


#### `<string/ResourceLocation>`

A resource location is similar to the stuff you enter in commands (except the meta data):
`/give @p <mod_id>:<item_id> <amount> <nbt>`
```json5
"<item_id>"                 // partly defined, use defaults: customplants:<item_id>:0
"<item_id>:<nbt>"           // partly defined, use defaults: customplants:<item_id>:<nbt>
"<mod_id>:<item_id>"        // partly defined, use defaults: <mod_id>:<item_id>:0
"<mod_id>:<item_id>:<nbt>"  // fully defined
```


#### `<object/Behavior>`

```json
{
  "actions": "<object/Events>",
  "features": "<object/Features>"
}
```
The features/ actions are only executed if the position of this object in the array matches the age of the plant.


#### `<object/Effect>`

```json5
{
  "id": "<int>",                  // Required
  "duration": "<int[0~1000000]>", // Default 30 (= 1s)
  "amplifier": "<int[0~255]>",    // Default 0
  "ambient": "<boolean>",         // Default 0
  "particles": "<boolean>"        // Default true
}
```
- When using a mod's effect ID, make sure to require the mod in the packs `pack.mcmeta` file!
- Duration is in game ticks
- When the effect is added by a feature, the value should be `true`, otherwise `false` to not confuse players.<br />
  When true, a blue border is drawn around the effect card left to the inventory.
  This indicates, that the effect came from a beacon and plants with features are just like beacons, I guess.
- When the effect is added by an action, the value should be `false` to indicate that this effect will be gone after the time run out.


### Datatypes (Actions)


#### `<object/Events>`

```json
{
  "clicked": "<object/Action>",
  "collided_entity": "<object/Actions>",
  "collided_npc":" <object/Actions>",
  "collided_player": "<object/Actions>",
  "destroyed": "<object/Actions>",
  "grown": "<object/Actions>"
}
```
- `clicked`: When the plant gets (de)activated by right-clicking.
- `collided_entity`: When any entity (Players, NPCs, Items, ...) collides with the plant.
- `collided_npc`: When an NPC (village, pig, cow, ...) collides with the plant.
- `collided_player`: When a player collides with the plant.
- `destroyed`: When the plant gets destroyed.
- `grown`: When the plant grows.<br />
  Will be ignored if the plant doesn't grow. 


#### `<object/Actions>`

```json5
{
  "air": "<int[-300~300]>",               // Default 0
  "effects": "<array<object/Effect>>",    // Default []
  "experience_amount": "<int>",           // Default 0
  "experience_is_in_levels": "<boolean>", // Default false
  "health_amount": "<float>",             // Default 0
  "hunger_amount": "<int[-20~20]>",       // Default 0
  "hunger_exhaustion": "<int>",           // Default 0  TODO 2022-06-04
  "hunger_saturation": "<int>",           // Default 0  TODO 2022-06-04
  "target": "<string/TargetSelector>"     // Required
}
```
- `air`: The amount air to add/ remove to/ from the target (Every entity).
- `effects`: Effects to add to the target (Living entities; NPC, Player).
- `experience_amount`: Experience to add to the target.
- `experience_is_in_levels`: Whether `experience_amount` is in XP or levels.
- `health_amount`: Amount of health to add to the target.
- `hunger_amount`:  Amount of hunger (the meat clubs) to add to the target.
- `hunger_exhaustion`: Invisible value; Amount of exhaustion to add to the target.
- `hunger_saturation`: Invisible value; Amount of saturation to add to the target.
- `target`: The target (`@<a|e|p|s>`, player name, anything. Like in Commands).


### Datatypes (Features)


#### `<object/Features>`

```json5
{
  "can_sustain_leaves": "<boolean>",                                          // Default false
  "effects": "<array<array/tuple<<string/TargetSelector>,<object/Effect>>>",  // Default []
  "enchant_power_bonus": "<float>",                                           // Default 0
  "is_ladder": "<boolean>",                                                   // Default false
  "is_solid": "<boolean>",                                                    // Default false
  "is_web": "<boolean>"                                                       // Default false
}
```
- `can_sustain_leaves`: **\[WIP\]** I have literally NO idea, what this does. I expected this prevents leaves from decaying but this happens absolutely random. I have no idea. May get removed.
- `effects`: Effects to add to their targets.
- `enchant_power_bonus`: Lets the plant act as a bookshelf for enchantment tables.<br />
  Bookshelves have a bonus of `1.0`.
- `is_ladder`: Lets the plant act as a ladder.
- `is_solid`: Makes the plant solid block (i.e. unpassable).<br />
   Note that `is_ladder` and `is_web` won't work anymore.
- `is_web`: Lets the plant act as a spider web.

Note that feature values defined in `constant` will get overridden when a value is defined in the other states `active` and `inactive`.