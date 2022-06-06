# Custom Plants

This mod lets you add custom plants to the game and differs between 4 plant types:

- Bushes
- Crops
- Extendables
- Trees

## File Structure

```editorconfig
. # Pack Root
|-- assets # default minecraft assets like in resourcepacks goes here
|   `-- customplants
|       |-- blockstates # Every block requires a blockstate here pointing to models
|       |   `-- <id>.json
|       |-- lang # Every item and block requires an english (and maybe more) translation(s)
|       |   |-- en_us.lang
|       |   `-- <lang code>.lang (for each additional translation)
|       |-- models # model files. <id>_<state> is only a suggestion, it might be anything
|       |   |-- block # models for blocks
|       |   |   `-- <id>_<state>.json
|       |   |-- item # modcels for items
|       |   |   `-- <id>_<state>.json
|       `-- textures # texture files. <id>_<state> is only a suggestion, it might be anything
|           |-- blocks # textures for blocks (when an item uses a texture of a block it doesn't has to be duplicated to ../items)
|           |   `-- <id>_<state>.png
|           `-- items # textures for items (e.g. seeds)
|               `-- <id>_<state>.png
`-- customplants # plant configuration files goes here
    |-- bushes # bush config files goes here
    |   `-- <id>.json
    |-- crops # crops config files goes here
    |   `-- <id>.json
    |-- extendables # extendable config files goes here
    |   `-- <id>.json
    `--trees # tree config files and tree models goes here
       |-- <id>.json # tree config file
       `-- <id>_<version>.tree.txt # tree model
```


### Plants

- The plant ID is the name of the plant config (without `.json` of course)
- Each plant requires a `blockstate.json` file
- Each defined blockstate requires a block model
- Each block model requires a block texture
- When the plant can be picked up (can be as an item in the inventory) it requires an additional item model
- Each item model requires a texture (either in items or blocks)


### Seeds
- Each seed defined in the plant config requires an item model
- Each item model requires a texture (in items)


## Plant Config

[ReadMe](./custom_plant_v0.json.md)


## Plants

### Bushes

Plants which don't have seeds don't grow.

Examples:
- Flowers,
- Grass,
- Dead Bushes

[Example Config](./example_bush.json)


### Crops

Plants which require seeds to be planted and have n growth stages/ ages

Examples:
- Wheat
- Carrots
- Potatoes

[Example Config](./example_crops.json)


### Extendables

Plants which don't grow technically; When the plant grows, a new block is placed in the opposite direction as the defined facing<br />
(Normal plants would grow up, if the facing e.g. is Down (Ceiling) the plant would grow to the floor)

Examples:
- Sugar Cane
- Bamboo
- Tall Grass

[Example Config](./custom_extendable.json)


### Trees

Well... trees. A plant, planted by a sapling which grows to a tree.

Examples:
- Oak Tree
- Birch Tree
- ...

[Example Config (Sapling)](./custom_tree.json)
[Example Config (Tree)](./custom_tree_0.tree.txt)

#### Configs

A tree consists of at least 2 configs:
- One for the sapling which defines the behaviour of the tree and
- multiple configs which defines the possible shapes of the grown tree. Each tree config defines another shape.